package content

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func GetItemsMessages(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var itemIds []int64
	var sinceTs string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &itemIds)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	sinceTs = r.Header.Get("If-Modified-Since")

	//empty return for empty id list
	if itemIds == nil || len(itemIds) == 0 {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte("[]"))
		return
	}

	//filter out any items that the user does not have access to
	itemRows, err := utility.DB.Query(`SELECT 
									I.items_id
	 							FROM 
									items I 
								LEFT JOIN 
									neighbourhood_household_users NHU 
								ON 
									I.items_add_numerics_0 = NHU.neighbourhood_household_users_id 
								WHERE 
									I.items_id IN (`+utility.IntArrayToCommaSeparatedString(itemIds)+`)
								AND EXISTS (SELECT * FROM 
												neighbourhood_household_users NHU2 
											WHERE 
												NHU2.neighbourhood_household_users_add_numerics_0 = NHU.neighbourhood_household_users_add_numerics_0 
											AND 
												NHU2.neighbourhood_household_users_add_numerics_2 = ?
											)
								`, userId)

	if err != nil {
		http.Error(w, "You do not have access to read messages of items from this neighbourhood "+err.Error(), http.StatusUnauthorized)
		return
	}
	defer itemRows.Close()

	var filteredItemIds []int64
	for itemRows.Next() {
		var itemId int64
		itemRows.Scan(&itemId)
		filteredItemIds = append(filteredItemIds, itemId)
	}

	//empty return for empty filtered id list
	if filteredItemIds == nil || len(filteredItemIds) == 0 {
		w.Header().Set("Content-Type", "application/json")
		w.Write([]byte("[]"))
		return
	}

	//get messages for the items
	sql := `SELECT 
				messages_id, messages_text_EN, messages_data, messages_add_numerics_0, messages_add_numerics_1
			FROM 
				messages 
			WHERE 
				messages_add_numerics_1 IN (` + utility.IntArrayToCommaSeparatedString(filteredItemIds) + `)`

	if sinceTs != "" {
		sql += " AND messages_data > " + sinceTs
	}

	messageRows, err := utility.DB.Query(sql)

	if err != nil {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}
	defer messageRows.Close()

	var itemMessages []entity.ItemMessage
	for messageRows.Next() {
		var itemMessage entity.ItemMessage
		messageRows.Scan(&itemMessage.Id, &itemMessage.Message, &itemMessage.LastModifiedTs, &itemMessage.Userid, &itemMessage.Itemid)
		itemMessages = append(itemMessages, itemMessage)
	}

	w.Header().Set("Content-Type", "application/json")
	if len(itemMessages) == 0 {
		w.Write([]byte("[]"))
	} else {
		json.NewEncoder(w).Encode(itemMessages)
	}
}
