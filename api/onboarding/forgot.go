package onboarding

import (
	"api/entity"
	"api/utility"
	"context"
	"database/sql"
	"net/http"
	"time"
)

func ForgotPassword(w http.ResponseWriter, r *http.Request) {
	var user entity.User

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequirePayload(ctx, w, r, &user)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	var userId string

	err := utility.DB.QueryRow(`SELECT 
									U.users_id						
								FROM 
									users U 								
								WHERE 
									U.users_add_strings_3 = ?
								LIMIT 1`,
		user.Email,
	).Scan(
		&userId,
	)

	if err != nil {
		if err == sql.ErrNoRows {
			http.Error(w, "Email incorrect "+err.Error(), http.StatusUnauthorized)
			return
		} else {
			http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
			return
		}
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

	//send the email with the password reset link

	err = utility.SendEmail(*user.Email, "Password reset", "<html><body>Click <a href='http://neighbourly.go.ro/resetpass.php?token="+authToken+"'>here</a> to reset your password<br />Or copy and paste the link below:<br/>http://neighbourly.go.ro/resetpass.php?token="+authToken+"</html></body>")

	if err != nil {
		http.Error(w, "Failed to send reset email "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}
