package content

import (
	"api/entity"
	"api/utility"
)

func GetItemsFromNeighbourhoods(neighbourhoodids string, sinceTs string) ([]entity.Item, error) {
	//get all the items from the neighbourhoods
	sql := `SELECT 
						I.items_id, 
						I.items_add_strings_0 AS type,
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
		sql += " AND I.items_data > " + sinceTs
	}
	itemRows, err := utility.DB.Query(sql, neighbourhoodids)
	if err != nil {
		return nil, err
	}

	var items []entity.Item
	defer itemRows.Close()

	for itemRows.Next() {
		var item entity.Item
		itemRows.Scan(&item.Itemid,
			&item.Type,
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
			return nil, err
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
			return nil, err
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

	return items, nil
}
