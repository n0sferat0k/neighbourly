package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.Item
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
    val url: String = "",
    val start: Instant? = null,
    val end: Instant? = null,
    val images: List<AttachmentVS> = emptyList(),
    val files: List<AttachmentVS> = emptyList(),
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
    url = url.orEmpty(),
    start = startTs.takeIf { it > 0 }
        ?.let { fromEpochSeconds(it.toLong()) },
    end = endTs.takeIf { it > 0 }
        ?.let { fromEpochSeconds(it.toLong()) },
    images = images.map {
        AttachmentVS(
            it.id ?: 0,
            it.url,
            it.name
        )
    },
    files = files.map {
        AttachmentVS(
            id = it.id ?: 0,
            url = it.url,
            name = it.name,
        )
    },
    neighbourhoodId = neighbourhoodId,
    imgCount = images.size,
    fileCount = files.size,
)