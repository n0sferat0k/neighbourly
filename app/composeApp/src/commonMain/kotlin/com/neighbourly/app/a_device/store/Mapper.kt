package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.Box
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Neighbourhood
import com.neighbourly.app.d_entity.data.User
import kotlinx.serialization.Serializable

fun StoreUser.toUser() =
    User(
        id = this.id,
        username = this.username,
        about = this.about,
        fullname = this.fullname,
        email = this.email,
        phone = this.phone,
        imageurl = this.imageurl,
        authtoken = this.authtoken,
        householdid = this.householdid,
        lastModifiedTs = this.lastModifiedTs,
        lastSyncTs = this.lastSyncTs,
        household = this.household?.toHousehold(),
        neighbourhoods = this.neighbourhoods.map { it.toStoreNeighbourhood() },
        mutedHouseholds = this.mutedHouseholds,
        mutedUsers = this.mutedUsers,
        watchedItems = this.watchedItems,
        localizing = this.localizing,
    )

fun User.toStoreUser(): StoreUser =
    StoreUser(
        id = this.id,
        username = this.username,
        about = this.about,
        fullname = this.fullname,
        email = this.email,
        phone = this.phone,
        imageurl = this.imageurl,
        authtoken = this.authtoken,
        householdid = this.householdid,
        lastModifiedTs = this.lastModifiedTs,
        lastSyncTs = this.lastSyncTs,
        household = this.household?.toStoreHousehold(),
        neighbourhoods = this.neighbourhoods.map { it.toStoreNeighbourhood() },
        mutedHouseholds = this.mutedHouseholds,
        mutedUsers = this.mutedUsers,
        watchedItems = this.watchedItems,
        localizing = this.localizing,
    )

fun StoreHousehold.toHousehold(): Household =
    Household(
        householdid = this.householdid,
        name = this.name,
        about = this.about,
        imageurl = this.imageurl,
        headid = this.headid,
        location =
        if (latitude != null && longitude != null && latitude != 0f && longitude != 0f) {
            Pair(latitude, longitude)
        } else {
            null
        },
        address = this.address,
        lastModifiedTs = this.lastModifiedTs,
        members = members?.map { it.toUser() },
        boxes = boxes?.map { it.toBox() }
    )

fun Household.toStoreHousehold(): StoreHousehold =
    StoreHousehold(
        householdid = this.householdid,
        name = this.name,
        about = this.about,
        imageurl = this.imageurl,
        headid = this.headid,
        latitude = location?.first,
        longitude = location?.second,
        address = this.address,
        lastModifiedTs = this.lastModifiedTs,
        members = members?.map { it.toStoreUser() },
        boxes = boxes?.map { it.toStoreBox() }
    )

fun Box.toStoreBox(): StoreBox =
    StoreBox(
        name = this.name,
        id = this.id,
    )

fun StoreBox.toBox(): Box =
    Box(
        name = this.name,
        id = this.id,
    )

fun Neighbourhood.toStoreNeighbourhood(): StoreNeighbourhood =
    StoreNeighbourhood(
        neighbourhoodid = this.neighbourhoodid,
        name = this.name,
        geofence = this.geofence,
        access = this.access,
        parent = this.parent?.toStoreUser(),
    )

fun StoreNeighbourhood.toStoreNeighbourhood(): Neighbourhood =
    Neighbourhood(
        neighbourhoodid = this.neighbourhoodid,
        name = this.name,
        geofence = this.geofence,
        access = this.access,
        parent = this.parent?.toUser(),
    )

@Serializable
data class StoreUser(
    val id: Int,
    val username: String,
    val about: String? = null,
    val fullname: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val householdid: Int? = null,
    val lastModifiedTs: Int = 0,
    val lastSyncTs: Int? = null,
    val lastMessageSyncTs: Int? = null,
    val household: StoreHousehold? = null,
    val neighbourhoods: List<StoreNeighbourhood> = emptyList(),
    val mutedHouseholds: Set<Int> = emptySet(),
    val watchedItems: Set<Int> = emptySet(),
    val mutedUsers: Set<Int> = emptySet(),
    val localizing: Boolean = false,
)

@Serializable
data class StoreBox(
    val name: String,
    val id: String,
)

@Serializable
data class StoreHousehold(
    val householdid: Int,
    val name: String,
    val headid: Int,
    val about: String? = null,
    val imageurl: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val address: String? = null,
    val lastModifiedTs: Int = 0,
    val gpsprogress: Float? = null,
    val members: List<StoreUser>? = null,
    val boxes: List<StoreBox>? = null,
)

@Serializable
data class StoreNeighbourhood(
    val neighbourhoodid: Int,
    val name: String,
    val geofence: String,
    val access: Int,
    val parent: StoreUser? = null,
)
