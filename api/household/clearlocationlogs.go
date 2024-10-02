package household

import (
	"api/utility"
	"context"
	"net/http"
)

func ClearGpsData(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	_, err := utility.DB.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
