package com.neighbourly.app.d_entity.data

data class Household(
    val householdid: Int,
    val name: String,
    val headid: Int,
    val about: String? = null,
    val imageurl: String? = null,
    val location: Pair<Float, Float>? = null,
    val address: String? = null,
    val gpsprogress: Float? = null,
    val lastModifiedTs: Int,
    val members: List<User>? = null,
)
