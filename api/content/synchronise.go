package content

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func Synchronise(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	sinceTs := r.Header.Get("If-Modified-Since")

	var neighbourhoodids = ""
	rows, err := utility.DB.Query("SELECT UNIQUE neighbourhood_household_users_add_numerics_0 FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_2 = ?", userId)
	if err != nil {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}

	defer rows.Close()
	for rows.Next() {
		var neighbourhoodid string
		rows.Scan(&neighbourhoodid)
		neighbourhoodids += neighbourhoodid + ","
	}
	neighbourhoodids += "0"

	sql := `SELECT 
						I.items_id, 
						I.items_titlu_EN,
						I.items_text_EN,
						I.items_link,
						I.items_add_numerics_1 AS target,
						I.items_add_numerics_2 AS start,
						I.items_add_numerics_3 AS end,
						I.items_data AS modified,
						NHU.neighbourhood_household_users_add_numerics_0 AS neighbourhood
						NHU.neighbourhood_household_users_add_numerics_1 AS household
						NHU.neighbourhood_household_users_add_numerics_2 AS user
	 				FROM
						items I
					LEFT JOIN 
						neighbourhood_household_users NHU 
					ON 
						NHU.neighbourhood_household_users_id = I.items_add_numerics_0
					WHERE 
						NHU.neighbourhood_household_users_add_numerics_0 IN (?)`

	if sinceTs == "" {
		rows, err = utility.DB.Query(sql, neighbourhoodids)
	} else {
		sql += "AND I.items_data > ?"
		rows, err = utility.DB.Query(sql, neighbourhoodids, sinceTs)
	}

	if err != nil {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}

	var items []entity.Item

	defer rows.Close()
	for rows.Next() {
		var item entity.Item
		rows.Scan(&item.Itemid,
			&item.Name,
			&item.Description,
			&item.Url,
			&item.TargetUserid,
			&item.StartTs,
			&item.EndTs,
			&item.LastModifiedTs,
			&item.Neighbourhoodid,
			&item.Householdid,
			&item.Userid)

		items = append(items, item)
	}

	//todo fill in images and files

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(items)
}
