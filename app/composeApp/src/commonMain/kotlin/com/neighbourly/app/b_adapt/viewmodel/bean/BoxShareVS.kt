package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.BoxShare

data class BoxShareVS(
    val id: Int,
    val name: String,
    val boxId: String,
    val token: String = "",
    val household: HouseholdVS? = null,
)

fun BoxShare.toBoxShareVS(household: HouseholdVS?): BoxShareVS = BoxShareVS(
    id = this.id,
    name = this.name,
    boxId = this.boxId,
    token = this.token,
    household = household,
)