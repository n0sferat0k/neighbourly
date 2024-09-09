package com.neighbourly.app.b_adapt.gateway

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
        imageurl = CONTENT_BASE_URL + imageurl,
        authtoken = authtoken,
        household = household?.toHousehold(),
        neighbourhoods = neighbourhoods.map { it.toNeighbourhood() },
    )

fun HouseholdDTO.toHousehold(): Household =
    Household(
        householdid = householdid,
        name = name,
        about = about,
        imageurl = imageurl,
        headid = headid,
        latitude = latitude,
        longitude = longitude,
        address = address,
    )

fun NeighbourhoodDTO.toNeighbourhood(): Neighbourhood =
    Neighbourhood(
        neighbourhoodid = neighbourhoodid,
        name = name,
        geofence = geofence,
        access = access,
        parent = parent.toUser(),
    )

const val CONTENT_BASE_URL = "http://neighbourly.go.ro/"
