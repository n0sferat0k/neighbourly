package com.neighbourly.app.d_entity.data

data class BoxShare(
    val id: Int,
    val name: String,
    val boxId: String,
    val token: String = "",
    val householdId: Int = -1,
)