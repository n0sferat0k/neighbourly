package household

import (
	"api/utility"
	"context"
	"encoding/json"
	"net/http"
	"strconv"
)

func GetGpsHeatmap(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	onlyNightParam := r.URL.Query().Get("onlyNight")
	onlyNight, err := strconv.ParseBool(onlyNightParam)
	if err != nil {
		onlyNight = false
	}

	gpsPayloads, err := utility.RetrieveHeatmap(userId, onlyNight)
	if err != nil {
		http.Error(w, "Failed to get heatmap data "+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(gpsPayloads)
}
