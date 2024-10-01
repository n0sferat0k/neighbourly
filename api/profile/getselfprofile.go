package profile

import (
	"api/utility"
	"net/http"
)

func RefreshProfile(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()
	ctx = utility.RequirePost(ctx, w, r)
	ctx = utility.RequireValidToken(ctx, w, r)
	if ctx.Value(utility.CtxKeyContinue) == false {
		return
	}

	utility.ReturnSelfSession(ctx, w, r, nil)
}
