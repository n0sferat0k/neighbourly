package com.neighbourly.app.d_entity.data

data class User(
    val id: Int,
    val username: String,
    val about: String? = null,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String?,
    val authtoken: String? = null,
    val household: Household?,
    val neighbourhoods: List<Neighbourhood> = emptyList(),
    val localizing: Boolean = false,
)
