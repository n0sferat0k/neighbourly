package com.neighbourly.app.d_entity.data

data class User(
    val id: Int,
    val username: String,
    val about: String,
    val password: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String,
    val authtoken: String,
    val household: Household?
)