package household

import (
	"api/utility"
	"net/http"
)

func ResetHouseholdLocation(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value(utility.CtxKeyUserId).(string)

	_, err := utility.DB.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
		return
	}

	_, err = utility.DB.Exec("UPDATE households SET households_add_numerics_1 = 0,  households_add_numerics_2 = 0 WHERE households_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear household location"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
