package main

import (
	"encoding/json"
	"net/http"
)

func AddToHousehold(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	var familyMember User
	if err := json.NewDecoder(r.Body).Decode(&familyMember); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	var familyMemberFound bool = false
	db.QueryRow("SELECT COUNT(*) > 0 AS found FROM users WHERE users_add_numerics_0 = 0 AND users_id = ? AND users_add_strings_0 = ?", familyMember.Userid, familyMember.Username).Scan(&familyMemberFound)

	if !familyMemberFound {
		http.Error(w, "User not found, or already added to household", http.StatusNotFound)
		return
	}

	var householdId int
	db.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)

	_, err := db.Exec(`UPDATE users SET users_add_numerics_0 = ? WHERE users_add_numerics_0 = 0 AND users_id = ? AND users_add_strings_0 = ?`,
		householdId, familyMember.Userid, familyMember.Username)

	if err != nil {
		http.Error(w, "Failed to add user to household "+err.Error(), http.StatusInternalServerError)
		return
	}

	rows, err := db.Query(`SELECT 
				neighbourhood_household_users_add_numerics_0 AS neighbourhoodid,
				neighbourhood_household_users_add_numerics_3 AS access
	 		FROM 
				neighbourhood_household_users
			WHERE 
				neighbourhood_household_users_add_numerics_1 = ? 
			AND 
				neighbourhood_household_users_add_numerics_2 = ?`, householdId, userId)

	if err != nil {
		http.Error(w, "Failed to add user to neighbourhoods "+err.Error(), http.StatusInternalServerError)
		return
	}

	defer rows.Close()

	var neighbourhoodId int
	var access int
	for rows.Next() {
		err := rows.Scan(&neighbourhoodId, &access)
		if err != nil {
			http.Error(w, "Failed to add user to neighbourhoods "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec(`INSERT INTO neighbourhood_household_users (
							neighbourhood_household_users_add_numerics_0, 
							neighbourhood_household_users_add_numerics_1, 
							neighbourhood_household_users_add_numerics_2, 
							neighbourhood_household_users_add_numerics_3,
							neighbourhood_household_users_add_numerics_4) 
						VALUES (?,?,?,?,?)`,
			neighbourhoodId, householdId, familyMember.Userid, access-1, userId)
		if err != nil {
			http.Error(w, "Failed to add user to neighbourhoods "+err.Error(), http.StatusInternalServerError)
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

func UpdateHousehold(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	var household Household
	if err := json.NewDecoder(r.Body).Decode(&household); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	var householdId int
	db.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
	if householdId > 0 {
		// Update the household in the database
		_, err := db.Exec(`UPDATE households SET households_titlu_EN = ?, households_add_strings_0 = ?, households_text_EN = ? WHERE households_id = ? AND households_add_numerics_0 = ?`,
			household.Name, household.Address, household.About, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update household "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		//Insert the household into the database
		insertResult, err := db.Exec(`INSERT INTO households (households_add_numerics_0, households_titlu_EN, households_add_strings_0, households_text_EN, households_pic) VALUES  (?,?,?,?,'')`,
			userId, household.Name, household.Address, household.About)

		if err != nil {
			http.Error(w, "Failed to insert household "+err.Error(), http.StatusInternalServerError)
			return
		}

		householdId, err := insertResult.LastInsertId()

		if err != nil {
			http.Error(w, "Failed to get inserted household "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec(`UPDATE users SET users_add_numerics_0 = ? WHERE users_id = ?`, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update user with household "+err.Error(), http.StatusInternalServerError)
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

func ResetHouseholdLocation(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Invalid request method "+r.Method, http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	_, err := db.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
	}

	_, err = db.Exec("UPDATE households SET households_add_numerics_1 = 0,  households_add_numerics_2 = 0 WHERE households_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear household location"+err.Error(), http.StatusInternalServerError)
	}

	//return success
	w.WriteHeader(http.StatusOK)
}

func AcceptHouseholdLocation(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method "+r.Method, http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	gpsPayloads, err := RetrieveHeatmap(userId, true)

	if err != nil {
		http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
		return
	}

	candidate, err := findLargestCluserLocation(gpsPayloads)
	if err != nil {
		http.Error(w, "Failed to get household candidate "+err.Error(), http.StatusInternalServerError)
		return
	}

	_, err = db.Exec("UPDATE households SET households_add_numerics_1 = ?,  households_add_numerics_2 = ? WHERE households_add_numerics_0 = ?",
		*candidate.Latitude*float64(gpsPrecisionFactor),
		*candidate.Longitude*float64(gpsPrecisionFactor),
		userId)

	if err != nil {
		http.Error(w, "Failed to store household location "+err.Error(), http.StatusInternalServerError)
	}

	_, err = db.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(candidate)
}
