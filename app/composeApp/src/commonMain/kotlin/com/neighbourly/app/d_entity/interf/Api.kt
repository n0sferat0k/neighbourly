package com.neighbourly.app.d_entity.interf

import com.neighbourly.app.d_entity.data.Attachment
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.ItemMessage
import com.neighbourly.app.d_entity.data.SyncData
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

    suspend fun uploadItemImage(
        token: String,
        itemId: Int,
        imageFileContents: FileContents,
    ): Attachment

    suspend fun uploadItemFile(
        token: String,
        itemId: Int,
        imageFileContents: FileContents,
    ): Attachment

    suspend fun deleteItemImage(
        token: String,
        itemImageId: Int
    )

    suspend fun deleteItemFile(
        token: String,
        itemFileId: Int
    )

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

    suspend fun leaveHousehold(
        token: String,
    ): User

    suspend fun addMemberToHousehold(
        token: String,
        id: Int,
        username: String,
        access: Map<Int, Int>,
    ): User

    suspend fun addMemberToNeighbourhood(
        token: String,
        neighbourhoodid: Int,
        id: Int,
        username: String,
        accs: Map<Int, Int>?,
    )

    suspend fun updateNeighbourhood(
        token: String,
        id: Int? = null,
        name: String,
        geofence: List<GpsItem>,
    ): User

    suspend fun leaveNeighbourhood(
        token: String,
        neighbourhoodId: Int,
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

    suspend fun synchronizeContent(token: String, lastSyncTs: Int?): SyncData

    suspend fun deleteItem(token: String, itemId: Int)

    suspend fun addOrUpdateItem(token: String, item: Item): Item

    suspend fun addItemMessage(token: String, itemId: Int, message:String): ItemMessage

    suspend fun getItemMessages(token: String, itemId: Int): List<ItemMessage>

    suspend fun deleteItemMessage(token: String, itemMessageId: Int)

    suspend fun addBox(token: String, boxId: String, boxName: String)

    suspend fun removeBox(token: String, boxId: String)

    suspend fun lockBox(token: String, boxId: String)

    suspend fun unlockBox(token: String, boxId: String)

    suspend fun openBox(token: String, boxId: String)
}
