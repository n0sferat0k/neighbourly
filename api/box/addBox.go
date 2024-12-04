package box

import (
	"api/entity"
	"api/utility"
	"context"
	"database/sql"
	"net/http"
)

func AddBox(w http.ResponseWriter, r *http.Request) {
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
		http.Error(w, "Only household head may add a Box"+err.Error(), http.StatusInternalServerError)
		return
	}

	var boxHouseholdId int64
	err = utility.DB.QueryRow(`SELECT * FROM boxes WHERE boxes_text_EN = ? LIMIT 1`, box.Id).Scan(&boxHouseholdId)
	if err != nil {
		if err == sql.ErrNoRows {
			_, err = utility.DB.Exec(`INSERT INTO boxes (boxes_titlu_EN, boxes_text_EN, boxes_add_numerics_0) VALUES (?, ?, ?)`, box.Name, box.Id, householdId)
			if err != nil {
				http.Error(w, "Failed to add box"+err.Error(), http.StatusInternalServerError)
				return
			}
		} else {
			http.Error(w, "Failed to add box"+err.Error(), http.StatusInternalServerError)
			return
		}
		//return success
		w.WriteHeader(http.StatusOK)
	} else {
		if boxHouseholdId == householdId {
			http.Error(w, "Box already added to household", http.StatusUnauthorized)
		} else {
			http.Error(w, "Box in use by a different household", http.StatusUnauthorized)
		}
	}
}
