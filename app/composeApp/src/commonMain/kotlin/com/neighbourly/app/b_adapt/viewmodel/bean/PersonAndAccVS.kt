package com.neighbourly.app.b_adapt.viewmodel.bean

data class PersonAndAccVS(
    val name: String,
    val access: Int,
    val accessOverride: Int? = null,
)