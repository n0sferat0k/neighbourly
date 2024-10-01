package household

import (
	"api/entity"
	"api/utility"
	"encoding/json"
	"net/http"
	"time"
)

func LogGpsLocation(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	userId := ctx.Value("userId").(string)

	var gps entity.GpsPayload
	if err := json.NewDecoder(r.Body).Decode(&gps); err != nil {
		http.Error(w, "Bad request", http.StatusBadRequest)
		return
	}

	_, err := utility.DB.Exec("INSERT INTO coordinates (coordinates_data, coordinates_add_numerics_0, coordinates_add_numerics_1, coordinates_add_numerics_2, coordinates_add_numerics_3) VALUES (?, ?, ?, ?, ?)",
		time.Now().Unix(),
		userId,
		*gps.Latitude*float64(utility.GpsPrecisionFactor),
		*gps.Longitude*float64(utility.GpsPrecisionFactor),
		gps.Timezone)
	if err != nil {
		http.Error(w, "Failed to store location"+err.Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
