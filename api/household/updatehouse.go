package household

import (
	"api/entity"
	"api/utility"
	"net/http"
)

func UpdateHousehold(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	ctx = utility.RequirePayloadHousehold(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)
	household := ctx.Value("household").(entity.Household)

	var householdId int
	utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
	if householdId > 0 {
		// Update the household in the database
		_, err := utility.DB.Exec(`UPDATE households SET households_titlu_EN = ?, households_add_strings_0 = ?, households_text_EN = ? WHERE households_id = ? AND households_add_numerics_0 = ?`,
			household.Name, household.Address, household.About, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update household "+err.Error(), http.StatusInternalServerError)
			return
		}
	} else {
		//Insert the household into the database
		insertResult, err := utility.DB.Exec(`INSERT INTO households (households_add_numerics_0, households_titlu_EN, households_add_strings_0, households_text_EN, households_pic) VALUES  (?,?,?,?,'')`,
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

		_, err = utility.DB.Exec(`UPDATE users SET users_add_numerics_0 = ? WHERE users_id = ?`, householdId, userId)

		if err != nil {
			http.Error(w, "Failed to update user with household "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	utility.ReturnSelfSession(ctx, w, r, nil)
}
