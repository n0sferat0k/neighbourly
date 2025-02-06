package utility

import (
	"fmt"
	"net/smtp"
)

func SendEmail(address string, subject string, body string) error {
	from := "neighbourly.app.ro@gmail.com"
	password := "vpup tqmp menw ehcf" //"NeighbourlyP@ssw0rd"

	// SMTP server configuration.
	smtpHost := "smtp.gmail.com"
	smtpPort := "587"

	// Message.
	subjectHeader := "Subject: " + subject + "\r\n"
	mimeHeader := "MIME-version: 1.0;\nContent-Type: text/html; charset=\"UTF-8\";\r\n\r\n"
	message := []byte(subjectHeader + mimeHeader + body)

	// Authentication.
	auth := smtp.PlainAuth("", from, password, smtpHost)

	// Sending email.
	err := smtp.SendMail(smtpHost+":"+smtpPort, auth, from, []string{address}, message)
	if err != nil {
		return fmt.Errorf("failed to send email: %w", err)
	}

	return nil
}
