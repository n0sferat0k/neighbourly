package main

import (
	"encoding/json"
	"net/http"
	"strconv"
	"time"
)

func RetrieveHeatmap(userId string, onlyNight bool) ([]GpsPayload, error) {
	factorStr := strconv.Itoa(gpsPrecisionFactor)
	query := `SELECT coordinates_add_numerics_1 / ` + factorStr + `, coordinates_add_numerics_2 / ` + factorStr + `, COUNT(*) FROM coordinates
				WHERE coordinates_add_numerics_0 = ?`

	if onlyNight {
		query += ` AND (
						TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '` + nightStart + `' AND '23:59:59'
						OR TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '00:00:00' AND '` + nightEnd + `'
					)`
	}

	query += ` GROUP BY coordinates_add_numerics_1, coordinates_add_numerics_2`

	rows, err := db.Query(query, userId)
	if err != nil {
		return nil, err
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
			return nil, err
		}

		gpsPayloads = append(gpsPayloads, gpsPayload)
	}

	return gpsPayloads, nil
}

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
		time.Now().Unix(), userId, *gps.Latitude*float64(gpsPrecisionFactor), *gps.Longitude*float64(gpsPrecisionFactor), gps.Timezone)

	if err != nil {
		http.Error(w, "Failed to store location"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}

func ClearGpsData(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Invalid request method "+r.Method, http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	_, err := db.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
	}

	//return success
	w.WriteHeader(http.StatusOK)
}

func GetGpsCandidate(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		http.Error(w, "Invalid request method "+r.Method, http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	gpsPayloads, err := RetrieveHeatmap(userId, true)

	if err != nil {
		http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
		return
	}

	candidate, err := findLargestCluserLocation(gpsPayloads)
	if err != nil {
		http.Error(w, "Failed to get household candidate "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(candidate)
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

	onlyNightParam := r.URL.Query().Get("onlyNight")
	onlyNight, err := strconv.ParseBool(onlyNightParam)
	if err != nil {
		onlyNight = false
	}

	gpsPayloads, err := RetrieveHeatmap(userId, onlyNight)

	if err != nil {
		http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(gpsPayloads)
}
