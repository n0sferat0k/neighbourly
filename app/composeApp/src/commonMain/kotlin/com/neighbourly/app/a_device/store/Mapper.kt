package com.neighbourly.app.a_device.store

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
        household = this.household?.toHousehold(),
        neighbourhoods = this.neighbourhoods.map { it.toStoreNeighbourhood() },
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
        household = this.household?.toStoreHousehold(),
        neighbourhoods = this.neighbourhoods.map { it.toStoreNeighbourhood() },
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
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val household: StoreHousehold? = null,
    val neighbourhoods: List<StoreNeighbourhood> = emptyList(),
    val localizing: Boolean = false,
)

@Serializable
data class StoreHousehold(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String? = null,
    val headid: Int,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val address: String,
    val gpsprogress: Float? = null,
)

@Serializable
data class StoreNeighbourhood(
    val neighbourhoodid: Int,
    val name: String,
    val geofence: String,
    val access: Int,
    val parent: StoreUser? = null,
)
