package com.neighbourly.app.d_entity.data

data class ItemMessage(
    val id: Int? = null,
    val message: String = "",
    val lastModifiedTs: Int? = null,
    val userId: Int? = null,
    val itemId: Int? = null,
)