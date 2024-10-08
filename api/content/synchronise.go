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

	//get all the ids of neighbourhoods the user belongs to
	var neighbourhoodids = ""
	rows, err := utility.DB.Query("SELECT DISTINCT neighbourhood_household_users_add_numerics_0 FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_2 = ?", userId)
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

	//add the 0 neighbourhood --- this is future reserved for things like system message items
	neighbourhoodids += "0"

	//get all the items from the neighbourhoods
	sql := `SELECT 
						I.items_id, 
						I.items_titlu_EN,
						I.items_text_EN,
						I.items_link,
						I.items_add_numerics_1 AS target,
						I.items_add_numerics_2 AS start,
						I.items_add_numerics_3 AS end,
						I.items_data AS modified,
						NHU.neighbourhood_household_users_add_numerics_0 AS neighbourhood,
						NHU.neighbourhood_household_users_add_numerics_1 AS household,
						NHU.neighbourhood_household_users_add_numerics_2 AS user
	 				FROM
						items I
					LEFT JOIN 
						neighbourhood_household_users NHU 
					ON 
						NHU.neighbourhood_household_users_id = I.items_add_numerics_0
					WHERE 
						NHU.neighbourhood_household_users_add_numerics_0 IN (?)`

	// If we have a valid sinceTs, we only get the items that have been modified since then
	if sinceTs != "" {
		sql += " AND I.items_data >= " + sinceTs
	}
	itemRows, err := utility.DB.Query(sql, neighbourhoodids)
	if err != nil {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}

	var items []entity.Item
	defer itemRows.Close()

	for itemRows.Next() {
		var item entity.Item
		itemRows.Scan(&item.Itemid,
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

		imagesRows, err := utility.DB.Query("SELECT items_IMGS_id, items_IMGS_pic FROM items_imgs WHERE items_id = ?", item.Itemid)
		if err != nil {
			http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
			return
		}
		defer imagesRows.Close()
		item.Images = make(map[int64]string)
		for imagesRows.Next() {
			var Imageid int64
			var Image string
			imagesRows.Scan(&Imageid, &Image)
			item.Images[Imageid] = Image
		}

		filesRows, err := utility.DB.Query("SELECT items_FILES_id, items_FILES_file FROM items_files WHERE items_id = ?", item.Itemid)
		if err != nil {
			http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
			return
		}
		defer filesRows.Close()
		item.Files = make(map[int64]string)
		for filesRows.Next() {
			var Fileid int64
			var File string
			filesRows.Scan(&Fileid, &File)
			item.Files[Fileid] = File
		}

		items = append(items, item)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(items)
}
