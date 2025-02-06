package content

import (
	"api/entity"
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func Synchronise(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	sinceTs := r.Header.Get("If-Modified-Since")

	//get all the ids of neighbourhoods the user belongs to
	var neighbourhoodids = ""
	rows, err := utility.DB.Query("SELECT DISTINCT neighbourhood_household_users_add_numerics_0 FROM neighbourhood_household_users WHERE neighbourhood_household_users_add_numerics_2 = ?", userId)
	if err != nil {
		http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
		return
	}
	defer rows.Close()
	for rows.Next() {
		var neighbourhoodid string
		rows.Scan(&neighbourhoodid)
		neighbourhoodids += neighbourhoodid + ","
	}

	//add the 0 neighbourhood --- this is future reserved for things like system message items
	neighbourhoodids += "0"

	var response entity.SyncResponse

	response.Items, response.Itemids, err = GetItemsFromNeighbourhoods(userId, neighbourhoodids, sinceTs)
	if err != nil {
		http.Error(w, "Database error getting items "+err.Error(), http.StatusInternalServerError)
		return
	}
	response.Households, response.Householdids, err = GetHouseholdsFromNeighbourhoods(neighbourhoodids, sinceTs)
	if err != nil {
		http.Error(w, "Database error getting households "+err.Error(), http.StatusInternalServerError)
		return
	}
	response.Users, response.Userids, err = GetUsrersFromNeighbourhoods(neighbourhoodids, sinceTs)
	if err != nil {
		http.Error(w, "Database error getting users "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}
