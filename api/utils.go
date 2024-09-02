package main

import (
	"crypto/rand"
	"encoding/hex"
	"regexp"

	"golang.org/x/crypto/bcrypt"
)

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
	// Create a byte slice to hold the random bytes
	token := make([]byte, 128)

	// Read random bytes into the slice

	_, err := rand.Reader.Read(token)

	// Encode the byte slice to a hexadecimal string
	return hex.EncodeToString(token), err
}
