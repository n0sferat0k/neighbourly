package com.neighbourly.app.b_adapt.viewmodel.bean

data class BoxVS(
    val id: String,
    val name: String,
    val owned: Boolean = false,
    val online: Boolean? = null,
    val triggered: Boolean = false,
    val unlocked: Boolean = false,
    val lit: Boolean = false,
    val shares: List<BoxShareVS> = emptyList(),
)