package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.Household

data class HouseholdVS(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val about: String = "",
    val imageurl: String? = null,
    val members: List<String> = emptyList()
)

fun Household.toHouseholdVS() = HouseholdVS(
    id = householdid,
    name = name,
    address = address.orEmpty(),
    about = about.orEmpty(),
    imageurl = imageurl,
)