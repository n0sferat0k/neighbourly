package media

import (
	"api/utility"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

const TARGET_PROFILE = "profile"
const TARGET_HOUSEHOLD = "household"

func UploadImage(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)

	target := r.URL.Query().Get("target")
	var targetFolder string
	var targetFilePrefix string
	var oldUserImg string
	var updateQuery string

	if target == TARGET_PROFILE {
		targetFolder = "usersIMGS"
		targetFilePrefix = "profile_" + userId
		utility.DB.QueryRow("SELECT users_pic FROM users WHERE users_id = ?", userId).Scan(&oldUserImg)
		updateQuery = "UPDATE users SET users_pic = ? WHERE users_id = " + userId
	} else if target == TARGET_HOUSEHOLD {
		var householdId string
		utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
		targetFolder = "householdsIMGS"
		targetFilePrefix = "household_" + householdId
		utility.DB.QueryRow("SELECT households_pic FROM households WHERE households_id = ?", householdId).Scan(&oldUserImg)
		updateQuery = "UPDATE households SET households_pic = ? WHERE households_id = " + householdId
	}

	// Parse the multipart form data
	if err := r.ParseMultipartForm(10 << 20); err != nil {
		http.Error(w, "Failed to parse multipart form", http.StatusBadRequest)
		return
	}

	// Retrieve the file from form data
	file, handler, err := r.FormFile("image")
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

		dst, err := os.Create(filepath.Join(saveFolder, destinationFileName))
		if err != nil {
			fmt.Println("Failed to create file:", err)
			return
		}
		defer dst.Close()

		// Copy the uploaded file to the destination file
		if _, err := io.Copy(dst, file); err != nil {
			fmt.Println("Failed to save file:", err)
			return
		}

		if _, err = utility.DB.Exec(updateQuery, dbFilePath); err != nil {
			http.Error(w, "Failed to update target with image", http.StatusInternalServerError)
			return
		}

		fmt.Println("File uploaded successfully:", handler.Filename)
	}()

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintln(w, dbFilePath)
}
