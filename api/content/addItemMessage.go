package content

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func AddItemMessage(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var itemMessage entity.ItemMessage

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &itemMessage)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	var neighbourhoodId int64

	err := utility.DB.QueryRow(`SELECT 
									I.items_id
	 							FROM 
									items I 
								LEFT JOIN 
									neighbourhood_household_users NHU 
								ON 
									I.items_add_numerics_0 = NHU.neighbourhood_household_users_id 
								WHERE 
									I.items_id = ?
								AND EXISTS (SELECT * FROM 
												neighbourhood_household_users NHU2 
											WHERE 
												NHU2.neighbourhood_household_users_add_numerics_0 = NHU.neighbourhood_household_users_add_numerics_0 
											AND 
												NHU2.neighbourhood_household_users_add_numerics_2 = ?
											)
								`, itemMessage.Itemid, userId).Scan(&neighbourhoodId)

	if err != nil {
		http.Error(w, "You do not have access to post to this neighbourhood"+err.Error(), http.StatusUnauthorized)
		return
	}

	insertResult, err := utility.DB.Exec(`INSERT INTO 
											messages
											(
												messages_text_EN,
												messages_data,
												messages_add_numerics_0,
												messages_add_numerics_1												
											) 
											VALUES (?, UNIX_TIMESTAMP(), ?, ?)`,
		itemMessage.Message,
		userId,
		itemMessage.Itemid,
	)

	if err != nil {
		http.Error(w, "Failed to insert message "+err.Error(), http.StatusInternalServerError)
		return
	}

	messageId, err := insertResult.LastInsertId()
	if err != nil {
		http.Error(w, "Failed to get inserted message id "+err.Error(), http.StatusInternalServerError)
		return
	}

	var insertedItem entity.ItemMessage
	err = utility.DB.QueryRow(`SELECT 
							messages_id, messages_text_EN, messages_data, messages_add_numerics_0, messages_add_numerics_1
						FROM 
							messages WHERE messages_id = ?
						`, messageId).Scan(&insertedItem.Id, &insertedItem.Message, &insertedItem.LastModifiedTs, &insertedItem.Userid, &insertedItem.Itemid)

	if err != nil {
		http.Error(w, "Failed to read message back from DB "+err.Error(), http.StatusUnauthorized)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(insertedItem)
}
