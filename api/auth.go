package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"net/http"

	"golang.org/x/crypto/bcrypt"
)

func RegisterUser(w http.ResponseWriter, r *http.Request) {
	type User struct {
		Username string `json:"username"`
		Password string `json:"password"`
		Email    string `json:"email"`
	}

	var user User
	var hashedPassword []byte
	var err error

	if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
		http.Error(w, "Invalid request payload", http.StatusBadRequest)
		return
	}

	if hashedPassword, err = bcrypt.GenerateFromPassword([]byte(user.Password), bcrypt.DefaultCost); err != nil {
		http.Error(w, "Failed to hash password", http.StatusInternalServerError)
		return
	}

	if _, err = db.Exec("INSERT INTO users (user_name, user_hash, user_mail) VALUES (?, ?, ?)", user.Username, string(hashedPassword), user.Email); err != nil {
		http.Error(w, "Failed to register user", http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintln(w, "User registered successfully")
}

func LoginUser(w http.ResponseWriter, r *http.Request) {
	type User struct {
		Username string `json:"username"`
		Password string `json:"password"`
	}

	var user User
	err := json.NewDecoder(r.Body).Decode(&user)
	if err != nil {
		http.Error(w, "Invalid request payload", http.StatusBadRequest)
		return
	}

	// Retrieve the hashed password from the database
	var storedHash string
	err = db.QueryRow("SELECT user_hash FROM users WHERE user_name = ?", user.Username).Scan(&storedHash)
	if err != nil {
		if err == sql.ErrNoRows {
			http.Error(w, "User not found", http.StatusUnauthorized)
		} else {
			http.Error(w, "Database error", http.StatusInternalServerError)
		}
		return
	}

	// Compare the hashed password with the entered password
	err = bcrypt.CompareHashAndPassword([]byte(storedHash), []byte(user.Password))
	if err != nil {
		http.Error(w, "Invalid password", http.StatusUnauthorized)
		return
	}

	w.WriteHeader(http.StatusOK)
	fmt.Fprintln(w, "Login successful")
}
