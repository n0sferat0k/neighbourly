package main

import (
	"database/sql"
	"log"
	"net/http"

	_ "github.com/go-sql-driver/mysql"
	"github.com/gorilla/mux"
)

var db *sql.DB

func main() {
	//database connection
	var err error
	if db, err = sql.Open("mysql", "root:@tcp(localhost:3306)/neighbourly"); err != nil {
		log.Fatal(err)
	}
	if err := db.Ping(); err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	//request handlers
	r := mux.NewRouter()
	r.HandleFunc("/register", RegisterUser).Methods("POST")
	r.HandleFunc("/login", LoginUser).Methods("POST")
	log.Fatal(http.ListenAndServe(":8080", r))
}
