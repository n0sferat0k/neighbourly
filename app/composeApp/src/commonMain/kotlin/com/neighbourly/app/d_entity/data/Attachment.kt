package com.neighbourly.app.d_entity.data

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Int? = null,
    val url: String = "",
    val name: String = "",
    val default: Boolean = false,
)
