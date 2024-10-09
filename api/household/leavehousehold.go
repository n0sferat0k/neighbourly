package household

import (
	"api/utility"
	"context"
	"database/sql"
	"net/http"
)

func LeaveHousehold(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//find the household where the user is the head
	var householdId int64
	err := utility.DB.QueryRow(`SELECT households_id FROM households WHERE households_add_numerics_0 = ? LIMIT 1`, userId).Scan(&householdId)
	if err != nil {
		if err != sql.ErrNoRows {
			householdId = -1
		} else {
			http.Error(w, "Failed to retrieve owned household"+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	//if the user has a household where he is the head, do the inheritance
	if householdId > 0 {
		//find the heir
		var heirId int = 0
		err = utility.DB.QueryRow(`SELECT users_id FROM users WHERE users_add_numerics_0 = ? AND users_id != ? ORDER BY users_id ASC LIMIT 1`, householdId, userId).Scan(&heirId)
		if err != nil && err != sql.ErrNoRows {
			http.Error(w, "Failed to inherit household"+err.Error(), http.StatusInternalServerError)
			return
		}

		if heirId > 0 {
			//if found, make the heir the head of the household
			_, err = utility.DB.Exec(`UPDATE households SET households_data = UNIX_TIMESTAMP(), households_add_numerics_0 = ? WHERE households_id = ?`, heirId, householdId)
			if err != nil {
				http.Error(w, "Failed to inherit household"+err.Error(), http.StatusInternalServerError)
				return
			}
		} else {
			//if no heir found, delete the household
			_, err = utility.DB.Exec(`DELETE FROM households WHERE households_id = ?`, householdId)
			if err != nil {
				http.Error(w, "Failed to delete household"+err.Error(), http.StatusInternalServerError)
				return
			}
		}
	}

	//remove the user from the household
	_, err = utility.DB.Exec(`UPDATE users SET users_add_numerics_0 = -1 WHERE users_id = ?`, userId)
	if err != nil {
		http.Error(w, "Failed to remove household from user"+err.Error(), http.StatusInternalServerError)
		return
	}

	//find all neighbourhoods that the user is part of
	rows, err := utility.DB.Query(`SELECT DISTINCT neighbourhood_household_users_add_numerics_0 AS neighbourhoodid FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_2 =  ?`, userId)
	if err != nil {
		http.Error(w, "Failed to find neighbourhoods"+err.Error(), http.StatusInternalServerError)
		return
	} else {
		defer rows.Close()
		var neighbourhoodid int64

		//remove the user from all neighbourhoods and make sure neighbourhoods are inherited as well
		for rows.Next() {
			err := rows.Scan(&neighbourhoodid)
			if err != nil {
				http.Error(w, "Failed to remove user from neighbourhood "+err.Error(), http.StatusInternalServerError)
				return
			}

			err = utility.LeaveNeighbourhoodWithHousehold(userId, -1, neighbourhoodid)
			if err != nil {
				http.Error(w, err.Error(), http.StatusInternalServerError)
				return
			}
		}
	}

	utility.ReturnSelfSession(userId, w, r, nil)
}
