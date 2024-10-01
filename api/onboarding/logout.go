package onboarding

import (
	"api/utility"
	"fmt"
	"net/http"
)

func LogoutUser(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value(utility.CtxKeyUserId).(string)
	token := ctx.Value(utility.CtxKeyToken).(string)

	logoutAll := r.URL.Query().Get("logoutAll")

	if logoutAll == "true" {
		_, err := utility.DB.Exec("DELETE FROM tokens WHERE tokens_add_numerics_0 = ?", userId)
		if err != nil {
			http.Error(w, "Failed to remove token "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		// delete the token from the database
		_, err := utility.DB.Exec("DELETE FROM tokens WHERE tokens_titlu_EN = ?", token)
		if err != nil {
			http.Error(w, "Failed to remove tokens "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	w.WriteHeader(http.StatusAccepted)
	fmt.Fprintln(w, "Logout successful")
}
