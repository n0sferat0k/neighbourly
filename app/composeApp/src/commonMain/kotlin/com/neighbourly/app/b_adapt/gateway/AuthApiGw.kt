package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.a_device.api.KtorAuthApi
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.GpsItem
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class AuthApiGw(
    val api: KtorAuthApi,
) : AuthApi {
    override suspend fun logout(
        token: String,
        logoutAll: Boolean,
    ) = runContextCatchTranslateThrow {
        api.logout(API_BASE_URL, token, logoutAll)
    }

    override suspend fun updateProfileImage(
        token: String,
        profileImageFileContents: FileContents,
    ): String =
        runContextCatchTranslateThrow {
            api
                .uploadImage(API_BASE_URL, token, profileImageFileContents)
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

    override suspend fun refreshProfile(token: String): User =
        runContextCatchTranslateThrow {
            api.refreshProfile(API_BASE_URL, token).toUser()
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

    companion object {
        const val API_BASE_URL = "http://neighbourly.go.ro:8080/"
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
