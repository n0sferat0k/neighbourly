package com.neighbourly.app.b_adapt.viewmodel.bean

import kotlinx.datetime.Instant

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
    val expLabel: String? = null,
    val deletable: Boolean = false,
    val householdImage: String? = null,
    val householdName: String? = null,
    val imageUrl: String? = null,
)