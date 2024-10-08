package com.neighbourly.app.d_entity.data

data class Item(
    val id: Int,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null,
    val targetUserId: Int? = null,
    val images: Map<Int, String> = emptyMap(),
    val files: Map<Int, String> = emptyMap(),
    val startTs: Int? = null,
    val endTs: Int? = null,
    val lastModifiedTs: Int,
    val neighbourhoodId: Int? = null,
    val householdId: Int? = null,
    val userId: Int? = null,
)