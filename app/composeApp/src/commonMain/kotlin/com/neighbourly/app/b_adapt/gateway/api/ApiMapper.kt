package com.neighbourly.app.b_adapt.gateway.api

import com.neighbourly.app.d_entity.data.Attachment
import com.neighbourly.app.d_entity.data.Box
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemMessage
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
        householdid = householdid,
        lastModifiedTs = lastModifiedTs,
        neighbourhoods = neighbourhoods.map { it.toNeighbourhood() },
    )

fun User.toUserDTO(): UserDTO =
    UserDTO(
        id = id,
        username = username,
        about = about,
        fullname = fullname,
        email = email,
        phone = phone,
        imageurl = imageurl?.prependResourceUrlBase(),
        authtoken = authtoken,
        household = household?.toHouseholdDTO(),
        lastModifiedTs = lastModifiedTs,
        neighbourhoods = neighbourhoods.map { it.toNeighbourhoodDTO() },
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
        boxes = boxes?.map { it.toBox() }
    )

fun Household.toHouseholdDTO(): HouseholdDTO =
    HouseholdDTO(
        householdid = householdid,
        name = name,
        about = about,
        imageurl = imageurl?.prependResourceUrlBase(),
        headid = headid,
        latitude = location?.first,
        longitude = location?.second,
        address = address,
        gpsprogress = gpsprogress,
        lastModifiedTs = lastModifiedTs,
        members = members?.map { it.toUserDTO() },
    )

fun BoxDTO.toBox(): Box = Box(
    name = name,
    id = id,
)

fun NeighbourhoodDTO.toNeighbourhood(): Neighbourhood =
    Neighbourhood(
        neighbourhoodid = neighbourhoodid,
        name = name.orEmpty(),
        geofence = geofence.orEmpty(),
        access = access ?: 0,
        parent = parent?.toUser(),
    )

fun Neighbourhood.toNeighbourhoodDTO(): NeighbourhoodDTO =
    NeighbourhoodDTO(
        neighbourhoodid = neighbourhoodid,
        name = name,
        geofence = geofence,
        access = access,
        parent = parent?.toUserDTO(),
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
        images = images.map { it.toAttachment() },
        files = files.map { it.toAttachment() },
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
        targetUserId = targetUserId ?: -1,
        images = images.map { it.toAttachmentDTO() },
        files = files.map { it.toAttachmentDTO() },
        startTs = startTs,
        endTs = endTs,
        lastModifiedTs = lastModifiedTs,
        neighbourhoodId = neighbourhoodId,
        householdId = householdId,
        userId = userId,
    )

fun ItemMessageDTO.toItemMessage() = ItemMessage(
    id = id,
    message = message.orEmpty(),
    lastModifiedTs = lastModifiedTs,
    userId = userId,
    itemId = itemId
)

fun AttachmentDTO.toAttachment(): Attachment = Attachment(
    id = id,
    url = url.prependResourceUrlBase(),
    name = name,
)

fun Attachment.toAttachmentDTO(): AttachmentDTO = AttachmentDTO(
    id = id,
    url = url,
    name = name,
)

fun String.prependResourceUrlBase() =
    this.takeIf { it.isNotBlank() }?.let { CONTENT_BASE_URL + it } ?: this

const val CONTENT_BASE_URL = "http://neighbourly.go.ro/"
