package box

import (
	"api/utility"
	"context"
	"net/http"
)

func OpBoxQuickOpen(w http.ResponseWriter, r *http.Request) {
	var token string

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequireGet(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}
	token = r.URL.Query().Get("token")

	var boxVerifiedId string
	err := utility.DB.QueryRow(`SELECT boxshares_text_EN FROM boxshares WHERE boxshares_add_strings_0 = ? LIMIT 1`, token).Scan(&boxVerifiedId)

	if err != nil {
		http.Error(w, "Invalid token "+err.Error(), http.StatusUnauthorized)
		return
	}

	mqttToken := utility.MqttClient.Publish("neighbourlybox/"+boxVerifiedId+"/command", 0, false, "OPEN")
	mqttToken.Wait()

	if mqttToken.Error() != nil {
		http.Error(w, "Failed to send command to Box "+mqttToken.Error().Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
