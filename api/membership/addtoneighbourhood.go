package membership

import (
	"api/entity"
	"api/utility"
	"context"
	"database/sql"
	"encoding/json"
	"math"
	"net/http"
)

func AddToNeighbourhood(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var addRequest entity.AddToNeighbourhoodRequest

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &addRequest)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//************************************ get the household of the user we are adding
	var householdid int64
	var longitude float64
	var latitude float64

	err := utility.DB.QueryRow(`SELECT users_add_numerics_0, households_add_numerics_1 / ?, households_add_numerics_2/ ?
								FROM users LEFT JOIN households ON households.households_id = users.users_add_numerics_0
								WHERE users_id = ? AND users_add_strings_0 = ?`,
		utility.GpsPrecisionFactor, utility.GpsPrecisionFactor, addRequest.Userid, addRequest.Username).Scan(&householdid, &latitude, &longitude)
	if err != nil || householdid == 0 {
		http.Error(w, "User not found or not in household "+err.Error(), http.StatusNotFound)
		return
	}

	//************************************ make sure the household is not already in the neighbourhood
	var exists bool
	err = utility.DB.QueryRow("SELECT 1 FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_0 = ? AND neighbourhood_household_users_add_numerics_1 = ? LIMIT 1", addRequest.Neighbourhoodid, householdid).Scan(&exists)
	if err != nil && err != sql.ErrNoRows {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}
	if exists {
		http.Error(w, "Household already in neighbourhood", http.StatusConflict)
		return
	}

	//************************************ get our own acces level to the target neighbourhood as well as the neighbourhood geofence
	var access int64
	var geofenceStr string

	err = utility.DB.QueryRow(`SELECT neighbourhood_household_users_add_numerics_3,	neighbourhoods_text_EN AS geofence	
								FROM neighbourhood_household_users LEFT JOIN neighbourhoods ON neighbourhoods.neighbourhoods_id = neighbourhood_household_users.neighbourhood_household_users_add_numerics_0
								WHERE neighbourhood_household_users_add_numerics_0 = ? AND neighbourhood_household_users_add_numerics_2 = ?`,
		addRequest.Neighbourhoodid, userId).Scan(&access, &geofenceStr)
	if err != nil {
		http.Error(w, "No access to requested neighbourhood "+err.Error(), http.StatusInternalServerError)
		return
	}
	var geofence [][2]float64
	json.Unmarshal([]byte(geofenceStr), &geofence)

	//************************************ make sure the household falls within the neighbourhood geofence
	if !utility.PointInPolygon(longitude, latitude, geofence) {
		http.Error(w, "Household is not within neighbourhood", http.StatusForbidden)
		return
	}

	//************************************ iterate over the users of the household and add them to the neighbourhood
	rows, err := utility.DB.Query(`SELECT users_id FROM  users WHERE users_add_numerics_0 = ?`, householdid)
	if err != nil {
		http.Error(w, "Failed to get users of household"+err.Error(), http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var householdUserId int64
	var householdUserAccess int64
	for rows.Next() {
		err := rows.Scan(&householdUserId)
		if err != nil {
			http.Error(w, "Failed to get users of household"+err.Error(), http.StatusInternalServerError)
			return
		}

		if value, exists := addRequest.Accs[householdUserId]; exists {
			householdUserAccess = int64(math.Min(float64(value), float64(access-1)))
		} else {
			householdUserAccess = access - 1
		}

		_, err = utility.DB.Exec(`INSERT INTO neighbourhood_household_users (
			neighbourhood_household_users_data,
			neighbourhood_household_users_add_numerics_0,
			neighbourhood_household_users_add_numerics_1,
			neighbourhood_household_users_add_numerics_2,
			neighbourhood_household_users_add_numerics_3,
			neighbourhood_household_users_add_numerics_4
		) VALUES (UNIX_TIMESTAMP(),?,?,?,?,?)`, addRequest.Neighbourhoodid, householdid, householdUserId, householdUserAccess, userId)

		if err != nil {
			http.Error(w, "Failed to insert neighbourhood household user "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
