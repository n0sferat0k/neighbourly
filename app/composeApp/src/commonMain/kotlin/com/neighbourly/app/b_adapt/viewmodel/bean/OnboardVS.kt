package com.neighbourly.app.b_adapt.viewmodel.bean

data class OnboardVS(
    val imageurl: String? = null,
    val hasHousehold: Boolean = false,
    val householdLocalized: Boolean = false,
    val hasNeighbourhoods: Boolean = false,
)