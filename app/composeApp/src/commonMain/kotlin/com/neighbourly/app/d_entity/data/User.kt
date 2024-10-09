package com.neighbourly.app.d_entity.data

data class User(
    val id: Int,
    val username: String,
    val fullname: String? = null,
    val about: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val lastSyncTs: Int? = null,
    val lastModifiedTs: Int,
    val householdid: Int? = null,
    val household: Household? = null,
    val neighbourhoods: List<Neighbourhood> = emptyList(),
    val localizing: Boolean = false,
)
