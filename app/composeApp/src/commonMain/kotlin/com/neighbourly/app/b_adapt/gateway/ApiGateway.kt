package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.a_device.api.KtorApi
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.Item
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.SyncData
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.Api
import com.neighbourly.app.d_entity.interf.StatusUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.concurrent.TimeoutException

class ApiGateway(
    val api: KtorApi,
    val statusUpdater: StatusUpdater,
) : Api {
    override suspend fun logout(
        token: String,
        logoutAll: Boolean,
    ) = runContextCatchTranslateThrow {
        api.logout(API_BASE_URL, token, logoutAll)
    }

    override suspend fun updateProfileImage(
        token: String,
        imageFileContents: FileContents,
    ): String =
        runContextCatchTranslateThrow {
            api
                .uploadFile(
                    baseUrl = API_BASE_URL,
                    token = token,
                    target = TARGET_PROFILE,
                    fileContents = imageFileContents
                ).values.first()
                .prependResourceUrlBase()
        }

    override suspend fun uploadItemImage(
        token: String,
        itemId: Int,
        imageFileContents: FileContents
    ): Pair<Int, String> =
        runContextCatchTranslateThrow {
            api
                .uploadFile(
                    baseUrl = API_BASE_URL,
                    token = token,
                    target = TARGET_ITEM_IMAGE,
                    targetId = itemId.toString(),
                    fileContents = imageFileContents
                ).entries.first().let {
                    Pair(
                        it.key, it.value.prependResourceUrlBase()
                    )
                }
        }

    override suspend fun uploadItemFile(
        token: String,
        itemId: Int,
        imageFileContents: FileContents
    ): Pair<Int, String> =
        runContextCatchTranslateThrow {
            api
                .uploadFile(
                    baseUrl = API_BASE_URL,
                    token = token,
                    target = TARGET_ITEM_FILE,
                    targetId = itemId.toString(),
                    fileContents = imageFileContents
                ).entries.first().let {
                    Pair(
                        it.key, it.value.prependResourceUrlBase()
                    )
                }
        }

    override suspend fun deleteItemImage(
        token: String,
        itemImageId: Int
    ) =
        runContextCatchTranslateThrow {
            api.deleteFile(
                baseUrl = API_BASE_URL,
                token = token,
                target = TARGET_ITEM_IMAGE,
                targetId = itemImageId.toString()
            )
        }

    override suspend fun deleteItemFile(
        token: String,
        itemFileId: Int
    ) =
        runContextCatchTranslateThrow {
            api.deleteFile(
                baseUrl = API_BASE_URL,
                token = token,
                target = TARGET_ITEM_IMAGE,
                targetId = itemFileId.toString()
            )
        }

    override suspend fun updateHouseholdImage(
        token: String,
        imageFileContents: FileContents,
    ): String =
        runContextCatchTranslateThrow {
            api
                .uploadFile(
                    baseUrl = API_BASE_URL,
                    token = token,
                    target = TARGET_HOUSEHOLD,
                    fileContents = imageFileContents
                ).values.first()
                .prependResourceUrlBase()
        }

    override suspend fun updateProfile(
        token: String,
        fullname: String,
        email: String,
        phone: String,
        about: String,
    ): User =
        runContextCatchTranslateThrow {
            api
                .updateProfile(
                    API_BASE_URL,
                    token,
                    UpdateProfileInput(
                        fullname = fullname,
                        phone = phone,
                        email = email,
                        about = about,
                    ),
                ).toUser()
        }

    override suspend fun updateHousehold(
        token: String,
        name: String,
        address: String,
        about: String,
    ): User =
        runContextCatchTranslateThrow {
            api
                .updateHousehold(
                    API_BASE_URL,
                    token,
                    UpdateHouseholdInput(
                        name = name,
                        address = address,
                        about = about,
                    ),
                ).toUser()
        }

    override suspend fun leaveHousehold(
        token: String,
    ): User =
        runContextCatchTranslateThrow {
            api
                .leaveHousehold(
                    API_BASE_URL,
                    token,
                ).toUser()
        }

    override suspend fun addMemberToHousehold(
        token: String,
        id: Int,
        username: String,
    ): User =
        runContextCatchTranslateThrow {
            api
                .addMemberToHousehold(
                    API_BASE_URL,
                    token,
                    AddMemberToHouseholdInput(
                        id = id,
                        username = username,
                    ),
                ).toUser()
        }

    override suspend fun addMemberToNeighbourhood(
        token: String,
        neighbourhoodid: Int,
        id: Int,
        username: String,
        accs: Map<Int, Int>?,
    ) = runContextCatchTranslateThrow {
        api
            .addMemberToNeighbourhood(
                API_BASE_URL,
                token,
                AddMemberToNeighbourhoodInput(
                    neighbourhoodid = neighbourhoodid,
                    id = id,
                    username = username,
                    accs = accs,
                ),
            )
    }

    override suspend fun updateNeighbourhood(
        token: String,
        id: Int?,
        name: String,
        geofence: List<GpsItem>,
    ): User =
        runContextCatchTranslateThrow {
            api
                .updateNeighbourhood(
                    API_BASE_URL,
                    token,
                    UpdateNeighbourhoodInput(
                        neighbourhoodid = id,
                        name = name,
                        geofence =
                        Json.encodeToString(
                            geofence
                                .map {
                                    listOf(
                                        it.latitude,
                                        it.longitude,
                                    )
                                }.toList(),
                        ),
                    ),
                ).toUser()
        }

    override suspend fun leaveNeighbourhood(
        token: String,
        neighbourhoodId: Int,
    ): User =
        runContextCatchTranslateThrow {
            api
                .leaveNeighbourhood(API_BASE_URL, token, neighbourhoodId).toUser()
        }

    override suspend fun refreshProfile(token: String): User =
        runContextCatchTranslateThrow {
            api.refreshProfile(API_BASE_URL, token).toUser()
        }

    override suspend fun fetchProfile(
        token: String,
        id: Int,
        username: String,
    ): User =
        runContextCatchTranslateThrow {
            api.fetchProfile(API_BASE_URL, token, FetchProfileInput(id, username)).toUser()
        }

    override suspend fun register(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
    ): User =
        runContextCatchTranslateThrow {
            api
                .register(
                    API_BASE_URL,
                    RegisterInput(
                        username = username,
                        password = password,
                        fullname = fullname,
                        phone = phone,
                        email = email,
                    ),
                ).toUser()
        }

    override suspend fun login(
        username: String,
        password: String,
    ): User =
        runContextCatchTranslateThrow {
            api.login(API_BASE_URL, LoginInput(username, password)).toUser()
        }

    override suspend fun gpsLog(
        token: String,
        timezone: Int,
        latitude: Float,
        longitude: Float,
    ) = runContextCatchTranslateThrow {
        api.gpsLog(
            API_BASE_URL,
            token,
            GpsLogInput(timezone = timezone, latitude = latitude, longitude = longitude),
        )
    }

    override suspend fun getGpsHeatmap(token: String): List<GpsItem>? =
        runContextCatchTranslateThrow {
            api.getGpsHeatmap(API_BASE_URL, token)?.map { it.toGpsItem() }
        }

    override suspend fun getGpsCandidate(token: String): GpsItem =
        runContextCatchTranslateThrow {
            api.getGpsCandidate(API_BASE_URL, token).toGpsItem()
        }

    override suspend fun acceptGpsCandidate(token: String): GpsItem =
        runContextCatchTranslateThrow {
            api.acceptGpsCandidate(API_BASE_URL, token).toGpsItem()
        }

    override suspend fun clearGpsData(token: String) =
        runContextCatchTranslateThrow {
            api.clearGpsData(API_BASE_URL, token)
        }

    override suspend fun resetHouseholdLocation(token: String) =
        runContextCatchTranslateThrow {
            api.resetHouseholdLocation(API_BASE_URL, token)
        }

    override suspend fun synchronizeContent(
        token: String,
        lastSyncTs: Int?
    ): SyncData =
        runContextCatchTranslateThrow {
            api.synchronizeContent(API_BASE_URL, token, lastSyncTs ?: 0).let {
                SyncData(
                    items = it.items.map { it.toItem() },
                    itemIds = it.itemIds,
                    users = it.users.map { it.toUser() },
                    userIds = it.userIds,
                    houses = it.households.map { it.toHousehold() },
                    houseIds = it.householdIds,
                )
            }
        }

    override suspend fun deleteItem(token: String, itemId: Int) =
        runContextCatchTranslateThrow {
            api.deleteItem(API_BASE_URL, token, itemId)
        }

    override suspend fun addOrUpdateItem(token: String, item: Item): Item =
        runContextCatchTranslateThrow {
            api.addOrUpdateItem(API_BASE_URL, token, item.toItemDTO()).toItem()
        }

    override suspend fun lockBox(token: String, boxId: String) {
        runContextCatchTranslateThrow {
            api.boxOp(API_BASE_URL, token, BoxDTO(id = boxId, command = BOX_CMD_LOCK))
        }
    }

    override suspend fun unlockBox(token: String, boxId: String) {
        runContextCatchTranslateThrow {
            api.boxOp(API_BASE_URL, token, BoxDTO(id = boxId, command = BOX_CMD_UNLOCK))
        }
    }

    override suspend fun openBox(token: String, boxId: String) {
        runContextCatchTranslateThrow {
            api.boxOp(API_BASE_URL, token, BoxDTO(id = boxId, command = BOX_CMD_OPEN))
        }
    }

    companion object {
        const val API_BASE_URL = "http://neighbourly.go.ro:8080/"
        const val TARGET_PROFILE = "profile"
        const val TARGET_ITEM_IMAGE = "itemImage"
        const val TARGET_ITEM_FILE = "itemFile"
        const val TARGET_HOUSEHOLD = "household"
        const val BOX_CMD_LOCK = "LOCK"
        const val BOX_CMD_UNLOCK = "UNLOCK"
        const val BOX_CMD_OPEN = "OPEN"
    }

    suspend inline fun <R> runContextCatchTranslateThrow(crossinline block: suspend () -> R): R =
        runCatching {
            withContext(Dispatchers.IO) {
                block.invoke()
            }
        }.let {
            when {
                it.isSuccess -> {
                    statusUpdater.setOnline(true, null)
                    it.getOrElse { throw OpException("Unknown Error") }
                }

                it.isFailure -> {
                    val networkError = (it.exceptionOrNull()?.let { it is IOException || it is TimeoutException } ?: false)

                    val messgae = it.exceptionOrNull().let {
                        when (it) {
                            is ApiException -> it.msg
                            is IOException -> it.message ?: it.toString()
                            else -> "Unknown Error"
                        }
                    }

                    statusUpdater.setOnline(!networkError, messgae)
                    throw OpException(messgae)
                }

                else -> throw OpException("Unknown Error")
            }


        }
}

class ApiException(
    val msg: String,
) : RuntimeException(msg)
