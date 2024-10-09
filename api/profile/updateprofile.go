package profile

import (
	"api/entity"
	"api/utility"
	"context"
	"net/http"
)

func UpdateProfile(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var user entity.User

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &user)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	// Update the user in the database
	_, err := utility.DB.Exec(`UPDATE users SET users_data = UNIX_TIMESTAMP(), users_titlu_EN = ?, users_add_strings_3 = ?, users_add_strings_2 =  ?,  users_text_EN = ? WHERE users_id = ?`,
		user.Fullname, user.Email, user.Phone, user.Userabout, userId)
	if err != nil {
		http.Error(w, "Failed to register user "+err.Error(), http.StatusInternalServerError)
		return
	}

	utility.ReturnSelfSession(userId, w, r, nil)
}
