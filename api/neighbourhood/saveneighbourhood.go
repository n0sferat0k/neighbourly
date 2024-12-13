package neighbourhood

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func UpdateNeighbourhood(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var neighbourhood entity.Neighbourhood

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &neighbourhood)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	var geofence [][2]float64
	json.Unmarshal([]byte(*neighbourhood.Geofence), &geofence)

	spread, center := utility.FindMaxSpreadAndCenter(geofence)
	if spread > utility.MaxNeighbourhoodSizeMeters {
		http.Error(w, "Neighbourhood too large", http.StatusBadRequest)
		return
	}

	var householdId int
	utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)

	if householdId <= 0 {
		http.Error(w, "User does not have a household", http.StatusBadRequest)
		return
	}

	if neighbourhood.Neighbourhoodid == nil {
		//adding new newighbourhood
		insertResult, err := utility.DB.Exec(`INSERT INTO neighbourhoods (neighbourhoods_titlu_EN, neighbourhoods_text_EN, neighbourhoods_add_numerics_0, neighbourhoods_add_numerics_1) VALUES (?,?,?,?)`,
			neighbourhood.Name, neighbourhood.Geofence, center[1]*utility.GpsPrecisionFactor, center[0]*utility.GpsPrecisionFactor)
		if err != nil {
			http.Error(w, "Failed to insert neighbourhood "+err.Error(), http.StatusInternalServerError)
			return
		}

		neighbourhoodId, err := insertResult.LastInsertId()
		if err != nil {
			http.Error(w, "Failed to insert neighbourhood "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = utility.DB.Exec(`INSERT INTO neighbourhood_household_users (
			neighbourhood_household_users_data,
			neighbourhood_household_users_add_numerics_0,
			neighbourhood_household_users_add_numerics_1,
			neighbourhood_household_users_add_numerics_2,
			neighbourhood_household_users_add_numerics_3,
			neighbourhood_household_users_add_numerics_4
		) VALUES (UNIX_TIMESTAMP(),?,?,?,500,-1)`, neighbourhoodId, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to insert neighbourhood household user "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		//updating existing neighbourhood

		var accLevel int
		err := utility.DB.QueryRow(`SELECT 
						neighbourhood_household_users_add_numerics_3 
					FROM 
						neighbourhood_household_users 
					WHERE 
						neighbourhood_household_users_add_numerics_0 = ?
					AND
						neighbourhood_household_users_add_numerics_2 = ?`, neighbourhood.Neighbourhoodid, userId).Scan(&accLevel)

		if err != nil || accLevel < 500 {
			http.Error(w, "User does not have enough ACC", http.StatusBadRequest)
			return
		}

		_, err = utility.DB.Exec(`UPDATE neighbourhoods SET neighbourhoods_titlu_EN = ?, neighbourhoods_text_EN = ?, neighbourhoods_add_numerics_0 = ?, neighbourhoods_add_numerics_1 = ? WHERE neighbourhoods_id = ?`,
			neighbourhood.Name,
			neighbourhood.Geofence, center[1]*utility.GpsPrecisionFactor, center[0]*utility.GpsPrecisionFactor, *neighbourhood.Neighbourhoodid)

		if err != nil {
			http.Error(w, "Failed to update neighbourhood "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	utility.ReturnSelfSession(userId, w, r, nil)
}
