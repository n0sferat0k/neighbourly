package com.neighbourly.app.b_adapt.viewmodel.bean

import com.neighbourly.app.d_entity.data.Household

data class HouseholdSummaryVS(
    val id: Int = -1,
    val location: GpsItemVS? = null,
    val isCandidate: Boolean = false,
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val skillshare: Int = 0,
    val requests: Int = 0,
    val needs: Int = 0,
    val events: Int = 0,
    val sales: Int = 0,
    val barterings: Int = 0,
    val donations: Int = 0,
    val imageurl: String? = null,
)

fun HouseholdSummaryVS.pullFrom(household: Household) = this.copy(
    id = household.householdid,
    location = household.location?.let { GpsItemVS(it.first, it.second) } ?: this.location,
    isCandidate = household.location?.let { false } ?: isCandidate,
    name = household.name,
    address = household.address.orEmpty(),
    description = household.about.orEmpty(),
    imageurl = household.imageurl,
)

fun Household.toHouseholdSummaryVS(): HouseholdSummaryVS =
    HouseholdSummaryVS(
        id = householdid,
        location = location?.let { GpsItemVS(it.first, it.second) },
        name = name,
        address = address.orEmpty(),
        description = about.orEmpty(),
        imageurl = imageurl,
    )