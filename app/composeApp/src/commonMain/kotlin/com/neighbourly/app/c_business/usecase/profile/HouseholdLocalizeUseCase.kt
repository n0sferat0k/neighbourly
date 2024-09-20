package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class HouseholdLocalizeUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun startMonitoring() {
        sessionStore.updateUser { it?.copy(localizing = true) }
    }

    suspend fun stopMonitoring() {
        sessionStore.updateUser { it?.copy(localizing = false) }
    }

    suspend fun reportLocation(
        latitude: Float,
        longitude: Float,
    ) {
        val token = sessionStore.token
        token?.let {
            apiGw.gpsLog(token, -10, latitude, longitude)
        }
    }

    suspend fun fetchGpsLogs() {
        val token = sessionStore.token
        token?.let {
            apiGw.getGpsHeatmap(token).let { heatmap ->
                sessionStore.updateLocalization { it.copy(heatmap = heatmap) }
            }
        }
    }

    suspend fun fetchGpsCandidate() {
        val token = sessionStore.token
        token?.let {
            apiGw.getGpsCandidate(token).let { candidate ->
                sessionStore.updateLocalization { it.copy(candidate = candidate) }
            }
        }
    }

    suspend fun acceptGpsCandidate() {
        val token = sessionStore.token
        token?.let {
            apiGw.acceptGpsCandidate(token).let { gpsItem ->
                sessionStore.updateUser {
                    it?.copy(
                        localizing = false,
                        household =
                            it.household?.copy(
                                gpsprogress = null,
                                location =
                                    Pair(
                                        gpsItem.latitude,
                                        gpsItem.longitude,
                                    ),
                            ),
                    )
                }
                sessionStore.updateLocalization { it.copy(candidate = null, heatmap = null) }
            }
        }
    }

    suspend fun retryMonitoring() {
        val token = sessionStore.token
        token?.let {
            apiGw.clearGpsData(token)
            sessionStore.updateUser {
                it?.copy(
                    localizing = true,
                    household =
                        it.household?.copy(
                            gpsprogress = null,
                        ),
                )
            }
            sessionStore.updateLocalization { it.copy(candidate = null, heatmap = null) }
        }
    }

    suspend fun relocateHousehold() {
        val token = sessionStore.token
        token?.let {
            apiGw.resetHouseholdLocation(token)
            sessionStore.updateUser {
                it?.copy(
                    localizing = true,
                    household =
                        it.household?.copy(
                            location = null,
                            gpsprogress = null,
                        ),
                )
            }
            sessionStore.updateLocalization { it.copy(candidate = null, heatmap = null) }
        }
    }
}
