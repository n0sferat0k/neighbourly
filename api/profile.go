package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
)

const TARGET_PROFILE = "profile"
const TARGET_HOUSEHOLD = "household"

func RetreiveSessionUserData(userId string) (*User, error) {
	var existingUser User
	var existingHousehold Household

	err := db.QueryRow(`SELECT 	U.users_id,
		U.users_text_EN,
		U.users_titlu_EN,
		U.users_pic,
		U.users_add_strings_0,		
		U.users_add_strings_2,
		U.users_add_strings_3,

		H.households_id,
		H.households_titlu_EN,
		H.households_text_EN AS Householdabout,
		H.households_pic AS HouseholdImageURL,
		H.households_add_numerics_0 AS HeadID,
		H.households_add_numerics_1 / ? AS Latitude,
		H.households_add_numerics_2 / ? AS Longitude,
		H.households_add_strings_0 AS Address

		FROM 
			users U 
		LEFT JOIN 
			households H 
		ON 
			users_add_numerics_0 = households_id		
		WHERE 
			U.users_id = ?			
		LIMIT 1`,
		gpsPrecisionFactor, gpsPrecisionFactor, userId,
	).Scan(
		&existingUser.Userid,
		&existingUser.Userabout,
		&existingUser.Fullname,
		&existingUser.ImageURL,
		&existingUser.Username,
		&existingUser.Phone,
		&existingUser.Email,

		&existingHousehold.Householdid,
		&existingHousehold.Name,
		&existingHousehold.About,
		&existingHousehold.ImageURL,
		&existingHousehold.HeadID,
		&existingHousehold.Latitude,
		&existingHousehold.Longitude,
		&existingHousehold.Address,
	)

	if err != nil {
		return nil, err
	}

	if existingHousehold.Householdid != nil {
		err := db.QueryRow(`SELECT 
				COUNT(*) / ? AS gpsCnt
			FROM				
				coordinates
			WHERE 
				coordinates_add_numerics_0 = ?	
			AND
				(
					TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '`+nightStart+`' AND '23:59:59'
				OR 
					TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '00:00:00' AND '`+nightEnd+`'
				)
			LIMIT 1`,
			gpsSampleTarget, userId,
		).Scan(
			&existingHousehold.GpsProgress,
		)

		if err != nil {
			return nil, err
		}

		existingUser.Household = &existingHousehold
	}

	rows, err := db.Query(`SELECT 
		N.neighbourhoods_id AS id,
		N.neighbourhoods_titlu_EN AS name,
		N.neighbourhoods_text_EN AS geofence,
		N.neighbourhoods_add_numerics_0 AS latitude,
		N.neighbourhoods_add_numerics_1 AS longitude,
		NHU.neighbourhood_household_users_add_numerics_3 AS access,
		
		P.users_id AS parentId,
		P.users_text_EN AS parentAbout,
		P.users_titlu_EN AS parentFullname,
		P.users_pic AS parentImageURL,		
		P.users_add_strings_2 AS parentPhone,
		P.users_add_strings_3 AS parentEmail

		FROM
			neighbourhood_household_users NHU
		LEFT JOIN 
			neighbourhoods N
		ON 
			N.neighbourhoods_id = NHU.neighbourhood_household_users_add_numerics_0 
		LEFT JOIN 
			households H
		ON
			H.households_id = NHU.neighbourhood_household_users_add_numerics_1
		LEFT JOIN 
			users U
		ON
			U.users_id = NHU.neighbourhood_household_users_add_numerics_2			
		LEFT JOIN 
			users P
		ON
			P.users_id = NHU.neighbourhood_household_users_add_numerics_4
		WHERE 
			U.users_id = ?`,
		userId,
	)

	if err != nil {
		return nil, err
	}

	defer rows.Close()

	var neighbourhoods []Neighbourhood
	for rows.Next() {
		var neighbourhood Neighbourhood
		var parent User
		err := rows.Scan(
			&neighbourhood.Neighbourhoodid,
			&neighbourhood.Name,
			&neighbourhood.Geofence,
			&neighbourhood.Latitude,
			&neighbourhood.Longitude,
			&neighbourhood.Access,

			&parent.Userid,
			&parent.Userabout,
			&parent.Fullname,
			&parent.ImageURL,
			&parent.Phone,
			&parent.Email,
		)

		if err != nil {
			return nil, err
		}

		if parent.Userid != nil {
			neighbourhood.Parent = &parent
		}

		neighbourhoods = append(neighbourhoods, neighbourhood)
	}

	existingUser.Neighbourhoods = neighbourhoods
	return &existingUser, nil
}

func UpdateHousehold(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	var household Household
	if err := json.NewDecoder(r.Body).Decode(&household); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	var householdId int
	db.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
	if householdId > 0 {
		// Update the household in the database
		_, err := db.Exec(`UPDATE households SET households_titlu_EN = ?, households_add_strings_0 = ?, households_text_EN = ? WHERE households_id = ?`,
			household.Name, household.Address, household.About, householdId)

		if err != nil {
			http.Error(w, "Failed to update household "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		//Insert the household into the database
		insertResult, err := db.Exec(`INSERT INTO households (households_titlu_EN, households_add_strings_0, households_text_EN, households_pic) VALUES  (?,?,?,'')`,
			household.Name, household.Address, household.About)

		if err != nil {
			http.Error(w, "Failed to insert household "+err.Error(), http.StatusInternalServerError)
			return
		}

		householdId, err := insertResult.LastInsertId()

		if err != nil {
			http.Error(w, "Failed to get inserted household "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = db.Exec(`UPDATE users SET users_add_numerics_0 = ? WHERE users_id = ?`, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update user with household "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	existingUser, err := RetreiveSessionUserData(userId)
	if err != nil {
		http.Error(w, "Failed to get user info for "+userId+":"+err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}

func UpdateProfile(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	var user User
	if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	// Update the user in the database
	_, err := db.Exec(`UPDATE users SET users_titlu_EN = ?, users_add_strings_3 = ?, users_add_strings_2 =  ?,  users_text_EN = ? WHERE users_id = ?`,
		user.Fullname, user.Email, user.Phone, user.Userabout, userId)
	if err != nil {
		http.Error(w, "Failed to register user "+err.Error(), http.StatusInternalServerError)
		return
	}

	existingUser, err := RetreiveSessionUserData(userId)
	if err != nil {
		http.Error(w, "Failed to get user info "+err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}

func RefreshProfile(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	existingUser, err := RetreiveSessionUserData(userId)
	if err != nil {
		http.Error(w, "Failed to get user info "+err.Error(), http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}

func UploadImage(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	var userId string = validateToken(w, r)
	if userId == "" {
		return
	}

	target := r.URL.Query().Get("target")
	var targetFolder string
	var targetFilePrefix string
	var oldUserImg string
	var updateQuery string

	if target == TARGET_PROFILE {
		targetFolder = "usersIMGS"
		targetFilePrefix = "profile_" + userId
		db.QueryRow("SELECT users_pic FROM users WHERE users_id = ?", userId).Scan(&oldUserImg)
		updateQuery = "UPDATE users SET users_pic = ? WHERE users_id = " + userId
	} else {
		var householdId string
		db.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
		targetFolder = "householdsIMGS"
		targetFilePrefix = "household_" + householdId
		db.QueryRow("SELECT households_pic FROM households WHERE households_id = ?", householdId).Scan(&oldUserImg)
		updateQuery = "UPDATE households SET households_pic = ? WHERE households_id = " + householdId
	}

	// Parse the multipart form data
	if err := r.ParseMultipartForm(10 << 20); err != nil {
		http.Error(w, "Failed to parse multipart form", http.StatusBadRequest)
		return
	}

	// Retrieve the file from form data
	file, handler, err := r.FormFile("image")
	if err != nil {
		http.Error(w, "Failed to retrieve file", http.StatusBadRequest)
		return
	}
	defer file.Close()

	wwwRelativeFolder := targetFolder
	apiRelativeFolder := "../www/"
	saveFolder := apiRelativeFolder + wwwRelativeFolder
	var randomString string
	if randomString, err = generageRandomToken(16); err != nil {
		randomString = ""
	}
	destinationFileName := targetFilePrefix + "_" + randomString + filepath.Ext(handler.Filename)
	dbFilePath := filepath.Join(wwwRelativeFolder, destinationFileName)

	// Create a Go routine to save the file
	go func() {
		if _, err := os.Stat("uploads"); os.IsNotExist(err) {
			os.Mkdir("uploads", os.ModePerm)
		}

		if _, err := os.Stat(saveFolder); os.IsNotExist(err) {
			os.Mkdir(saveFolder, os.ModePerm)
		}

		// Delete old file
		delFile := apiRelativeFolder + oldUserImg
		fmt.Println("deleteing file:" + delFile)

		if err := os.Remove(delFile); err != nil {
			fmt.Println("Failed to delete file:", err)
		}
		// Create the destination file

		dst, err := os.Create(filepath.Join(saveFolder, destinationFileName))
		if err != nil {
			fmt.Println("Failed to create file:", err)
			return
		}
		defer dst.Close()

		// Copy the uploaded file to the destination file
		if _, err := io.Copy(dst, file); err != nil {
			fmt.Println("Failed to save file:", err)
			return
		}

		if _, err = db.Exec(updateQuery, dbFilePath); err != nil {
			http.Error(w, "Failed to update target with image", http.StatusInternalServerError)
			return
		}

		fmt.Println("File uploaded successfully:", handler.Filename)
	}()

	w.WriteHeader(http.StatusCreated)
	fmt.Fprintln(w, dbFilePath)
}
