package household

import (
	"api/utility"
	"encoding/json"
	"net/http"
	"strconv"
)

func GetGpsHeatmap(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequireGet(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)

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
