package com.neighbourly.app.d_entity.data

data class Box(
    val name: String,
    val id: String,
    val householdId: Int,
    val shares: List<BoxShare>,
)