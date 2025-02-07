package main

import (
	"fmt"
	"log"
	"net/http"

	"api/box"
	"api/content"
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
	defer utility.DB.Close()

	utility.ConnectMQTT()
	defer utility.MqttClient.Disconnect(250)

	//request handlers
	r := mux.NewRouter()
	r.HandleFunc("/register", onboarding.RegisterUser).Methods("POST")
	r.HandleFunc("/login", onboarding.LoginUser).Methods("POST")
	r.HandleFunc("/logout", onboarding.LogoutUser).Methods("POST")
	r.HandleFunc("/forgot", onboarding.ForgotPassword).Methods("POST")
	r.HandleFunc("/profile/fetch", profile.FetchProfile).Methods("POST")
	r.HandleFunc("/profile/refresh", profile.RefreshProfile).Methods("POST")
	r.HandleFunc("/profile/update", profile.UpdateProfile).Methods("POST")
	r.HandleFunc("/profile/updateHousehold", household.UpdateHousehold).Methods("POST")
	r.HandleFunc("/profile/leaveHousehold", household.LeaveHousehold).Methods("POST")
	r.HandleFunc("/profile/addToHousehold", membership.AddToHousehold).Methods("POST")
	r.HandleFunc("/profile/addToNeighbourhood", membership.AddToNeighbourhood).Methods("POST")
	r.HandleFunc("/profile/updateNeighbourhood", neighbourhood.UpdateNeighbourhood).Methods("POST")
	r.HandleFunc("/profile/leaveNeighbourhood", neighbourhood.LeaveNeighbourhood).Methods("POST")
	r.HandleFunc("/files/upload", media.UploadFile).Methods("POST")
	r.HandleFunc("/files/delete", media.DeleteFile).Methods("GET")
	r.HandleFunc("/gps/log", household.LogGpsLocation).Methods("POST")
	r.HandleFunc("/gps/heatmap", household.GetGpsHeatmap).Methods("GET")
	r.HandleFunc("/gps/candidate", household.GetGpsCandidate).Methods("GET")
	r.HandleFunc("/gps/clear", household.ClearGpsData).Methods("GET")
	r.HandleFunc("/gps/resetHouseholdLocation", household.ResetHouseholdLocation).Methods("GET")
	r.HandleFunc("/gps/acceptCandidate", household.AcceptHouseholdLocation).Methods("POST")
	r.HandleFunc("/content/sync", content.Synchronise).Methods("GET")
	r.HandleFunc("/content/delItem", content.DeleteItem).Methods("GET")
	r.HandleFunc("/content/addOrUpdateItem", content.AddOrUpdateItem).Methods("POST")
	r.HandleFunc("/content/addItemMessage", content.AddItemMessage).Methods("POST")
	r.HandleFunc("/content/deleteItemMessage", content.DeleteItemMessage).Methods("GET")
	r.HandleFunc("/content/getItemsMessages", content.GetItemsMessages).Methods("POST")
	r.HandleFunc("/box/addOrUpdateBox", box.AddOrUpdateBox).Methods("POST")
	r.HandleFunc("/box/delBox", box.DelBox).Methods("POST")
	r.HandleFunc("/box/opBox", box.OpBox).Methods("POST")
	fmt.Println("Starting server on :8080")

	log.Fatal(http.ListenAndServe(":8080", r))
}
