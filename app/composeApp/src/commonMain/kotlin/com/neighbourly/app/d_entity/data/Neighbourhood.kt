package com.neighbourly.app.d_entity.data

data class Neighbourhood(
    val neighbourhoodid: Int,
    val name: String,
    val geofence: String,
    val access: Int,
    val parent: User?,
)
