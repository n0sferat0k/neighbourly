package com.neighbourly.app.c_business.usecase.content

import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemType
import com.neighbourly.app.d_entity.interf.Db
import kotlinx.datetime.Clock

class FilterItemsUseCase(val database: Db) {

    suspend fun filterItems(
        type: ItemType?,
        householdId: Int?,
        itemIds: List<Int>?,
        getExpired: Boolean
    ): Map<Item, Household?> {
        val now = Clock.System.now().epochSeconds.toInt()

        val items = database.filterItems(type, householdId, itemIds).let {
            if (getExpired) it else it.filter { it.id != null && (it.endTs == 0 || it.endTs > now) }
        }
        val houses = database.filterHouseholds(items.map { it.householdId }.filterNotNull())

        return items.map { item ->
            item to houses.firstOrNull { it.householdid == item.householdId }
        }.toMap()
    }
}