package com.neighbourly.app.c_business.usecase.profile

import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.interf.AuthApi
import com.neighbourly.app.d_entity.interf.SessionStore

class NeighbourhoodManagementUseCase(
    val apiGw: AuthApi,
    val sessionStore: SessionStore,
) {
    suspend fun startDrawing() {
        sessionStore.updateLocalization { it.copy(drawing = true) }
    }

    suspend fun saveDrawing(poly: List<GpsItem>) {
        sessionStore.updateLocalization { it.copy(drawingPoints = poly) }
    }

    suspend fun saveNeighbourhood(
        id: Int? = null,
        name: String,
    ) {
        val token = sessionStore.token
        val geofence = sessionStore.drawing
        if (token != null && geofence != null) {
            val user = apiGw.updateNeighbourhood(token, id, name, geofence)

            sessionStore.updateUser {
                it?.copy(
                    username = user.username,
                    about = user.about,
                    fullname = user.fullname,
                    email = user.email,
                    phone = user.phone,
                    imageurl = user.imageurl,
                    household = user.household,
                    neighbourhoods = user.neighbourhoods,
                )
            }
            sessionStore.updateLocalization {
                it.copy(
                    drawing = false,
                    drawingPoints = null,
                )
            }
        }
    }
}
