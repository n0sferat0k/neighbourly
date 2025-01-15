package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.BARTER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.DONATION
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.EVENT
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.INFO
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.NEED
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REMINDER
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.REQUEST
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.SALE
import com.neighbourly.app.b_adapt.viewmodel.bean.ItemTypeVS.SKILLSHARE
import com.neighbourly.app.d_entity.data.ItemType

enum class ItemTypeVS {
    INFO, DONATION, BARTER, SALE, EVENT, NEED, REQUEST, SKILLSHARE, REMINDER;
}

fun ItemType.toItemTypeVS() = when (this) {
    ItemType.INFO -> INFO
    ItemType.DONATION -> DONATION
    ItemType.BARTER -> BARTER
    ItemType.SALE -> SALE
    ItemType.EVENT -> EVENT
    ItemType.NEED -> NEED
    ItemType.REQUEST -> REQUEST
    ItemType.SKILLSHARE -> SKILLSHARE
    ItemType.REMINDER -> REMINDER
}