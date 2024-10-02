package utility

import "database/sql"

var DB *sql.DB

func ConnectDB() {
	var err error
	if DB, err = sql.Open("mysql", "root:qwerty1234@tcp(localhost:3306)/neighbourly"); err != nil {
		panic(err)
	}
	if err := DB.Ping(); err != nil {
		panic(err)
	}
}
