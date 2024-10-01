package profile

import (
	"api/entity"
	"api/utility"
	"encoding/json"
	"net/http"
)

func FetchProfile(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	ctx = utility.RequirePayloadUser(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	user := ctx.Value(utility.CtxKeyUser).(entity.User)

	existingUser, err := utility.RetreiveUserData(*user.Userid, *user.Username)
	if err != nil {
		http.Error(w, "Failed to get user info "+err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}
