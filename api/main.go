package main

import (
	"database/sql"
	"fmt"
	"log"
	"net/http"

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
)

var db *sql.DB

func main() {
	//database connection
	var err error
	if db, err = sql.Open("mysql", "root:qwerty1234@tcp(localhost:3306)/neighbourly"); err != nil {
		panic(err)
	}
	if err := db.Ping(); err != nil {
		panic(err)
	}
	defer db.Close()

	//request handlers
	r := mux.NewRouter()
	r.HandleFunc("/register", RegisterUser).Methods("POST")
	r.HandleFunc("/login", LoginUser).Methods("POST")
	r.HandleFunc("/logout", LogoutUser).Methods("POST")
	r.HandleFunc("/profile/refresh", RefreshProfile).Methods("POST")
	r.HandleFunc("/profile/update", UpdateProfile).Methods("POST")
	r.HandleFunc("/profile/updateHousehold", UpdateHousehold).Methods("POST")
	r.HandleFunc("/profile/upload", UploadImage).Methods("POST")
	r.HandleFunc("/gps/log", LogGpsLocation).Methods("POST")
	r.HandleFunc("/gps/heatmap", GetGpsHeatmap).Methods("GET")
	r.HandleFunc("/gps/candidate", GetGpsCandidate).Methods("GET")
	fmt.Println("Starting server on :8080")
	log.Fatal(http.ListenAndServe(":8080", r))
}
