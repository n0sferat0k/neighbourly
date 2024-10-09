package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.User

interface Db {
    suspend fun getLastModifTs(): Int?
    suspend fun storeItems(items: List<Item>)
    suspend fun storeUsers(users: List<User>)
    suspend fun storeHouseholds(households: List<Household>)
    suspend fun filterItems(type: ItemType): List<Item>
}