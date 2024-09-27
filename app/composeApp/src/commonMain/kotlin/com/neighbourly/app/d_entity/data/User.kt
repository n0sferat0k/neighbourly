package com.neighbourly.app.d_entity.data

data class User(
    val id: Int,
    val username: String,
    val about: String? = null,
    val fullname: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val household: Household?,
    val neighbourhoods: List<Neighbourhood> = emptyList(),
    val localizing: Boolean = false,
)
