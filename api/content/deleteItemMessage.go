package content

import (
	"api/utility"
	"context"
	"net/http"
)

func DeleteItemMessage(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var itemMessageId string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	itemMessageId = r.URL.Query().Get("itemMessageId")

	//user can delete a message if they are the author or if they are in the same household as the author, or if their household own the item commented upon
	_, err := utility.DB.Exec(`DELETE FROM messages WHERE messages_id = ? 
								AND (
										messages_add_numerics_0 = ? 
									OR 
										EXISTS(SELECT * FROM 
													neighbourhood_household_users NHU1 LEFT JOIN neighbourhood_household_users NHU2 
												ON 
													NHU1.neighbourhood_household_users_add_numerics_1 = NHU2.neighbourhood_household_users_add_numerics_1
												WHERE 
													NHU1.neighbourhood_household_users_add_numerics_2 = messages_add_numerics_0
												AND 
													NHU2.neighbourhood_household_users_add_numerics_2 = ?)
									OR 
										EXISTS(SELECT * FROM 
													items I LEFT JOIN neighbourhood_household_users NHU ON I.items_add_numerics_0 = NHU.neighbourhood_household_users_id
												WHERE 
													I.items_id = messages_add_numerics_1 
												AND 
													NHU.neighbourhood_household_users_add_numerics_2 = ?)
								)
								`, itemMessageId, userId, userId, userId)
	if err != nil {
		http.Error(w, "Failed to delete item messages "+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
