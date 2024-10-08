package onboarding

import (
	"api/entity"
	"api/utility"
	"context"
	"database/sql"
	"net/http"
	"time"
)

func LoginUser(w http.ResponseWriter, r *http.Request) {
	var user entity.User

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequirePayload(ctx, w, r, &user)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	var userId string
	var passHash string

	err := utility.DB.QueryRow(`SELECT 	U.users_id,								
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
			http.Error(w, "Username or Password incorrect"+err.Error(), http.StatusUnauthorized)
			return
		} else {
			http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	if !utility.CheckPasswordHash(*user.Password, passHash) {
		http.Error(w, "Username or Password incorrect", http.StatusUnauthorized)
		return
	}

	//create an auth token
	authToken, err := utility.GenerateAuthToken()
	if err != nil {
		http.Error(w, "Failed to generate token", http.StatusInternalServerError)
		return
	}

	var tokenExpiration = time.Now().Add(time.Hour * 24 * 7) // 1 week

	// Insert the new token into the database
	_, err = utility.DB.Exec("INSERT INTO tokens (tokens_add_numerics_0 , tokens_titlu_EN, tokens_data) VALUES (?, ?, ?)",
		userId, authToken, tokenExpiration.Unix())
	if err != nil {
		http.Error(w, "Failed to add token "+err.Error(), http.StatusInternalServerError)
		return
	}

	utility.ReturnSelfSession(userId, w, r, &authToken)
}
