package main

import (
	"encoding/json"
	"net/http"
	"time"
)

func LogGpsLocation(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	var gps GpsPayload
	if err := json.NewDecoder(r.Body).Decode(&gps); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	_, err := db.Exec("INSERT INTO coordinates (coordinates_data, coordinates_add_numerics_0, coordinates_add_numerics_1, coordinates_add_numerics_2, coordinates_add_numerics_3) VALUES (?, ?, ?, ?, ?)",
		time.Now().Unix(), userId, *gps.Latitude*float64(10000), *gps.Longitude*float64(10000), gps.Timezone)

	if err != nil {
		http.Error(w, "Failed to store location"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}

func GetGpsHeatmap(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Invalid request method "+r.Method, http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	rows, err := db.Query(`SELECT coordinates_add_numerics_1 / 10000, coordinates_add_numerics_2 / 10000, COUNT(*) FROM coordinates
				WHERE coordinates_add_numerics_0 = ?
			AND (
					TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '`+nightStart+`' AND '23:59:59'
					OR TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '00:00:00' AND '`+nightEnd+`'
				)
			GROUP BY
				coordinates_add_numerics_1, coordinates_add_numerics_2`,
		userId)

	if err != nil {
		http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
		return
	}

	defer rows.Close()

	var gpsPayloads []GpsPayload
	for rows.Next() {
		var gpsPayload GpsPayload
		err := rows.Scan(
			&gpsPayload.Latitude,
			&gpsPayload.Longitude,
			&gpsPayload.Frequency,
		)

		if err != nil {
			http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
			return
		}

		gpsPayloads = append(gpsPayloads, gpsPayload)
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(gpsPayloads)
}
