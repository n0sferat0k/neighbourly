package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemMessage
import com.neighbourly.app.d_entity.data.ItemType.REMINDER
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds

data class ItemVS(
    val id: Int? = null,
    val type: ItemTypeVS = ItemTypeVS.INFO,
    val name: String = "",
    val description: String = "",
    val dates: List<Instant> = emptyList(),
    val targetUserId: Int? = null,
    val accent: Boolean = false,
    val url: String = "",
    val start: Instant? = null,
    val end: Instant? = null,
    val images: List<AttachmentVS> = emptyList(),
    val files: List<AttachmentVS> = emptyList(),
    val messages: List<ItemMessageVS> = emptyList(),
    val neighbourhoodId: Int? = null,
    val imgCount: Int = 0,
    val fileCount: Int = 0,
    val augmentation: ItemAugmentVS? = null,
)

data class ItemAugmentVS(
    val expLabel: String? = null,
    val deletable: Boolean = false,
    val household: HouseholdVS? = null,
    val imageUrl: String? = null,
    val watched: Boolean = false,
)

data class ItemMessageVS(
    val deletable: Boolean = false,
    val id: Int,
    val senderId:Int,
    val message: String,
    val sender: String = "",
    val household: HouseholdVS? = null,
)

fun Item.toItemVS() = ItemVS(
    id = id,
    type = type.toItemTypeVS(),
    name = name.orEmpty(),
    description = description.orEmpty(),
    dates = if (type == REMINDER) {
        kotlin.runCatching {
            description?.split(",")
                ?.map { fromEpochSeconds(it.toLong()) }
                ?: emptyList()
        }.getOrNull() ?: emptyList()
    } else emptyList(),
    targetUserId = targetUserId,
    accent = accent,
    url = url.orEmpty(),
    start = startTs.takeIf { it > 0 }
        ?.let { fromEpochSeconds(it.toLong()) },
    end = endTs.takeIf { it > 0 }
        ?.let { fromEpochSeconds(it.toLong()) },
    images = images.map {
        AttachmentVS(
            id = it.id ?: 0,
            url = it.url,
            name = it.name,
            default = it.default,
        )
    },
    files = files.map {
        AttachmentVS(
            id = it.id ?: 0,
            url = it.url,
            name = it.name,
            default = it.default,
        )
    },
    neighbourhoodId = neighbourhoodId,
    imgCount = images.size,
    fileCount = files.size,
)

fun ItemMessage.toItemMessageVS(deletable: Boolean, senderId:Int, sender: String, household: HouseholdVS? = null) = ItemMessageVS(
    deletable = deletable,
    id = id ?: 0,
    message = message,
    senderId = senderId,
    sender = sender,
    household = household
)