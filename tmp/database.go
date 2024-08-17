package main

import (
	"database/sql"
	"log"

	_ "github.com/mattn/go-sqlite3"
)

var db *sql.DB

func init() {
	var err error
	db, err = sql.Open("sqlite3", "./gps_data.db")
	if err != nil {
		log.Fatal(err)
	}

	createTable := `
	CREATE TABLE IF NOT EXISTS gps_data (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		user_id TEXT,
		latitude REAL,
		longitude REAL,
		timestamp INTEGER
	);`

	_, err = db.Exec(createTable)
	if err != nil {
		log.Fatal(err)
	}
}

func SaveGPSData(data GPSData) error {
	stmt, err := db.Prepare("INSERT INTO gps_data(user_id, latitude, longitude, timestamp) VALUES(?, ?, ?, ?)")
	if err != nil {
		return err
	}
	defer stmt.Close()

	_, err = stmt.Exec(data.UserID, data.Latitude, data.Longitude, data.Timestamp)
	return err
}

func GetGPSDataByUser(userID string) ([]GPSData, error) {
	rows, err := db.Query("SELECT user_id, latitude, longitude, timestamp FROM gps_data WHERE user_id = ?", userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var data []GPSData
	for rows.Next() {
		var d GPSData
		err := rows.Scan(&d.UserID, &d.Latitude, &d.Longitude, &d.Timestamp)
		if err != nil {
			return nil, err
		}
		data = append(data, d)
	}
	return data, nil
}
