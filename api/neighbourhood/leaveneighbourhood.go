package neighbourhood

import (
	"api/entity"
	"api/utility"
	"context"
	"net/http"
)

func LeaveNeighbourhood(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var neighbourhood entity.Neighbourhood

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &neighbourhood)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//find the household of the user and make sure he is the head of the household

	var householdId int64
	err := utility.DB.QueryRow(`SELECT households_id FROM households where households_add_numerics_0 = ?`, userId).Scan(&householdId)
	if err != nil {
		http.Error(w, "Only household head may leave neighbourhood"+err.Error(), http.StatusInternalServerError)
		return
	}

	err = utility.LeaveNeighbourhoodWithHousehold(userId, householdId, *neighbourhood.Neighbourhoodid)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	utility.ReturnSelfSession(userId, w, r, nil)
}
