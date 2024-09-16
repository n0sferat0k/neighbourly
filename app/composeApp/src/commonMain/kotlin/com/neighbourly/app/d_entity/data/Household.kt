package com.neighbourly.app.d_entity.data

data class Household(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String? = null,
    val headid: Int,
    val location: Pair<Float, Float>? = null,
    val address: String,
    val gpsprogress: Float? = null,
)
