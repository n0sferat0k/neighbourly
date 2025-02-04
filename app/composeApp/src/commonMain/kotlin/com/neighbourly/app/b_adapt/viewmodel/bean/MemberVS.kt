package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.User

data class MemberVS(
    val id: Int = -1,
    val username: String = "",
    val fullname: String = "",
    val email: String = "",
    val phone: String = "",
    val about: String = "",
    val imageurl: String? = null,
    val hasEstablishedHousehold: Boolean = false,
    val muted: Boolean = false,
)

fun User.toMemberVS(): MemberVS =
    MemberVS(
        id = this.id,
        username = this.username,
        fullname = this.fullname.orEmpty(),
        email = this.email.orEmpty(),
        phone = this.phone.orEmpty(),
        about = this.about.orEmpty(),
        imageurl = this.imageurl,
        hasEstablishedHousehold = this.household != null && this.household.location != null,
    )