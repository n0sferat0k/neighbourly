package utility

import (
	"crypto/rand"
	"encoding/hex"
	"strconv"
	"strings"

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

// IntArrayToCommaSeparatedString converts an array of integers to a comma-separated string
func IntArrayToCommaSeparatedString(intArray []int) string {
	// Create a slice to hold the string representations of the integers
	strArray := make([]string, len(intArray))

	// Convert each integer to a string
	for i, num := range intArray {
		strArray[i] = strconv.Itoa(num)
	}

	// Join the string representations with a comma
	return strings.Join(strArray, ",")
}
