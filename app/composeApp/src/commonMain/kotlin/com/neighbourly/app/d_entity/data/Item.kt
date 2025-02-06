package com.neighbourly.app.d_entity.data

data class Item(
    val id: Int?,
    val type: ItemType = ItemType.INFO,
    val name: String? = null,
    val description: String? = null,
    val url: String? = null,
    val targetUserId: Int? = null,
    val accent: Boolean = false,
    val images: List<Attachment> = emptyList(),
    val files: List<Attachment> = emptyList(),
    val messages: List<ItemMessage> = emptyList(),
    val startTs: Int = 0,
    val endTs: Int = 0,
    val lastModifiedTs: Int? = null,
    val neighbourhoodId: Int? = null,
    val householdId: Int? = null,
    val userId: Int? = null,
)

enum class ItemType {
    INFO,
    DONATION,
    BARTER,
    SALE,
    EVENT,
    NEED,
    REQUEST,
    SKILLSHARE,
    REMINDER;

    companion object {
        fun getByName(type: String?): ItemType =
            entries.filter { it.name == type }.firstOrNull() ?: INFO
    }
}