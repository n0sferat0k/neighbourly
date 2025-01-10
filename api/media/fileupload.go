package media

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"fmt"
	"image"
	"image/jpeg"
	"image/png"
	"io"
	"net/http"
	"os"
	"path/filepath"

	"github.com/nfnt/resize"
)

func UploadFile(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	target := r.URL.Query().Get("target")
	targetId := r.URL.Query().Get("targetId")

	var targetFolder string
	var targetFilePrefix string
	var oldUserImg string
	var updateQuery string = ""
	var insertQuery string = ""
	var auxQueryWithId string = ""
	var auxQuery string = ""

	if target == utility.TARGET_PROFILE {
		targetFolder = "usersIMGS"
		targetFilePrefix = "profile_" + userId
		utility.DB.QueryRow("SELECT users_pic FROM users WHERE users_id = ?", userId).Scan(&oldUserImg)
		updateQuery = "UPDATE users SET users_data = UNIX_TIMESTAMP(), users_pic = ? WHERE users_id = " + userId
	} else if target == utility.TARGET_HOUSEHOLD {
		var householdId string
		utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
		targetFolder = "householdsIMGS"
		targetFilePrefix = "household_" + householdId
		utility.DB.QueryRow("SELECT households_pic FROM households WHERE households_id = ?", householdId).Scan(&oldUserImg)
		updateQuery = "UPDATE households SET households_data = UNIX_TIMESTAMP(), households_pic = ? WHERE households_id = " + householdId
	} else if target == utility.TARGET_ITEM_IMAGE {
		var itemId string
		err := utility.DB.QueryRow(`SELECT 
										I.items_id 
									FROM 
										items I
									LEFT JOIN neighbourhood_household_users NHU
										ON I.items_add_numerics_0 = NHU.neighbourhood_household_users_id
									WHERE 
										I.items_id = ?
									AND
										NHU.neighbourhood_household_users_add_numerics_2 = ?
									LIMIT 1`, targetId, userId).Scan(&itemId)
		if err != nil {
			http.Error(w, "No access to modify this item "+err.Error(), http.StatusBadRequest)
			return
		}
		targetFolder = "itemsIMGS"
		targetFilePrefix = "item_image_" + itemId

		insertQuery = "INSERT INTO items_imgs (items_IMGS_pic, items_IMGS_name, items_id) VALUES (?,?," + itemId + ")"
		auxQueryWithId = "UPDATE items SET items_data = UNIX_TIMESTAMP(), items_pic = ? WHERE items_id = " + itemId
	} else if target == utility.TARGET_ITEM_FILE {
		var itemId string
		err := utility.DB.QueryRow(`SELECT 
										I.items_id 
									FROM items I
									LEFT JOIN neighbourhood_household_users NHU
										ON I.items_add_numerics_0 = NHU.neighbourhood_household_users_id
									WHERE 
										I.items_id = ?
									AND
										NHU.neighbourhood_household_users_add_numerics_2 = ?
									LIMIT 1`, targetId, userId).Scan(&itemId)
		if err != nil {
			http.Error(w, "No access to modify this item "+err.Error(), http.StatusBadRequest)
			return
		}
		targetFolder = "itemsFILES"
		targetFilePrefix = "item_file_" + itemId

		insertQuery = "INSERT INTO items_files (items_FILES_file, items_FILES_name, items_id) VALUES (?,?," + itemId + ")"
		auxQuery = "UPDATE items SET items_data = UNIX_TIMESTAMP() WHERE items_id = " + itemId
	}

	// Parse the multipart form data
	if err := r.ParseMultipartForm(10 << 20); err != nil {
		http.Error(w, "Failed to parse multipart form", http.StatusBadRequest)
		return
	}

	// Retrieve the file from form data
	file, handler, err := r.FormFile("file")
	if err != nil {
		http.Error(w, "Failed to retrieve file", http.StatusBadRequest)
		return
	}
	defer file.Close()

	wwwRelativeFolder := targetFolder
	apiRelativeFolder := "../www/"
	saveFolder := apiRelativeFolder + wwwRelativeFolder
	var randomString string
	if randomString, err = utility.GenerageRandomToken(16); err != nil {
		randomString = ""
	}
	destinationFileName := targetFilePrefix + "_" + randomString + filepath.Ext(handler.Filename)
	dbFilePath := filepath.Join(wwwRelativeFolder, destinationFileName)
	dbFileName := handler.Filename

	// Create a Go routine to save the file
	go func() {
		if _, err := os.Stat("uploads"); os.IsNotExist(err) {
			os.Mkdir("uploads", os.ModePerm)
		}

		if _, err := os.Stat(saveFolder); os.IsNotExist(err) {
			os.Mkdir(saveFolder, os.ModePerm)
		}

		// Delete old file
		delFile := apiRelativeFolder + oldUserImg
		fmt.Println("deleteing file:" + delFile)

		if err := os.Remove(delFile); err != nil {
			fmt.Println("Failed to delete file:", err)
		}

		// Create the destination file
		outputPath := filepath.Join(saveFolder, destinationFileName)
		outputFile, err := os.Create(outputPath)
		if err != nil {
			http.Error(w, "Error creating the output file: "+err.Error(), http.StatusInternalServerError)
			return
		}
		defer outputFile.Close()

		if target == utility.TARGET_ITEM_FILE {
			// Copy the uploaded file to the destination file
			if _, err := io.Copy(outputFile, file); err != nil {
				fmt.Println("Failed to save file:", err)
				return
			}
		} else {
			// Decode the image
			img, format, err := image.Decode(file)
			if err != nil {
				http.Error(w, "Error decoding the image: "+err.Error(), http.StatusBadRequest)
				return
			}

			// Define the maximum width and height
			// Resize the image
			resizedImg := resize.Thumbnail(utility.MAX_IMG_SIZE, utility.MAX_IMG_SIZE, img, resize.Lanczos3)

			// Encode and save the resized image
			switch format {
			case "jpeg":
				err = jpeg.Encode(outputFile, resizedImg, nil)
			case "png":
				err = png.Encode(outputFile, resizedImg)
			default:
				http.Error(w, "Unsupported image format: "+format, http.StatusBadRequest)
				return
			}

			if err != nil {
				http.Error(w, "Error encoding the resized image: "+err.Error(), http.StatusInternalServerError)
				return
			}

			// Respond with the path to the resized image
			w.Write([]byte("Resized image saved to " + outputPath))
		}

		fmt.Println("File uploaded successfully:", handler.Filename)
	}()

	var response entity.Attachment
	response.Url = &dbFilePath
	response.Name = &dbFileName

	if updateQuery != "" {
		if _, err = utility.DB.Exec(updateQuery, dbFilePath); err != nil {
			http.Error(w, "Failed to add file ref to DB "+err.Error(), http.StatusInternalServerError)
			return
		}
	}
	if insertQuery != "" {
		if insertResult, err := utility.DB.Exec(insertQuery, dbFilePath, dbFileName); err != nil {
			http.Error(w, "Failed to add file ref to DB "+err.Error(), http.StatusInternalServerError)
			return
		} else {
			insertedId, err := insertResult.LastInsertId()
			if err != nil {
				http.Error(w, "Failed to insert file ref "+err.Error(), http.StatusInternalServerError)
				return
			}

			response.Id = &insertedId

			if auxQuery != "" {
				if _, err = utility.DB.Exec(auxQuery); err != nil {
					http.Error(w, "Failed to run DB query "+err.Error(), http.StatusInternalServerError)
					return
				}
			}
			if auxQueryWithId != "" {
				if _, err = utility.DB.Exec(auxQueryWithId, insertedId); err != nil {
					http.Error(w, "Failed to run DB query "+err.Error(), http.StatusInternalServerError)
					return
				}
			}
		}
	}

	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(response)
}
