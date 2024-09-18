package com.neighbourly.app.d_entity.data

data class LocalizationProgress(
    val heatmap: List<GpsItem>? = null,
    val candidate: GpsItem? = null,
)
