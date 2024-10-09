package household

import (
	"api/entity"
	"api/utility"
	"context"
	"net/http"
)

func UpdateHousehold(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var household entity.Household

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &household)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	var householdId int
	utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
	if householdId > 0 {
		// Update the household in the database
		_, err := utility.DB.Exec(`UPDATE households SET households_data = UNIX_TIMESTAMP(), households_titlu_EN = ?, households_add_strings_0 = ?, households_text_EN = ? WHERE households_id = ? AND households_add_numerics_0 = ?`,
			household.Name, household.Address, household.About, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update household "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		//Insert the household into the database
		insertResult, err := utility.DB.Exec(`INSERT INTO households (households_data, households_add_numerics_0, households_titlu_EN, households_add_strings_0, households_text_EN, households_pic) VALUES  (UNIX_TIMESTAMP(),?,?,?,?,'')`,
			userId, household.Name, household.Address, household.About)

		if err != nil {
			http.Error(w, "Failed to insert household "+err.Error(), http.StatusInternalServerError)
			return
		}

		householdId, err := insertResult.LastInsertId()

		if err != nil {
			http.Error(w, "Failed to get inserted household "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = utility.DB.Exec(`UPDATE users SET users_data = UNIX_TIMESTAMP(), users_add_numerics_0 = ? WHERE users_id = ?`, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update user with household "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	utility.ReturnSelfSession(userId, w, r, nil)
}
