package household

import (
	"api/utility"
	"encoding/json"
	"net/http"
)

func AcceptHouseholdLocation(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)

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

	_, err = utility.DB.Exec("UPDATE households SET households_add_numerics_1 = ?,  households_add_numerics_2 = ? WHERE households_add_numerics_0 = ?",
		*candidate.Latitude*float64(utility.GpsPrecisionFactor),
		*candidate.Longitude*float64(utility.GpsPrecisionFactor),
		userId)
	if err != nil {
		http.Error(w, "Failed to store household location "+err.Error(), http.StatusInternalServerError)
		return
	}

	_, err = utility.DB.Exec("DELETE FROM coordinates WHERE coordinates_add_numerics_0 = ?", userId)
	if err != nil {
		http.Error(w, "Failed to clear GPS data"+err.Error(), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(candidate)
}