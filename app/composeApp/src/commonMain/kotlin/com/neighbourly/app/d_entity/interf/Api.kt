package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.User

interface Api {
    suspend fun login(
        username: String,
        password: String,
    ): User

    suspend fun register(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
    ): User

    suspend fun updateProfileImage(
        token: String,
        profileImageFileContents: FileContents,
    ): String

    suspend fun updateHouseholdImage(
        token: String,
        profileImageFileContents: FileContents,
    ): String

    suspend fun refreshProfile(token: String): User

    suspend fun fetchProfile(
        token: String,
        id: Int,
        username: String,
    ): User

    suspend fun logout(
        token: String,
        logoutAll: Boolean,
    )

    suspend fun updateProfile(
        token: String,
        fullname: String,
        email: String,
        phone: String,
        about: String,
    ): User

    suspend fun updateHousehold(
        token: String,
        name: String,
        address: String,
        about: String,
    ): User

    suspend fun addMemberToHousehold(
        token: String,
        id: Int,
        username: String,
    ): User

    suspend fun updateNeighbourhood(
        token: String,
        id: Int? = null,
        name: String,
        geofence: List<GpsItem>,
    ): User

    suspend fun gpsLog(
        token: String,
        timezone: Int,
        latitude: Float,
        longitude: Float,
    )

    suspend fun getGpsHeatmap(token: String): List<GpsItem>?

    suspend fun getGpsCandidate(token: String): GpsItem

    suspend fun acceptGpsCandidate(token: String): GpsItem

    suspend fun clearGpsData(token: String)

    suspend fun resetHouseholdLocation(token: String)
}