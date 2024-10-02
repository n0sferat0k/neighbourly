package household

import (
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
)

func GetGpsCandidate(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	gpsPayloads, err := utility.RetrieveHeatmap(userId, true)
	if err != nil {
		http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
		return
	}

	candidate, err := utility.FindLargestCluserLocation(gpsPayloads)
	if err != nil {
		http.Error(w, "Failed to get household candidate "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(candidate)
}
