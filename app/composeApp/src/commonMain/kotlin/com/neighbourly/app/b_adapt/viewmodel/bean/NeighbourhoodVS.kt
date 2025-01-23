package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.Neighbourhood

data class NeighbourhoodVS(
    val name: String,
    val id: Int,
    val acc: Int,
    val geofence: String,
)

fun Neighbourhood.toNeighbourhoodVS() = NeighbourhoodVS(
    id = neighbourhoodid,
    name = name,
    acc = access,
    geofence = geofence,
)