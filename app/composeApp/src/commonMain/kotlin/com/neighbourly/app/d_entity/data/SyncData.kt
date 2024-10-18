package com.neighbourly.app.d_entity.data

data class SyncData(
    val items: List<Item>,
    val itemIds: List<Int>,
    val users: List<User>,
    val userIds: List<Int>,
    val houses: List<Household>,
    val houseIds: List<Int>,
)