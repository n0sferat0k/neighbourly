package main

import (
	"encoding/json"
	"net/http"
)

func ReceiveGPS(w http.ResponseWriter, r *http.Request) {
	var data GPSData
	err := json.NewDecoder(r.Body).Decode(&data)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	// Save to database
	err = SaveGPSData(data)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	w.WriteHeader(http.StatusOK)
}

// Endpoint to analyze data and find home base
func DetermineHomeBase(w http.ResponseWriter, r *http.Request) {
	userID := r.URL.Query().Get("user_id")
	if userID == "" {
		http.Error(w, "User ID is required", http.StatusBadRequest)
		return
	}

	gpsData, err := GetGPSDataByUser(userID)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	homeBase := DetermineHomeBaseFromGPSData(gpsData)

	// Return the computed home base as JSON
	response := map[string]float64{
		"latitude":  homeBase.Latitude,
		"longitude": homeBase.Longitude,
	}
	json.NewEncoder(w).Encode(response)
}
