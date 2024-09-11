package main

import (
	"crypto/rand"
	"database/sql"
	"encoding/hex"
	"fmt"
	"net/http"
	"regexp"
	"strings"
	"time"

	"golang.org/x/crypto/bcrypt"
)

func getToken(r *http.Request) string {
	return strings.TrimPrefix(r.Header.Get("Authorization"), "Bearer ")
}

// validateToken is a placeholder function for token validation
func validateToken(w http.ResponseWriter, r *http.Request) string {

	// Extract the token from the request headers
	token := getToken(r)

	if token == "" {
		http.Error(w, "Missing token", http.StatusUnauthorized)
		return ""
	}

	var existingTokenUserId string
	var existingTokenTs int64

	err := db.QueryRow("SELECT tokens_add_numerics_0, tokens_data FROM tokens WHERE tokens_titlu_EN = ? LIMIT 1", token).Scan(&existingTokenUserId, &existingTokenTs)
	if err != nil {
		if err == sql.ErrNoRows {
			http.Error(w, "Invalid token: "+token, http.StatusUnauthorized)
			return ""
		} else {
			http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
			return ""
		}
	}
	if existingTokenTs < time.Now().Unix() {
		http.Error(w, "Expired token", http.StatusUnauthorized)
		return ""
	}

	fmt.Println("Tokened user:" + existingTokenUserId)

	return existingTokenUserId
}

func validateEmail(email string) bool {
	const emailRegex = `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`
	re := regexp.MustCompile(emailRegex)
	return re.MatchString(email)
}

func validatePhoneNumber(phone string) bool {
	const phoneRegex = `^\+?[0-9\s\-\(\)]+$`
	re := regexp.MustCompile(phoneRegex)
	return re.MatchString(phone)
}

// Function to hash passwords using bcrypt
func hashPassword(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), 14)
	return string(bytes), err
}

// Function to check passwords using bcrypt
func checkPasswordHash(password, hash string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
	return err == nil
}

func generateAuthToken() (string, error) {
	return generageRandomToken(126)
}

func generageRandomToken(length int64) (string, error) {
	token := make([]byte, length)
	_, err := rand.Reader.Read(token)
	return hex.EncodeToString(token), err
}
