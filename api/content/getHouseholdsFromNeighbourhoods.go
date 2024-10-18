package content

import (
	"api/entity"
	"api/utility"
)

func GetHouseholdsFromNeighbourhoods(neighbourhoodids string, sinceTs string) ([]entity.Household, []int, error) {
	//get all the house ids that still exist from the neighbourhoods
	sql := `SELECT 
				H.households_id
			FROM 				
				households H 		
			WHERE EXISTS(SELECT
							NHU.neighbourhood_household_users_id 
						FROM 
							neighbourhood_household_users NHU
						WHERE 
							NHU.neighbourhood_household_users_add_numerics_0 IN (?)
						AND 
							NHU.neighbourhood_household_users_add_numerics_1 = H.households_id
						)`

	houseRows, err := utility.DB.Query(sql, neighbourhoodids)
	if err != nil {
		return nil, nil, err
	}
	var houseIds []int
	defer houseRows.Close()
	for houseRows.Next() {
		var houseId int
		houseRows.Scan(&houseId)
		houseIds = append(houseIds, houseId)
	}

	//get all the houses from the neighbourhoods
	sql = `SELECT 
				H.households_id,
				H.households_titlu_EN,
				H.households_text_EN AS Householdabout,
				H.households_pic AS HouseholdImageURL,
				H.households_add_numerics_0 AS HeadID,
				H.households_add_numerics_1 / ? AS Latitude,
				H.households_add_numerics_2 / ? AS Longitude,
				H.households_add_strings_0 AS Address,
				H.households_data AS lastModifTs
			FROM 				
				households H 
			WHERE EXISTS(SELECT
							NHU.neighbourhood_household_users_id 
						FROM 
							neighbourhood_household_users NHU
						WHERE 
							NHU.neighbourhood_household_users_add_numerics_0 IN (?)
						AND 
							NHU.neighbourhood_household_users_add_numerics_1 = H.households_id
						)`

	// If we have a valid sinceTs, we only get the items that have been modified since then
	if sinceTs != "" {
		sql += " AND H.households_data > " + sinceTs
	}
	houseRows, err = utility.DB.Query(sql, utility.GpsPrecisionFactor, utility.GpsPrecisionFactor, neighbourhoodids)
	if err != nil {
		return nil, nil, err
	}

	var houses []entity.Household
	defer houseRows.Close()

	for houseRows.Next() {
		var house entity.Household
		houseRows.Scan(&house.Householdid,
			&house.Name,
			&house.About,
			&house.ImageURL,
			&house.HeadID,
			&house.Latitude,
			&house.Longitude,
			&house.Address,
			&house.LastModifiedTs)

		houses = append(houses, house)
	}

	return houses, houseIds, nil
}
