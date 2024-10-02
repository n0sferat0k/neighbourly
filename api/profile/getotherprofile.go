package profile

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func FetchProfile(w http.ResponseWriter, r *http.Request) {
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

	existingUser, err := utility.RetreiveUserData(*user.Userid, *user.Username)
	if err != nil {
		http.Error(w, "Failed to get user info "+err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}
