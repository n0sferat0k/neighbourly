package com.neighbourly.app.d_entity.data

data class Household(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String,
    val headid: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String
)