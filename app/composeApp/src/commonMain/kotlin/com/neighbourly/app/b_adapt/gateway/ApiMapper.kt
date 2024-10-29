package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
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
        lastModifiedTs = lastModifiedTs,
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
        lastModifiedTs = lastModifiedTs,
        members = members?.map { it.toUser() },
    )

fun NeighbourhoodDTO.toNeighbourhood(): Neighbourhood =
    Neighbourhood(
        neighbourhoodid = neighbourhoodid,
        name = name.orEmpty(),
        geofence = geofence.orEmpty(),
        access = access ?: 0,
        parent = parent?.toUser(),
    )

fun GpsItemDTO.toGpsItem(): GpsItem =
    GpsItem(
        latitude = latitude,
        longitude = longitude,
        frequency = frequency,
    )

fun ItemDTO.toItem(): Item =
    Item(
        id = id,
        type = ItemType.getByName(type),
        name = name,
        description = description,
        url = url,
        targetUserId = targetUserId,
        images = images.map { (key, value) -> key to value.prependResourceUrlBase() }.toMap(),
        files = files.map { (key, value) -> key to value.prependResourceUrlBase() }.toMap(),
        startTs = startTs,
        endTs = endTs,
        lastModifiedTs = lastModifiedTs,
        neighbourhoodId = neighbourhoodId,
        householdId = householdId,
        userId = userId,
    )

fun Item.toItemDTO(): ItemDTO =
    ItemDTO(
        id = id,
        type = type.name,
        name = name,
        description = description,
        url = url,
        targetUserId = targetUserId,
        images = images.map { (key, value) -> key to value.prependResourceUrlBase() }.toMap(),
        files = files.map { (key, value) -> key to value.prependResourceUrlBase() }.toMap(),
        startTs = startTs,
        endTs = endTs,
        lastModifiedTs = lastModifiedTs,
        neighbourhoodId = neighbourhoodId,
        householdId = householdId,
        userId = userId,
    )

fun String.prependResourceUrlBase() =
    this.takeIf { it.isNotBlank() }?.let { CONTENT_BASE_URL + it } ?: this

const val CONTENT_BASE_URL = "http://neighbourly.go.ro/"
