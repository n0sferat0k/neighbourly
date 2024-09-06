package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"net/http"
	"time"
)

func LogoutUser(w http.ResponseWriter, r *http.Request) {
	//********************************************************VALIDATION - http method
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	logoutAll := r.URL.Query().Get("logoutAll")

	if logoutAll == "true" {
		_, err := db.Exec("DELETE FROM tokens WHERE tokens_add_numerics_0 = ?", userId)
		if err != nil {
			http.Error(w, "Failed to remove token "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		var token string = getToken(r)
		// delete the token from the database
		_, err := db.Exec("DELETE FROM tokens WHERE tokens_titlu_EN = ?", token)
		if err != nil {
			http.Error(w, "Failed to remove tokens "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	w.WriteHeader(http.StatusAccepted)
	fmt.Fprintln(w, "Logout successful")
}

func LoginUser(w http.ResponseWriter, r *http.Request) {
	var err error

	//********************************************************VALIDATION - http method
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}
	//********************************************************VALIDATION - user payload
	var user User
	if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	var existingUser User
	var existingHousehold Household
	err = db.QueryRow(`SELECT 	U.users_id,
								U.users_text_EN,
								U.users_titlu_EN,
								U.users_pic,
								U.users_add_strings_0,
								U.users_add_strings_1,
								U.users_add_strings_2,
								U.users_add_strings_3,

								H.households_id,
								H.households_titlu_EN,
								H.households_text_EN AS Householdabout,
								H.households_pic AS HouseholdImageURL,
								H.households_add_numerics_0 AS HeadID,
								H.households_add_numerics_1 / 10000000 AS Latitude,
								H.households_add_numerics_2 / 10000000 AS Longitude,
								H.households_add_strings_0 AS Address

								FROM 
									users U 
								LEFT JOIN 
									households H 
								ON 
									users_add_numerics_0 = households_id
								WHERE 
									U.users_add_strings_0 = ?
								LIMIT 1`,
		user.Username,
	).Scan(
		&existingUser.Userid,
		&existingUser.Userabout,
		&existingUser.Fullname,
		&existingUser.ImageURL,
		&existingUser.Username,
		&existingUser.Password,
		&existingUser.Phone,
		&existingUser.Email,

		&existingHousehold.Householdid,
		&existingHousehold.Name,
		&existingHousehold.About,
		&existingHousehold.ImageURL,
		&existingHousehold.HeadID,
		&existingHousehold.Latitude,
		&existingHousehold.Longitude,
		&existingHousehold.Address,
	)

	if err != nil {
		if err == sql.ErrNoRows {
			http.Error(w, "Username or Password incorrect", http.StatusUnauthorized)
			return
		} else {
			http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	if !checkPasswordHash(user.Password, existingUser.Password) {
		http.Error(w, "Username or Password incorrect", http.StatusUnauthorized)
		return
	}

	if existingHousehold.Householdid != nil {
		existingUser.Household = &existingHousehold
	}

	//create an auth token
	authToken, err := generateAuthToken()
	if err != nil {
		http.Error(w, "Failed to generate token", http.StatusInternalServerError)
		return
	}
	existingUser.Authtoken = authToken
	var tokenExpiration = time.Now().Add(time.Hour * 24 * 7) // 1 week

	// Insert the new token into the database
	_, err = db.Exec("INSERT INTO tokens (tokens_add_numerics_0 , tokens_titlu_EN, tokens_data) VALUES (?, ?, ?)",
		existingUser.Userid, existingUser.Authtoken, tokenExpiration.Unix())
	if err != nil {
		http.Error(w, "Failed to add token "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}
