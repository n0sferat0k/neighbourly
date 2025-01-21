package com.neighbourly.app.b_adapt.viewmodel.bean

data class HouseholdSummaryVS(
        val id: Int,
        val location: GpsItemVS,
        val name: String,
        val floatName: Boolean = false,
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