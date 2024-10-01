package utility

import (
	"crypto/rand"
	"encoding/hex"

	"golang.org/x/crypto/bcrypt"
)

// Function to hash passwords using bcrypt
func HashPassword(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), 14)
	return string(bytes), err
}

// Function to check passwords using bcrypt
func CheckPasswordHash(password, hash string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
	return err == nil
}

func GenerateAuthToken() (string, error) {
	return GenerageRandomToken(126)
}

func GenerageRandomToken(length int64) (string, error) {
	token := make([]byte, length)
	_, err := rand.Reader.Read(token)
	return hex.EncodeToString(token), err
}
