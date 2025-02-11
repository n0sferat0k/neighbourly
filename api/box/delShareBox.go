package box

import (
	"api/entity"
	"api/utility"
	"context"
	"net/http"
)

func DelShareBox(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var boxShare entity.BoxShare

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &boxShare)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//find the household of the user
	var householdId int64
	err := utility.DB.QueryRow(`SELECT users_add_numerics_0 FROM users WHERE users_id = ? LIMIT 1`, userId).Scan(&householdId)
	if err != nil {
		http.Error(w, "Error getting household "+err.Error(), http.StatusInternalServerError)
		return
	}

	_, err = utility.DB.Exec(`DELETE FROM boxshares 
								WHERE 
									boxshares_id = ? 
								AND 
									(
										boxshares_add_numerics_0 = ? 
									OR 
										EXISTS (SELECT * FROM boxes 
													WHERE 
														boxes_text_EN = boxshares_text_EN 
													AND 
														boxes_add_numerics_0 = ?
												)
									)`, boxShare.Id, householdId, householdId)

	if err != nil {
		http.Error(w, "Remove box failed"+err.Error(), http.StatusUnauthorized)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
