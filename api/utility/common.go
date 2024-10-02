package utility

import (
	"context"
	"database/sql"
	"encoding/json"
	"net/http"
	"strings"
	"time"
)

type contextKey string

const CtxKeyContinue contextKey = "continue"

func RequirePost(ctx context.Context, w http.ResponseWriter, r *http.Request) context.Context {
	//********************************************************VALIDATION - http method
	if ctx.Value(CtxKeyContinue) == true {
		if r.Method != http.MethodPost {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return context.WithValue(ctx, CtxKeyContinue, false)
		}
	}
	return ctx
}

func RequireGet(ctx context.Context, w http.ResponseWriter, r *http.Request) context.Context {
	//********************************************************VALIDATION - http method
	if ctx.Value(CtxKeyContinue) == true {
		if r.Method != http.MethodGet {
			http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
			return context.WithValue(ctx, CtxKeyContinue, false)
		}
	}
	return ctx
}

func RequireValidToken(ctx context.Context, w http.ResponseWriter, r *http.Request, userId *string, userToken *string) context.Context {
	//********************************************************VALIDATION - token
	if ctx.Value(CtxKeyContinue) == true {
		// Extract the token from the request headers
		token := strings.TrimPrefix(r.Header.Get("Authorization"), "Bearer ")

		if token == "" {
			http.Error(w, "Missing token", http.StatusUnauthorized)
			return context.WithValue(ctx, CtxKeyContinue, false)
		}

		var existingTokenUserId string
		var existingTokenTs int64

		err := DB.QueryRow("SELECT tokens_add_numerics_0, tokens_data FROM tokens WHERE tokens_titlu_EN = ? LIMIT 1", token).Scan(&existingTokenUserId, &existingTokenTs)
		if err != nil {
			if err == sql.ErrNoRows {
				http.Error(w, "Invalid token: "+token, http.StatusUnauthorized)
				return context.WithValue(ctx, CtxKeyContinue, false)
			} else {
				http.Error(w, "Database error "+err.Error(), http.StatusInternalServerError)
				return context.WithValue(ctx, CtxKeyContinue, false)
			}
		}
		if existingTokenTs < time.Now().Unix() {
			http.Error(w, "Expired token", http.StatusUnauthorized)
			return context.WithValue(ctx, CtxKeyContinue, false)
		}

		*userId = existingTokenUserId
		*userToken = token

		return ctx
	}
	return ctx
}

// RequirePayload decodes the JSON payload into the provided type
func RequirePayload(ctx context.Context, w http.ResponseWriter, r *http.Request, v interface{}) context.Context {
	if ctx.Value(CtxKeyContinue) == true {
		if r.Header.Get("Content-Type") != "application/json" {
			http.Error(w, "Unexpected content type", http.StatusBadRequest)
			return context.WithValue(ctx, CtxKeyContinue, false)
		}
		if err := json.NewDecoder(r.Body).Decode(v); err != nil {
			http.Error(w, "Bad request", http.StatusBadRequest)
			return context.WithValue(ctx, CtxKeyContinue, false)
		}
	}
	return ctx
}

func ReturnSelfSession(userId string, w http.ResponseWriter, r *http.Request, authToken *string) {
	existingUser, err := RetreiveSessionUserData(userId)
	if err != nil {
		http.Error(w, "Failed to get user info for "+userId+":"+err.Error(), http.StatusInternalServerError)
		return
	}
	if authToken != nil {
		existingUser.Authtoken = authToken
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(existingUser)
}
