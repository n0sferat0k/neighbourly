package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.GpsItem

data class GpsItemVS(
    val latitude: Float,
    val longitude: Float,
    val frequency: Int = 1,
)

fun GpsItem.toGpsItemVS() = GpsItemVS(
    latitude = latitude,
    longitude = longitude,
)