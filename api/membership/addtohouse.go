package membership

import (
	"api/entity"
	"api/utility"
	"net/http"
)

func AddToHousehold(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	ctx = utility.RequirePayloadUser(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)
	familyMember := ctx.Value("user").(entity.User)

	var familyMemberFound bool = false
	utility.DB.QueryRow("SELECT COUNT(*) > 0 AS found FROM users WHERE users_add_numerics_0 = 0 AND users_id = ? AND users_add_strings_0 = ?", familyMember.Userid, familyMember.Username).Scan(&familyMemberFound)
	if !familyMemberFound {
		http.Error(w, "User not found, or already added to household", http.StatusNotFound)
		return
	}

	var householdId int
	utility.DB.QueryRow("SELECT users_add_numerics_0 FROM users WHERE users_id = ?", userId).Scan(&householdId)
	if householdId == 0 {
		http.Error(w, "You are not part of a household", http.StatusForbidden)
		return
	}

	_, err := utility.DB.Exec(`UPDATE users SET users_add_numerics_0 = ? WHERE users_add_numerics_0 = 0 AND users_id = ? AND users_add_strings_0 = ?`,
		householdId, familyMember.Userid, familyMember.Username)
	if err != nil {
		http.Error(w, "Failed to add user to household "+err.Error(), http.StatusInternalServerError)
		return
	}

	rows, err := utility.DB.Query(`SELECT 
				neighbourhood_household_users_add_numerics_0 AS neighbourhoodid,
				neighbourhood_household_users_add_numerics_3 AS access
	 		FROM 
				neighbourhood_household_users
			WHERE 
				neighbourhood_household_users_add_numerics_1 = ? 
			AND 
				neighbourhood_household_users_add_numerics_2 = ?`, householdId, userId)
	if err != nil {
		http.Error(w, "Failed to add user to neighbourhoods "+err.Error(), http.StatusInternalServerError)
		return
	}

	defer rows.Close()

	var neighbourhoodId int
	var access int
	for rows.Next() {
		err := rows.Scan(&neighbourhoodId, &access)
		if err != nil {
			http.Error(w, "Failed to add user to neighbourhoods "+err.Error(), http.StatusInternalServerError)
			return
		}

		_, err = utility.DB.Exec(`INSERT INTO neighbourhood_household_users (
							neighbourhood_household_users_add_numerics_0, 
							neighbourhood_household_users_add_numerics_1, 
							neighbourhood_household_users_add_numerics_2, 
							neighbourhood_household_users_add_numerics_3,
							neighbourhood_household_users_add_numerics_4) 
						VALUES (?,?,?,?,?)`,
			neighbourhoodId, householdId, familyMember.Userid, access-1, userId)
		if err != nil {
			http.Error(w, "Failed to add user to neighbourhoods "+err.Error(), http.StatusInternalServerError)
			return
		}
	}

	utility.ReturnSelfSession(ctx, w, r, nil)
}
