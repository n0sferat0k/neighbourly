package main

import (
	"encoding/json"
	"net/http"
)

func UpdateNeighbourhood(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	var neighbourhood Neighbourhood
	if err := json.NewDecoder(r.Body).Decode(&neighbourhood); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	var geofence [][2]float64
	json.Unmarshal([]byte(*neighbourhood.Geofence), &geofence)

	spread, center := findMaxSpreadAndCenter(geofence)
	if spread > MaxNeighbourhoodSizeMeters {
		http.Error(w, "Neighbourhood too large", http.StatusBadRequest)
		return
	}

	var householdId int
	db.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)

	if householdId <= 0 {
		http.Error(w, "User does not have a household", http.StatusBadRequest)
		return
	}

	if neighbourhood.Neighbourhoodid == nil {
		//adding new newighbourhood
		insertResult, err := db.Exec(`INSERT INTO neighbourhoods (neighbourhoods_titlu_EN, neighbourhoods_text_EN, neighbourhoods_add_numerics_0, neighbourhoods_add_numerics_1) VALUES (?,?,?,?)`,
			neighbourhood.Name, neighbourhood.Geofence, center[1]*gpsPrecisionFactor, center[0]*gpsPrecisionFactor)
		if err != nil {
			http.Error(w, "Failed to insert neighbourhood "+err.Error(), http.StatusInternalServerError)
			return
		}

		neighbourhoodId, err := insertResult.LastInsertId()
		if err != nil {
			http.Error(w, "Failed to insert neighbourhood "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec(`INSERT INTO neighbourhood_household_users (
			neighbourhood_household_users_add_numerics_0,
			neighbourhood_household_users_add_numerics_1,
			neighbourhood_household_users_add_numerics_2,
			neighbourhood_household_users_add_numerics_3,
			neighbourhood_household_users_add_numerics_4
		) VALUES (?,?,?,500,-1)`, neighbourhoodId, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to insert neighbourhood household user "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		//updating existing neighbourhood

		var accLevel int
		err := db.QueryRow(`SELECT 
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

		_, err = db.Exec(`UPDATE neighbourhoods SET neighbourhoods_titlu_EN = ?, neighbourhoods_text_EN = ?, neighbourhoods_add_numerics_0 = ?, neighbourhoods_add_numerics_1 = ? WHERE neighbourhoods_id = ?`,
			neighbourhood.Name, neighbourhood.Geofence, center[1]*gpsPrecisionFactor, center[0]*gpsPrecisionFactor, *neighbourhood.Neighbourhoodid)

		if err != nil {
			http.Error(w, "Failed to update neighbourhood "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	existingUser, err := RetreiveSessionUserData(userId)
	if err != nil {
		http.Error(w, "Failed to get user info for "+userId+":"+err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}
