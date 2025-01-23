package utility

import "api/entity"

func RetreiveSessionUserData(userId string) (*entity.User, error) {
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
		U.users_add_numerics_0 AS householdid,

		H.households_id,
		H.households_titlu_EN,
		H.households_text_EN AS Householdabout,
		H.households_pic AS HouseholdImageURL,
		H.households_add_numerics_0 AS HeadID,
		ROUND(H.households_add_numerics_1 / ?, 6) AS Latitude,
		ROUND(H.households_add_numerics_2 / ?, 6) AS Longitude,
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
		GpsPrecisionFactor, GpsPrecisionFactor, userId,
	).Scan(
		&existingUser.Userid,
		&existingUser.Userabout,
		&existingUser.Fullname,
		&existingUser.ImageURL,
		&existingUser.Username,
		&existingUser.Phone,
		&existingUser.Email,
		&existingUser.Householdid,

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
		err := DB.QueryRow(`SELECT 
				COUNT(*) / ? AS gpsCnt
			FROM				
				coordinates
			WHERE 
				coordinates_add_numerics_0 = ?	
			AND
				(
					TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '`+NightStart+`' AND '23:59:59'
				OR 
					TIME(ADDTIME(FROM_UNIXTIME(coordinates_data), SEC_TO_TIME(coordinates_add_numerics_3 * 3600))) BETWEEN '00:00:00' AND '`+NightEnd+`'
				)
			LIMIT 1`,
			GpsSampleTarget, userId,
		).Scan(
			&existingHousehold.GpsProgress,
		)

		if err != nil {
			return nil, err
		}

		memberRows, err := DB.Query(`SELECT 	
									U.users_id AS userid,
									U.users_text_EN AS userabout,
									U.users_titlu_EN AS fullname,
									U.users_pic AS  ImageURL,
									U.users_add_strings_0 AS Username,		
									U.users_add_strings_2 AS Phone,
									U.users_add_strings_3 AS Email		
								FROM 
									users U 
								WHERE 
									U.users_add_numerics_0 = ?`, existingHousehold.Householdid)

		if err != nil {
			return nil, err
		}

		defer memberRows.Close()

		var houseMembers []entity.User
		for memberRows.Next() {
			var houseMember entity.User
			err := memberRows.Scan(
				&houseMember.Userid,
				&houseMember.Userabout,
				&houseMember.Fullname,
				&houseMember.ImageURL,
				&houseMember.Username,
				&houseMember.Phone,
				&houseMember.Email,
			)
			if err != nil {
				return nil, err
			}

			houseMembers = append(houseMembers, houseMember)
		}
		existingHousehold.Members = houseMembers

		boxRows, err := DB.Query(`SELECT 	
									B.boxes_titlu_EN AS name,
									B.boxes_text_EN AS id
								FROM 
									boxes B 
								WHERE 
									B.boxes_add_numerics_0 = ?`, existingHousehold.Householdid)

		if err != nil {
			return nil, err
		}

		defer boxRows.Close()

		var houseBoxes []entity.Box
		for boxRows.Next() {
			var houseBox entity.Box
			err := boxRows.Scan(
				&houseBox.Name,
				&houseBox.Id,
			)
			if err != nil {
				return nil, err
			}

			houseBoxes = append(houseBoxes, houseBox)
		}
		existingHousehold.Boxes = houseBoxes

		existingUser.Household = &existingHousehold
	}

	rows, err := DB.Query(`SELECT 
		N.neighbourhoods_id AS id,
		N.neighbourhoods_titlu_EN AS name,
		N.neighbourhoods_text_EN AS geofence,
		N.neighbourhoods_add_numerics_0 AS latitude,
		N.neighbourhoods_add_numerics_1 AS longitude,
		NHU.neighbourhood_household_users_add_numerics_3 AS access,
		
		P.users_id AS parentId,
		P.users_add_strings_0 AS Username,
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

	var neighbourhoods []entity.Neighbourhood
	for rows.Next() {
		var neighbourhood entity.Neighbourhood
		var parent entity.User
		err := rows.Scan(
			&neighbourhood.Neighbourhoodid,
			&neighbourhood.Name,
			&neighbourhood.Geofence,
			&neighbourhood.Latitude,
			&neighbourhood.Longitude,
			&neighbourhood.Access,

			&parent.Userid,
			&parent.Username,
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
