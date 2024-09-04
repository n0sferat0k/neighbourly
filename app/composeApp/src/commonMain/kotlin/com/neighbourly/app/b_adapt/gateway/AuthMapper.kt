package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.User

fun UserDTO.toUser() =
    User(
        id = id,
        username = username,
        about = about,
        fullname = fullname,
        email = email,
        phone = phone,
        imageurl = imageurl,
        authtoken = authtoken,
        household = household?.toHousehold(),
    )

fun HouseholdDTO.toHousehold() =
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
