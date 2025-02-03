package content

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func GetItemMessages(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var itemId string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	itemId = r.URL.Query().Get("itemId")

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
								`, itemId, userId).Scan(&neighbourhoodId)

	if err != nil {
		http.Error(w, "You do not have access to read messages of items from this neighbourhood "+err.Error(), http.StatusUnauthorized)
		return
	}

	rows, err := utility.DB.Query(`SELECT 
										messages_id, messages_text_EN, messages_data, messages_add_numerics_0, messages_add_numerics_1
									FROM 
										messages 
									WHERE 
										messages_add_numerics_1 = ?
									

								`, itemId)

	if err != nil {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var itemMessages []entity.ItemMessage
	for rows.Next() {
		var itemMessage entity.ItemMessage
		rows.Scan(&itemMessage.Id, &itemMessage.Message, &itemMessage.LastModifiedTs, &itemMessage.Userid, &itemMessage.Itemid)
		itemMessages = append(itemMessages, itemMessage)
	}

	w.Header().Set("Content-Type", "application/json")
	if len(itemMessages) == 0 {
		w.Write([]byte("[]"))
	} else {
		json.NewEncoder(w).Encode(itemMessages)
	}
}
