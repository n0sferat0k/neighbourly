package box

import (
	"api/entity"
	"api/utility"
	"context"
	"net/http"
)

func OpBox(w http.ResponseWriter, r *http.Request) {
	var userId string
	var token string
	var box entity.Box

	ctx := context.WithValue(r.Context(), utility.CtxKeyContinue, true)
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r, &userId, &token)
	ctx = utility.RequirePayload(ctx, w, r, &box)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	//find the household where the user is the head
	var householdId int64
	err := utility.DB.QueryRow(`SELECT users_add_numerics_0 FROM users WHERE users_id = ? LIMIT 1`, userId).Scan(&householdId)
	if err != nil {
		http.Error(w, "Only household members may control Boxes"+err.Error(), http.StatusUnauthorized)
		return
	}

	var boxVerifiedId string
	err = utility.DB.QueryRow(`SELECT boxes_text_EN FROM boxes WHERE boxes_text_EN = ? AND boxes_add_numerics_0 = ? LIMIT 1`, box.Id, householdId).Scan(&boxVerifiedId)
	if err != nil {
		http.Error(w, "You do not have access to this Box"+err.Error(), http.StatusUnauthorized)
		return
	}

	mqttToken := utility.MqttClient.Publish("neighbourlybox/"+boxVerifiedId+"/command", 0, false, *box.Command)
	mqttToken.Wait()

	if mqttToken.Error() != nil {
		http.Error(w, "Failed to send command to Box "+mqttToken.Error().Error(), http.StatusInternalServerError)
		return
	}

	//return success
	w.WriteHeader(http.StatusOK)
}
