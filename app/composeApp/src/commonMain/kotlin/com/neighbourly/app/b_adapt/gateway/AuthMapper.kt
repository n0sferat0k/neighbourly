package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.d_entity.data.HeatmapItem
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Neighbourhood
import com.neighbourly.app.d_entity.data.User

fun UserDTO.toUser(): User =
    User(
        id = id,
        username = username,
        about = about,
        fullname = fullname,
        email = email,
        phone = phone,
        imageurl = imageurl?.prependResourceUrlBase(),
        authtoken = authtoken,
        household = household?.toHousehold(),
        neighbourhoods = neighbourhoods.map { it.toNeighbourhood() },
    )

fun HouseholdDTO.toHousehold(): Household =
    Household(
        householdid = householdid,
        name = name,
        about = about,
        imageurl = imageurl?.prependResourceUrlBase(),
        headid = headid,
        location =
            if (latitude != null && longitude != null && latitude != 0f && longitude != 0f) {
                Pair(
                    latitude,
                    longitude,
                )
            } else {
                null
            },
        address = address,
        gpsprogress = gpsprogress,
    )

fun NeighbourhoodDTO.toNeighbourhood(): Neighbourhood =
    Neighbourhood(
        neighbourhoodid = neighbourhoodid,
        name = name,
        geofence = geofence,
        access = access,
        parent = parent?.toUser(),
    )

fun HeatmapItemDTO.toHeatmapItem(): HeatmapItem =
    HeatmapItem(
        latitude = latitude,
        longitude = longitude,
        frequency = frequency,
    )

fun String.prependResourceUrlBase() = this.takeIf { !it.isNullOrBlank() }?.let { CONTENT_BASE_URL + it } ?: this

const val CONTENT_BASE_URL = "http://neighbourly.go.ro/"
