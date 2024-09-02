package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

func UploadImage(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	// Extract the token from the request headers
	token := r.Header.Get("Authorization")
	if token == "" {
		http.Error(w, "Missing token", http.StatusUnauthorized)
		return
	}

	// Validate the token (simple validation for demonstration purposes)
	userID, err := validateToken(token)
	if err != nil {
		http.Error(w, "Invalid token", http.StatusUnauthorized)
		return
	}

	// Parse the multipart form data
	err = r.ParseMultipartForm(10 << 20) // 10 MB
	if err != nil {
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
	go func() {
		// Create the uploads folder if it doesn't exist
		if _, err := os.Stat("uploads"); os.IsNotExist(err) {
			os.Mkdir("uploads", os.ModePerm)
		}

		// Create a user-specific folder
		wwwRelativeFolder := "usersIMGS"
		saveFolder := "../www/usersIMGS"
		if _, err := os.Stat(saveFolder); os.IsNotExist(err) {
			os.Mkdir(saveFolder, os.ModePerm)
		}

		// Create the destination file
		destinationFileName := "profile_" + userID + filepath.Ext(handler.Filename)
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
		if _, err = db.Exec("UPDATE users SET users_pic = ? WHERE users_id = ?", dbFilePath, userID); err != nil {
			http.Error(w, "Failed to update user with image", http.StatusInternalServerError)
			return
		}

		fmt.Println("File uploaded successfully:", handler.Filename)
	}()

	fmt.Fprintln(w, "File uploaded successfully")
	w.WriteHeader(http.StatusCreated)
}

// validateToken is a placeholder function for token validation
func validateToken(token string) (string, error) {
	// For demonstration purposes, assume the token is the userID
	// In a real application, you would validate the token and extract the userID
	if token == "valid-token" {
		return "1", nil
	}
	return "", fmt.Errorf("invalid token")
}
