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

	var userId string
	var passHash string

	err = db.QueryRow(`SELECT 	U.users_id,								
								U.users_add_strings_1								
								FROM 
									users U 								
								WHERE 
									U.users_add_strings_0 = ?
								LIMIT 1`,
		user.Username,
	).Scan(
		&userId,
		&passHash,
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

	if !checkPasswordHash(*user.Password, passHash) {
		http.Error(w, "Username or Password incorrect", http.StatusUnauthorized)
		return
	}

	//create an auth token
	authToken, err := generateAuthToken()
	if err != nil {
		http.Error(w, "Failed to generate token", http.StatusInternalServerError)
		return
	}

	var tokenExpiration = time.Now().Add(time.Hour * 24 * 7) // 1 week

	// Insert the new token into the database
	_, err = db.Exec("INSERT INTO tokens (tokens_add_numerics_0 , tokens_titlu_EN, tokens_data) VALUES (?, ?, ?)",
		userId, authToken, tokenExpiration.Unix())
	if err != nil {
		http.Error(w, "Failed to add token "+err.Error(), http.StatusInternalServerError)
		return
	}

	existingUser, err := RetreiveSessionUserData(userId)
	if err != nil {
		http.Error(w, "Failed to get user info "+err.Error(), http.StatusInternalServerError)
		return
	}
	existingUser.Authtoken = &authToken

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}
