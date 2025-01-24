package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.data.User

interface Db {
    suspend fun clear()
    suspend fun getLastModifTs(): Int?
    suspend fun storeItems(items: List<Item>)
    suspend fun stripItems(validIds: List<Int>)
    suspend fun deleteItem(itemId: Int)
    suspend fun storeUsers(users: List<User>)
    suspend fun stripUsers(validIds: List<Int>)
    suspend fun getUser(userId: Int): User
    suspend fun getUsers(): List<User>
    suspend fun storeHouseholds(households: List<Household>)
    suspend fun getHousehold(householdId: Int): Household
    suspend fun stripHouseholds(validIds: List<Int>)
    suspend fun getItem(itemId: Int): Item
    suspend fun getItemIds(): List<Int>
    suspend fun filterItems(type: ItemType? = null, householdId: Int? = null): List<Item>
    suspend fun filterHouseholds(ids: List<Int>? = null): List<Household>
}