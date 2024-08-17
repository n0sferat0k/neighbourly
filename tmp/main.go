package main

import (
	"log"
	"net/http"

	"github.com/gorilla/mux"
)

func main() {
	// Initialize router
	r := mux.NewRouter()

	// Define routes
	r.HandleFunc("/gps", ReceiveGPS).Methods("POST")
	r.HandleFunc("/home-base", DetermineHomeBase).Methods("GET")

	// Start server
	log.Fatal(http.ListenAndServe(":8000", r))
}
