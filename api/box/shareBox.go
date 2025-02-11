package box

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func ShareBox(w http.ResponseWriter, r *http.Request) {
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
	err := utility.DB.QueryRow(`SELECT users_add_numerics_0 FROM users WHERE users_id = ? LIMIT 1`, userId).Scan(&householdId)
	if err != nil {
		http.Error(w, "Only household members may control Boxes"+err.Error(), http.StatusUnauthorized)
		return
	}

	var boxVerifiedId string
	err = utility.DB.QueryRow(`SELECT boxes_text_EN FROM boxes WHERE boxes_text_EN = ? AND boxes_add_numerics_0 = ? LIMIT 1`, box.Id, householdId).Scan(&boxVerifiedId)
	if err != nil {
		http.Error(w, "You do not have access to share this Box "+err.Error(), http.StatusUnauthorized)
		return
	}

	boxToken, err := utility.GenerateAuthToken()
	if err != nil {
		http.Error(w, "Unable to generate token "+err.Error(), http.StatusUnauthorized)
		return
	}

	insertResult, err := utility.DB.Exec(`INSERT INTO boxshares (boxshares_add_strings_0, boxshares_titlu_EN, boxshares_text_EN, boxshares_add_numerics_0) VALUES (?,?,?,-1)`, boxToken, box.Name, box.Id)
	if err != nil {
		http.Error(w, "Unable to save token "+err.Error(), http.StatusUnauthorized)
		return
	}

	insertedId, err := insertResult.LastInsertId()

	boxShare := entity.BoxShare{
		Id:          &insertedId,
		Name:        box.Name,
		BoxId:       box.Id,
		Householdid: &householdId,
		Token:       &boxToken,
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(boxShare)
}
