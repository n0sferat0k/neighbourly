package content

import (
	"api/entity"
	"api/utility"
)

func GetUsrersFromNeighbourhoods(neighbourhoodids string, sinceTs string) ([]entity.User, error) {
	//get all the items from the neighbourhoods

	sql := `SELECT 	
				U.users_id AS userid,
				U.users_text_EN AS userabout,
				U.users_titlu_EN AS fullname,
				U.users_pic AS  ImageURL,
				U.users_add_strings_0 AS Username,		
				U.users_add_strings_2 AS Phone,
				U.users_add_strings_3 AS Email,
				U.users_add_numerics_0 AS Householdid,
				U.users_data AS lastModifTs
			FROM 
				users U 
			WHERE EXISTS(SELECT 
							neighbourhood_household_users_id 
						FROM 
							neighbourhood_household_users NHU
						WHERE 
							NHU.neighbourhood_household_users_add_numerics_0 IN (?)
						AND 
							NHU.neighbourhood_household_users_add_numerics_2 = U.users_id
						)
			`

	// If we have a valid sinceTs, we only get the items that have been modified since then
	if sinceTs != "" {
		sql += " AND U.users_data > " + sinceTs
	}
	userRows, err := utility.DB.Query(sql, neighbourhoodids)
	if err != nil {
		return nil, err
	}

	var users []entity.User
	defer userRows.Close()

	for userRows.Next() {
		var user entity.User
		userRows.Scan(&user.Userid,
			&user.Userabout,
			&user.Fullname,
			&user.ImageURL,
			&user.Username,
			&user.Phone,
			&user.Email,
			&user.Householdid,
			&user.LastModifiedTs)

		users = append(users, user)
	}

	return users, nil
}
