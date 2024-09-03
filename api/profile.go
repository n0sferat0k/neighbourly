package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

func UploadProfileImage(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
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

	// Create a Go routine to save the file
	go func(userId string) {

		// Create the uploads folder if it doesn't exist
		if _, err := os.Stat("uploads"); os.IsNotExist(err) {
			os.Mkdir("uploads", os.ModePerm)
		}

		wwwRelativeFolder := "usersIMGS"
		apiRelativeFolder := "../www/"
		saveFolder := apiRelativeFolder + wwwRelativeFolder

		if _, err := os.Stat(saveFolder); os.IsNotExist(err) {
			os.Mkdir(saveFolder, os.ModePerm)
		}

		// Delete old file
		var oldUserImg string
		if err = db.QueryRow("SELECT users_pic FROM users WHERE users_id = ?", userId).Scan(&oldUserImg); err == nil {
			delFile := apiRelativeFolder + oldUserImg
			fmt.Println("deleteing file:" + delFile)

			if err := os.Remove(delFile); err != nil {
				fmt.Println("Failed to delete file:", err)
			}
		} else {
			fmt.Println("deleteing failed:" + err.Error())
		}

		// Create the destination file
		destinationFileName := "profile_" + userId + filepath.Ext(handler.Filename)
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

		dbFilePath := filepath.Join(wwwRelativeFolder, destinationFileName)
		if _, err = db.Exec("UPDATE users SET users_pic = ? WHERE users_id = ?", dbFilePath, userId); err != nil {
			http.Error(w, "Failed to update user with image", http.StatusInternalServerError)
			return
		}

		fmt.Println("File uploaded successfully:", handler.Filename)
	}(userId)

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintln(w, "File uploaded successfully")
}
