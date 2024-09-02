package main

import (
	"database/sql"
	"encoding/json"
	"net/http"
	"time"
)

func RegisterUser(w http.ResponseWriter, r *http.Request) {
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
	//********************************************************VALIDATION - phone number format
	if !validatePhoneNumber(user.Phone) {
		http.Error(w, "Invalid phone number", http.StatusBadRequest)
		return
	}
	//********************************************************VALIDATION - email format
	if !validateEmail(user.Email) {
		http.Error(w, "Invalid email", http.StatusBadRequest)
		return
	}

	//********************************************************VALIDATION - unique usrname
	var existingUserId string
	err := db.QueryRow("SELECT users_id FROM users WHERE users_add_strings_0 = ? LIMIT 1", user.Username).Scan(&existingUserId)
	if err == sql.ErrNoRows {
		// Hash the password
		hashedPassword, err := hashPassword(user.Password)
		if err != nil {
			http.Error(w, "Failed to hash password", http.StatusInternalServerError)
			return
		}
		user.Password = ""

		// Insert the new user into the database
		insertResult, err := db.Exec("INSERT INTO users (users_add_strings_0, users_add_strings_1, users_titlu_EN, users_add_strings_3, users_add_strings_2) VALUES (?, ?, ?, ?, ?)",
			user.Username, hashedPassword, user.Fullname, user.Email, user.Phone)
		if err != nil {
			http.Error(w, "Failed to register user "+err.Error(), http.StatusInternalServerError)
			return
		}

		// Retain the user ID
		insertedID, err := insertResult.LastInsertId()
		if err != nil {
			http.Error(w, "Failed to register user", http.StatusInternalServerError)
			return
		}
		user.Userid = insertedID

		//create an auth token
		authToken, err := generateAuthToken()
		if err != nil {
			http.Error(w, "Failed to generate token", http.StatusInternalServerError)
			return
		}
		user.Authtoken = authToken
		var tokenExpiration = time.Now().Add(time.Hour * 24 * 7) // 1 week

		// Insert the new token into the database
		_, err = db.Exec("INSERT INTO tokens (tokens_add_numerics_0 , tokens_titlu_EN, tokens_data) VALUES (?, ?, ?)",
			user.Userid, user.Authtoken, tokenExpiration.Unix())
		if err != nil {
			http.Error(w, "Failed to register user", http.StatusInternalServerError)
			return
		}

		user.Household = nil

		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(user)
	} else if err != nil {
		http.Error(w, "Database error", http.StatusInternalServerError)
	} else {
		http.Error(w, "Username already exists", http.StatusConflict)
	}
}
