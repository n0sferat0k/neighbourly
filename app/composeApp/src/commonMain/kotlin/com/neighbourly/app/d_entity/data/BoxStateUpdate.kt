package com.neighbourly.app.d_entity.data

data class BoxStateUpdate(
    val id: String,
    val online: Boolean? = null,
    val triggered: Boolean? = null,
    val unlocked: Boolean? = null,
    val lit: Boolean? = null,
    val ssd: String? = null,
    val signal: Int? = null,
)