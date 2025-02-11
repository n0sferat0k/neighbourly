package box

import (
	"api/utility"
	"context"
	"net/http"
)

func AcquireShareBox(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	boxShareToken := r.URL.Query().Get("token")
	if boxShareToken == "" {
		http.Error(w, "Error invalid token", http.StatusInternalServerError)
		return
	}

	//find the household of the user
	var householdId int64
	err := utility.DB.QueryRow(`SELECT users_add_numerics_0 FROM users WHERE users_id = ? LIMIT 1`, userId).Scan(&householdId)
	if err != nil {
		http.Error(w, "Error getting household "+err.Error(), http.StatusInternalServerError)
		return
	}

	_, err = utility.DB.Exec(`UPDATE boxshares SET boxshares_add_strings_0 = '', boxshares_add_numerics_0 = ? WHERE boxshares_add_strings_0 = ?`, householdId, boxShareToken)
	if err != nil {
		http.Error(w, "Invalid token "+err.Error(), http.StatusUnauthorized)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
