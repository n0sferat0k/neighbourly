package onboarding

import (
	"api/entity"
	"api/utility"
	"context"
	"database/sql"
	"encoding/json"
	"net/http"
	"time"
)

func RegisterUser(w http.ResponseWriter, r *http.Request) {
	var user entity.User

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequirePayload(ctx, w, r, &user)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//********************************************************VALIDATION - phone number format
	if !utility.ValidatePhoneNumber(*user.Phone) {
		http.Error(w, "Invalid phone number", http.StatusBadRequest)
		return
	}
	//********************************************************VALIDATION - email format
	if !utility.ValidateEmail(*user.Email) {
		http.Error(w, "Invalid email", http.StatusBadRequest)
		return
	}

	//********************************************************VALIDATION - unique usrname
	var existingUserId string
	err := utility.DB.QueryRow("SELECT users_id FROM users WHERE users_add_strings_0 = ? LIMIT 1", user.Username).Scan(&existingUserId)
	if err == sql.ErrNoRows {
		// Hash the password
		hashedPassword, err := utility.HashPassword(*user.Password)
		if err != nil {
			http.Error(w, "Failed to hash password", http.StatusInternalServerError)
			return
		}
		*user.Password = ""

		var now = time.Now()
		// Insert the new user into the database
		insertResult, err := utility.DB.Exec(`INSERT INTO users (
			users_add_strings_0,
			users_add_strings_1, 
			users_titlu_EN, 
			users_add_strings_3, 
			users_add_strings_2,
			users_text_EN,
			users_pic,
			users_data
			users_add_numerics_0) 
		VALUES (?, ?, ?, ?, ?, "", "", ?, -1)`,
			user.Username,
			hashedPassword,
			user.Fullname,
			user.Email,
			user.Phone,
			now.Unix())
		if err != nil {
			http.Error(w, "Failed to register user "+err.Error(), http.StatusInternalServerError)
			return
		}

		// Retain the user ID
		insertedID, err := insertResult.LastInsertId()
		if err != nil {
			http.Error(w, "Failed to register user"+err.Error(), http.StatusInternalServerError)
			return
		}
		user.Userid = &insertedID

		//create an auth token
		authToken, err := utility.GenerateAuthToken()
		if err != nil {
			http.Error(w, "Failed to generate token", http.StatusInternalServerError)
			return
		}
		user.Authtoken = &authToken
		var tokenExpiration = time.Now().Add(time.Hour * 24 * 7) // 1 week

		// Insert the new token into the database
		_, err = utility.DB.Exec("INSERT INTO tokens (tokens_add_numerics_0 , tokens_titlu_EN, tokens_data) VALUES (?, ?, ?)",
			user.Userid, user.Authtoken, tokenExpiration.Unix())
		if err != nil {
			http.Error(w, "Failed to add token "+*user.Authtoken+" -- "+err.Error(), http.StatusInternalServerError)
			return
		}

		user.Household = nil
		user.Neighbourhoods = nil

		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(user)
	} else if err != nil {
		http.Error(w, "Database error", http.StatusInternalServerError)
	} else {
		http.Error(w, "Username already exists", http.StatusConflict)
	}
}
