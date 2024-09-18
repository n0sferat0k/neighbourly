package main

import (
	"encoding/json"
	"net/http"
	"strconv"
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
		time.Now().Unix(), userId, *gps.Latitude*float64(gpsPrecisionFactor), *gps.Longitude*float64(gpsPrecisionFactor), gps.Timezone)

	if err != nil {
		http.Error(w, "Failed to store location"+err.Error(), http.StatusInternalServerError)
		return
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

	candidate, err := CalculateCandidateHouseholdLocation(gpsPayloads)
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
