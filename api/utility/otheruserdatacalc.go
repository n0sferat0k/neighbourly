package utility

import (
	"api/entity"
)

func RetreiveUserData(userId int64, userName string) (*entity.User, error) {
	var existingUser entity.User
	var existingHousehold entity.Household

	err := DB.QueryRow(`SELECT 	
		U.users_id AS userid,
		U.users_text_EN AS userabout,
		U.users_titlu_EN AS fullname,
		U.users_pic AS  ImageURL,
		U.users_add_strings_0 AS Username,		
		U.users_add_strings_2 AS Phone,
		U.users_add_strings_3 AS Email,

		H.households_id,
		H.households_titlu_EN,
		H.households_add_numerics_1 / ? AS Latitude,
		H.households_add_numerics_2 / ? AS Longitude,
		H.households_add_numerics_0 AS HeadID

		FROM 
			users U 
		LEFT JOIN 
			households H 
		ON 
			users_add_numerics_0 = households_id	
		WHERE 
			U.users_id = ?			
		AND 
			U.users_add_strings_0 = ?
		LIMIT 1`,
		GpsPrecisionFactor, GpsPrecisionFactor, userId, userName,
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
		&existingHousehold.Latitude,
		&existingHousehold.Longitude,
		&existingHousehold.HeadID,
	)

	if existingHousehold.Householdid != nil {
		rows, err := DB.Query(`SELECT 	
									U.users_id AS userid,
									U.users_add_strings_0 AS Username,									
									U.users_titlu_EN AS fullname,
									U.users_pic AS  ImageURL	
								FROM 
									users U 
								WHERE 
									U.users_add_numerics_0 = ?`, existingHousehold.Householdid)

		if err != nil {
			return nil, err
		}

		defer rows.Close()

		var houseMembers []entity.User
		for rows.Next() {
			var houseMember entity.User
			err := rows.Scan(
				&houseMember.Userid,
				&houseMember.Username,
				&houseMember.Fullname,
				&houseMember.ImageURL,
			)
			if err != nil {
				return nil, err
			}

			houseMembers = append(houseMembers, houseMember)
		}

		existingHousehold.Members = houseMembers
		existingUser.Household = &existingHousehold
	}

	if err != nil {
		return nil, err
	}

	return &existingUser, nil
}
