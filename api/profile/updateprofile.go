package profile

import (
	"api/entity"
	"api/utility"
	"net/http"
)

func UpdateProfile(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	ctx = utility.RequirePayloadUser(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value(utility.CtxKeyUserId).(string)
	user := ctx.Value(utility.CtxKeyUser).(entity.User)

	// Update the user in the database
	_, err := utility.DB.Exec(`UPDATE users SET users_titlu_EN = ?, users_add_strings_3 = ?, users_add_strings_2 =  ?,  users_text_EN = ? WHERE users_id = ?`,
		user.Fullname, user.Email, user.Phone, user.Userabout, userId)
	if err != nil {
		http.Error(w, "Failed to register user "+err.Error(), http.StatusInternalServerError)
		return
	}

	utility.ReturnSelfSession(ctx, w, r, nil)
}
