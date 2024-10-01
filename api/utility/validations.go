package utility

import (
	"regexp"
)

func ValidateEmail(email string) bool {
	const emailRegex = `^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`
	re := regexp.MustCompile(emailRegex)
	return re.MatchString(email)
}

func ValidatePhoneNumber(phone string) bool {
	const phoneRegex = `^\+?[0-9\s\-\(\)]+$`
	re := regexp.MustCompile(phoneRegex)
	return re.MatchString(phone)
}
