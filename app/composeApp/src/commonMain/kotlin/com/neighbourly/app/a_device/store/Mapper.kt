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
    )

fun StoreHousehold.toHousehold(): Household =
    Household(
        householdid = this.householdid,
        name = this.name,
        about = this.about,
        imageurl = this.imageurl,
        headid = this.headid,
        location = if (latitude != null && longitude != null) Pair(latitude, longitude) else null,
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
)

@Serializable
data class StoreHousehold(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String? = null,
    val headid: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String,
)

@Serializable
data class StoreNeighbourhood(
    val neighbourhoodid: Int,
    val name: String,
    val geofence: String,
    val access: Int,
    val parent: StoreUser? = null,
)
