package utility

import (
	"database/sql"
	"errors"
)

func LeaveNeighbourhoodWithHousehold(userId string, householdId int64, neighbourhoodid int64) error {
	//find what acc the user has in the neighbourhood
	var acc int
	err := DB.QueryRow(`SELECT 
									neighbourhood_household_users_add_numerics_3 as acc
								FROM 
									neighbourhood_household_users 
								WHERE 
									neighbourhood_household_users_add_numerics_0 = ? AND neighbourhood_household_users_add_numerics_2 = ?`,
		neighbourhoodid, userId).Scan(&acc)
	if err != nil {
		return errors.New("Not a member of the neighbourhood" + err.Error())
	}

	//if user is creator of neighbourhood, someone should inherit it
	if acc == 500 {
		//find person to inherit neighbourhood, if household is leaving too (householdId != -1), the heir should be someone outside the household
		var heirId int
		err = DB.QueryRow(`SELECT 
										neighbourhood_household_users_add_numerics_2 AS user
									FROM 
										neighbourhood_household_users 
									WHERE 
										neighbourhood_household_users_add_numerics_0 = ?
									AND 
										neighbourhood_household_users_add_numerics_1 != ?
									AND 
										neighbourhood_household_users_add_numerics_2 != ?

									ORDER BY neighbourhood_household_users_add_numerics_3 DESC
									LIMIT 1`,
			neighbourhoodid, householdId, userId).Scan(&heirId)

		if err != nil {
			if err == sql.ErrNoRows {
				//neighbourhood has no more members noone can inherit it and it should be deleted
				_, err = DB.Exec(`DELETE FROM neighbourhoods WHERE neighbourhoods_id = ?`, neighbourhoodid)
				if err != nil {
					return errors.New("Failed to delete empty neighbourhood" + err.Error())
				}
			} else {
				return errors.New("Could not find heir to neighbourhood" + err.Error())
			}
		}

		//if an heir was found, make them the head of the neighbourhood
		if heirId > 0 {
			//make heir the head of the neighbourhood
			_, err = DB.Exec(`UPDATE neighbourhood_household_users SET neighbourhood_household_users_data = UNIX_TIMESTAMP(), neighbourhood_household_users_add_numerics_3 = 500, neighbourhood_household_users_add_numerics_4 = -1 WHERE neighbourhood_household_users_add_numerics_0 = ? AND neighbourhood_household_users_add_numerics_2 = ?`, neighbourhoodid, heirId)
			if err != nil {
				return errors.New("Failed to update heir to head of neighbourhood" + err.Error())
			}
		} else {
			//delete the neighbourhood
			_, err = DB.Exec(`DELETE FROM neighbourhoods WHERE neighbourhoods_id = ?`, neighbourhoodid)
			if err != nil {
				return errors.New("Failed to delete empty neighbourhood" + err.Error())
			}
		}
	}

	//remove household from neighbourhood if specified (householdId != -1)
	if householdId != -1 {
		_, err = DB.Exec(`DELETE FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_0 = ? AND neighbourhood_household_users_add_numerics_1 = ?`,
			neighbourhoodid, householdId)
		if err != nil {
			return errors.New("Failed to remove houosehold from neighbourhood" + err.Error())
		}
	} else {
		//remove self from neighbourhood
		_, err = DB.Exec(`DELETE FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_0 = ? AND neighbourhood_household_users_add_numerics_2 = ?`,
			neighbourhoodid, userId)
		if err != nil {
			return errors.New("Failed to remove houosehold from neighbourhood" + err.Error())
		}
	}
	return nil
}
