package content

import (
	"api/entity"
	"api/utility"
	"fmt"
)

func GetItemsFromNeighbourhoods(userId string, neighbourhoodids string, sinceTs string) ([]entity.Item, []int64, error) {
	//get all the item ids that still exist from the neighbourhoods
	sql := `SELECT 
				I.items_id 
			FROM 
				items I 
			LEFT JOIN 
				neighbourhood_household_users NHU 
			ON 
				NHU.neighbourhood_household_users_id = I.items_add_numerics_0
			WHERE 
				(
						NOT(I.items_add_strings_0 = "REMINDER") 
					OR 
						I.items_accent = 1
					OR
						I.items_add_numerics_1  =  ?
				)
			AND	
				NHU.neighbourhood_household_users_add_numerics_0 IN (?)
				`
	itemRows, err := utility.DB.Query(sql, userId, neighbourhoodids)
	if err != nil {
		return nil, nil, err
	}
	var itemIds []int64

	defer itemRows.Close()
	for itemRows.Next() {
		var itemId int64
		itemRows.Scan(&itemId)
		itemIds = append(itemIds, itemId)
	}

	var items []entity.Item

	if len(itemIds) == 0 {
		return items, itemIds, nil
	} else {
		items, err = GetItems(itemIds, sinceTs)
		if err != nil {
			return nil, nil, err
		}

		return items, itemIds, nil
	}
}

func GetItems(itemIds []int64, sinceTs string) ([]entity.Item, error) {

	//get all the items from the neighbourhoods
	sql := `SELECT 
						I.items_id, 
						I.items_add_strings_0 AS type,
						I.items_titlu_EN,
						I.items_text_EN,
						I.items_link,
						I.items_add_numerics_1 AS target,
						I.items_accent AS accent,
						I.items_add_numerics_2 AS start,
						I.items_add_numerics_3 AS end,
						I.items_data AS modified,
						I.items_pic AS defPicId,
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
						I.items_id IN (` + utility.IntArrayToCommaSeparatedString(itemIds) + `)`

	// If we have a valid sinceTs, we only get the items that have been modified since then
	if sinceTs != "" {
		sql += " AND I.items_data > " + sinceTs
	}

	itemRows, err := utility.DB.Query(sql)
	if err != nil {
		//return custom error
		return nil, fmt.Errorf(sql)
	}

	var items []entity.Item
	defer itemRows.Close()

	for itemRows.Next() {
		var item entity.Item
		var defPicId int64

		itemRows.Scan(&item.Itemid,
			&item.Type,
			&item.Name,
			&item.Description,
			&item.Url,
			&item.TargetUserid,
			&item.Accent,
			&item.StartTs,
			&item.EndTs,
			&item.LastModifiedTs,
			&defPicId,
			&item.Neighbourhoodid,
			&item.Householdid,
			&item.Userid,
		)

		imagesRows, err := utility.DB.Query("SELECT items_IMGS_id, items_IMGS_pic, items_IMGS_name FROM items_imgs WHERE items_id = ?", item.Itemid)
		if err != nil {
			return nil, err
		}
		defer imagesRows.Close()

		var images []entity.Attachment
		for imagesRows.Next() {
			var image entity.Attachment
			imagesRows.Scan(&image.Id, &image.Url, &image.Name)
			isDefault := *image.Id == defPicId
			image.Default = &isDefault
			images = append(images, image)
		}
		item.Images = images

		filesRows, err := utility.DB.Query("SELECT items_FILES_id, items_FILES_file, items_FILES_name FROM items_files WHERE items_id = ?", item.Itemid)
		if err != nil {
			return nil, err
		}
		defer filesRows.Close()

		var files []entity.Attachment
		for filesRows.Next() {
			var file entity.Attachment
			filesRows.Scan(&file.Id, &file.Url, &file.Name)
			files = append(files, file)
		}
		item.Files = files

		items = append(items, item)
	}

	return items, nil
}
