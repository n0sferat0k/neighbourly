package content

import (
	"api/utility"
	"context"
	"net/http"
)

func DeleteItem(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	itemId := r.URL.Query().Get("itemId")

	//any household member may delete items created by members in the household
	var householdId int
	utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
	_, err := utility.DB.Exec(`DELETE FROM 
									items I
								WHERE 
									I.items_id = ?
								AND EXISTS(
									SELECT * FROM 
										neighbourhood_household_users NHU
									WHERE 
										NHU.neighbourhood_household_users_id = I.items_add_numerics_0										
									AND
										NHU.neighbourhood_household_users_add_numerics_1 = ?										
								)`, itemId, householdId)
	if err != nil {
		http.Error(w, "Failed to delete item"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
