package household

import (
	"api/utility"
	"net/http"
)

func ClearGpsData(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)

	_, err := utility.DB.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
