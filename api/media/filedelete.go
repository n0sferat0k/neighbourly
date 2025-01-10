package media

import (
	"api/utility"
	"context"
	"fmt"
	"net/http"
	"os"
)

func DeleteFile(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	target := r.URL.Query().Get("target")
	targetId := r.URL.Query().Get("targetId")

	var fileUrl string
	var itemId string
	var delQuery string

	if target == utility.TARGET_ITEM_IMAGE {
		var itemImgId string

		err := utility.DB.QueryRow(`SELECT 
										II.items_id, II.items_IMGS_id, II.items_IMGS_pic
									FROM 
										items_imgs II 
                                    LEFT JOIN items I ON II.items_id = I.items_id 
                                    LEFT JOIN neighbourhood_household_users NHU ON I.items_add_numerics_0 = NHU.neighbourhood_household_users_id
									WHERE 
										II.items_IMGS_id = ? AND NHU.neighbourhood_household_users_add_numerics_2 = ?`, targetId, userId).Scan(&itemId, &itemImgId, &fileUrl)

		if err != nil {
			http.Error(w, "No access to modify this image"+err.Error(), http.StatusBadRequest)
			return
		}

		delQuery = "DELETE FROM items_imgs WHERE items_IMGS_id = " + itemImgId
	} else if target == utility.TARGET_ITEM_FILE {
		var itemFileId string

		err := utility.DB.QueryRow(`SELECT 
										FF.items_id, FF.items_FILES_id, FF.items_FILES_file
									FROM 
										items_files FF 
									LEFT JOIN items I ON FF.items_id = I.items_id 
									LEFT JOIN neighbourhood_household_users NHU ON I.items_add_numerics_0 = NHU.neighbourhood_household_users_id
									WHERE 
										FF.items_FILES_id = ? AND NHU.neighbourhood_household_users_add_numerics_2 = ?`, targetId, userId).Scan(&itemId, &itemFileId, &fileUrl)
		if err != nil {
			http.Error(w, "No access to modify this file "+err.Error(), http.StatusBadRequest)
			return
		}

		delQuery = "DELETE FROM items_files WHERE items_FILES_id = " + itemFileId
	}

	go func() {
		apiRelativeFolder := "../www/"
		delFile := apiRelativeFolder + fileUrl
		fmt.Println("deleteing file:" + delFile)

		if err := os.Remove(delFile); err != nil {
			fmt.Println("Failed to delete file:", err)
		}
	}()

	if _, err := utility.DB.Exec(delQuery); err != nil {
		http.Error(w, "Failed to delete ref from DB"+err.Error(), http.StatusInternalServerError)
		return
	}

	if _, err := utility.DB.Exec("UPDATE items SET items_data = UNIX_TIMESTAMP() WHERE items_id = " + itemId); err != nil {
		http.Error(w, "Failed to update item date"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
