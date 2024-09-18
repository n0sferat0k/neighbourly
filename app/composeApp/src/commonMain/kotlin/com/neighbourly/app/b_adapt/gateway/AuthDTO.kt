package com.neighbourly.app.b_adapt.gateway

import kotlinx.serialization.Serializable

@Serializable
data class LoginInput(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterInput(
    val username: String,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
)

@Serializable
data class UpdateProfileInput(
    val fullname: String,
    val phone: String,
    val email: String,
    val about: String,
)

@Serializable
data class GpsLogInput(
    val timezone: Int,
    val latitude: Float,
    val longitude: Float,
)

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val about: String? = null,
    val password: String? = null,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val household: HouseholdDTO? = null,
    val neighbourhoods: List<NeighbourhoodDTO> = emptyList(),
)

@Serializable
data class GpsItemDTO(
    val latitude: Float,
    val longitude: Float,
    val frequency: Int? = null,
)

@Serializable
data class HouseholdDTO(
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
data class NeighbourhoodDTO(
    val neighbourhoodid: Int,
    val name: String,
    val geofence: String,
    val access: Int,
    val parent: UserDTO? = null,
)
