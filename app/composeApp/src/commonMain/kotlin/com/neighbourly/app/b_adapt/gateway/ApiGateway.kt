package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.a_device.api.KtorApi
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

class ApiGateway(
    val api: KtorApi,
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
                .uploadImage(API_BASE_URL, token, TARGET_PROFILE, imageFileContents)
                .prependResourceUrlBase()
        }

    override suspend fun updateHouseholdImage(
        token: String,
        imageFileContents: FileContents,
    ): String =
        runContextCatchTranslateThrow {
            api
                .uploadImage(API_BASE_URL, token, TARGET_HOUSEHOLD, imageFileContents)
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

    companion object {
        const val API_BASE_URL = "http://neighbourly.go.ro:8080/"
        const val TARGET_PROFILE = "profile"
        const val TARGET_HOUSEHOLD = "household"
    }

    suspend inline fun <R> runContextCatchTranslateThrow(crossinline block: suspend () -> R): R =
        runCatching {
            withContext(Dispatchers.IO) {
                block.invoke()
            }
        }.let {
            when {
                it.isSuccess -> it.getOrElse { throw OpException("Unknown Error") }
                it.isFailure ->
                    it.exceptionOrNull().let {
                        when (it) {
                            is ApiException -> throw OpException(it.msg)
                            is IOException -> throw OpException(it.message ?: it.toString())
                            else -> throw OpException("Unknown Error")
                        }
                    }

                else -> throw OpException("Unknown Error")
            }
        }
}

class ApiException(
    val msg: String,
) : RuntimeException(msg)
