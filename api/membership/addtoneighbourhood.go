package membership

import (
	"api/entity"
	"api/utility"
	"encoding/json"
	"math"
	"net/http"
)

func AddToNeighbourhood(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)

	var addRequest entity.AddToNeighbourhoodRequest
	if err := json.NewDecoder(r.Body).Decode(&addRequest); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	var householdid int64
	err := utility.DB.QueryRow(`SELECT 
							users_add_numerics_0 AS householdid 
						FROM 
							users 
						WHERE  
							users_id = ? 
						AND 
							users_add_strings_0 = ?`,
		addRequest.Userid, addRequest.Username).Scan(&householdid)

	if err != nil || householdid == 0 {
		http.Error(w, "User not found or not in household "+err.Error(), http.StatusNotFound)
		return
	}

	var access int64
	err = utility.DB.QueryRow(`SELECT 
					neighbourhood_household_users_add_numerics_3 AS access
				FROM 
					neighbourhood_household_users 
				WHERE  
					neighbourhood_household_users_add_numerics_0 = ? 
				AND 
					neighbourhood_household_users_add_numerics_2 = ?`,
		addRequest.Neighbourhoodid, userId).Scan(&access)

	if err != nil {
		http.Error(w, "No access to requested neighbourhood "+err.Error(), http.StatusInternalServerError)
		return
	}

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
			neighbourhood_household_users_add_numerics_0,
			neighbourhood_household_users_add_numerics_1,
			neighbourhood_household_users_add_numerics_2,
			neighbourhood_household_users_add_numerics_3,
			neighbourhood_household_users_add_numerics_4
		) VALUES (?,?,?,?,?)`, addRequest.Neighbourhoodid, householdid, householdUserId, householdUserAccess, userId)

		if err != nil {
			http.Error(w, "Failed to insert neighbourhood household user "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
