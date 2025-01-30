package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.ItemType

sealed class MenuTabVS {
    object PROFILE : MenuTabVS()
    object MYSTUFF : MenuTabVS()
    object PUBLISH : MenuTabVS()
    object REMINDERS : MenuTabVS()
    object BOX : MenuTabVS()
    data class ITEMS(val type: ItemTypeVS) : MenuTabVS()
}