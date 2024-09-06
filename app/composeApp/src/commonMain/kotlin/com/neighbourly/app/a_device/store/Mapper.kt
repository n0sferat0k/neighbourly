package com.neighbourly.app.a_device.store

import com.neighbourly.app.d_entity.data.Household
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
    )

fun User.toStoreUser() =
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
    )

fun StoreHousehold.toHousehold() =
    Household(
        householdid = this.householdid,
        name = this.name,
        about = this.about,
        imageurl = this.imageurl,
        headid = this.headid,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
    )

fun Household.toStoreHousehold() =
    StoreHousehold(
        householdid = this.householdid,
        name = this.name,
        about = this.about,
        imageurl = this.imageurl,
        headid = this.headid,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
    )

@Serializable
data class StoreUser(
    val id: Int,
    val username: String,
    val about: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String?,
    val authtoken: String,
    val household: StoreHousehold?,
)

@Serializable
data class StoreHousehold(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String,
    val headid: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
)
