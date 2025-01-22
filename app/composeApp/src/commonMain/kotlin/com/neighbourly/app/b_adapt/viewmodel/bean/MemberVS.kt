package com.neighbourly.app.b_adapt.viewmodel.bean

data class MemberVS(
    val id: Int = -1,
    val username: String = "",
    val fullname: String = "",
    val email: String = "",
    val phone: String = "",
    val about: String = "",
    val imageurl: String? = null,
    val hasEstablishedHousehold: Boolean = false,
)