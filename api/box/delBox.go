package box

import (
	"api/entity"
	"api/utility"
	"context"
	"net/http"
)

func DelBox(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var box entity.Box

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &box)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//find the household where the user is the head
	var householdId int64
	err := utility.DB.QueryRow(`SELECT households_id FROM households WHERE households_add_numerics_0 = ? LIMIT 1`, userId).Scan(&householdId)
	if err != nil {
		http.Error(w, "Only household head may remove a Box"+err.Error(), http.StatusInternalServerError)
		return
	}

	_, err = utility.DB.Exec(`DELETE FROM boxes WHERE boxes_text_EN = ? AND boxes_add_numerics_0 = ?`, box.Id, householdId)

	if err != nil {
		http.Error(w, "Remove box failed"+err.Error(), http.StatusUnauthorized)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
