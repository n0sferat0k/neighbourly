package main

import (
	"fmt"
	"log"
	"net/http"

	"api/household"
	"api/media"
	"api/membership"
	"api/neighbourhood"
	"api/onboarding"
	"api/profile"
	"api/utility"

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
)

func main() {

	utility.ConnectDB()

	//request handlers
	r := mux.NewRouter()
	r.HandleFunc("/register", onboarding.RegisterUser).Methods("POST")
	r.HandleFunc("/login", onboarding.LoginUser).Methods("POST")
	r.HandleFunc("/logout", onboarding.LogoutUser).Methods("POST")
	r.HandleFunc("/profile/fetch", profile.FetchProfile).Methods("POST")
	r.HandleFunc("/profile/refresh", profile.RefreshProfile).Methods("POST")
	r.HandleFunc("/profile/update", profile.UpdateProfile).Methods("POST")
	r.HandleFunc("/profile/updateHousehold", household.UpdateHousehold).Methods("POST")
	r.HandleFunc("/profile/addToHousehold", membership.AddToHousehold).Methods("POST")
	r.HandleFunc("/profile/addToNeighbourhood", membership.AddToNeighbourhood).Methods("POST")
	r.HandleFunc("/profile/updateNeighbourhood", neighbourhood.UpdateNeighbourhood).Methods("POST")
	r.HandleFunc("/profile/upload", media.UploadImage).Methods("POST")
	r.HandleFunc("/gps/log", household.LogGpsLocation).Methods("POST")
	r.HandleFunc("/gps/heatmap", household.GetGpsHeatmap).Methods("GET")
	r.HandleFunc("/gps/candidate", household.GetGpsCandidate).Methods("GET")
	r.HandleFunc("/gps/clear", household.ClearGpsData).Methods("GET")
	r.HandleFunc("/gps/resetHouseholdLocation", household.ResetHouseholdLocation).Methods("GET")
	r.HandleFunc("/gps/acceptCandidate", household.AcceptHouseholdLocation).Methods("POST")
	fmt.Println("Starting server on :8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
