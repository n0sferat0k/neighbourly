package content

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func AddOrUpdateItem(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var item entity.Item

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &item)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	var nhuId int64

	err := utility.DB.QueryRow(`SELECT 	
									NHU.neighbourhood_household_users_id													
								FROM 
									neighbourhood_household_users NHU 								
								WHERE 
									NHU.neighbourhood_household_users_add_numerics_0 = ?
								AND 
									NHU.neighbourhood_household_users_add_numerics_2 = ?
								LIMIT 1`, item.Neighbourhoodid, userId).Scan(&nhuId)

	if err != nil {
		http.Error(w, "You do not have access to post to this neighbourhood"+err.Error(), http.StatusUnauthorized)
		return
	}

	var itemId int64

	if item.Itemid == nil {
		insertResult, err := utility.DB.Exec(`INSERT INTO 
									items 
									(
										items_titlu_EN,	
										items_text_EN,										
										items_data,									
										items_add_numerics_0,
										items_add_numerics_1,
										items_add_numerics_2,
										items_add_numerics_3,																		
										items_add_strings_0, 
										items_link,
										items_pic
									) 
									VALUES (?, ?, UNIX_TIMESTAMP(), ?, ?, ?, ?, ?, ?,'')`,
			item.Name,
			item.Description,
			nhuId,
			item.TargetUserid,
			item.StartTs,
			item.EndTs,
			item.Type,
			item.Url)
		if err != nil {
			http.Error(w, "Failed to insert item "+err.Error(), http.StatusInternalServerError)
			return
		}
		itemId, err = insertResult.LastInsertId()
		if err != nil {
			http.Error(w, "Failed to get inserted item id "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		_, err = utility.DB.Exec(`UPDATE 
									items 
								SET		
									items_titlu_EN = ?,	
									items_text_EN = ?,									
									items_data = UNIX_TIMESTAMP(),							
									items_add_numerics_1 = ?,
									items_add_numerics_2 = ?,
									items_add_numerics_3 = ?,																		
									items_add_strings_0 = ?, 
									items_link = ?
								WHERE 
									items_id = ? 
								AND 
									items_add_numerics_0 = ?`,
			item.Name,
			item.Description,
			item.TargetUserid,
			item.StartTs,
			item.EndTs,
			item.Type,
			item.Url,
			item.Itemid,
			nhuId)

		if err != nil {
			http.Error(w, "Failed to get update item "+err.Error(), http.StatusInternalServerError)
			return
		}
		itemId = *item.Itemid
	}

	upToDateItem, err := GetItems([]int64{itemId}, "")
	if err != nil {
		http.Error(w, "Failed to get up to date item "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(upToDateItem[0])
}
