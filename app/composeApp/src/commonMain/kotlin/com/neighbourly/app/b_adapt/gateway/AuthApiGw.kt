package com.neighbourly.app.b_adapt.gateway

import com.neighbourly.app.a_device.api.KtorAuthApi
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class AuthApiGw(
    val api: KtorAuthApi,
) : AuthApi {
    override suspend fun logout(
        token: String,
        logoutAll: Boolean,
    ) {
        try {
            return withContext(Dispatchers.IO) {
                api.logout(API_BASE_URL, token, logoutAll)
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    override suspend fun updateProfileImage(
        token: String,
        profileImageFileContents: FileContents,
    ): String {
        try {
            return withContext(Dispatchers.IO) {
                api.uploadImage(API_BASE_URL, token, profileImageFileContents)
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    override suspend fun updateProfile(
        token: String,
        fullname: String,
        email: String,
        phone: String,
        about: String,
    ): User {
        try {
            return withContext(Dispatchers.IO) {
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
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    override suspend fun refreshProfile(token: String): User {
        try {
            return withContext(Dispatchers.IO) {
                api.refreshProfile(API_BASE_URL, token).toUser()
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        fullname: String,
        email: String,
        phone: String,
    ): User {
        try {
            return withContext(Dispatchers.IO) {
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
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    override suspend fun login(
        username: String,
        password: String,
    ): User {
        try {
            return withContext(Dispatchers.IO) {
                api.login(API_BASE_URL, LoginInput(username, password)).toUser()
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    companion object {
        const val API_BASE_URL = "http://neighbourly.go.ro:8080/"
    }
}

class ApiException(
    val msg: String,
) : RuntimeException(msg)

@Serializable
data class LoginInput(
    val username: String,
    val password: String,
)

@Serializable
data class RegisterInput(
    val username: String,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
)

@Serializable
data class UpdateProfileInput(
    val fullname: String,
    val phone: String,
    val email: String,
    val about: String,
)

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val about: String? = null,
    val password: String? = null,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String? = null,
    val authtoken: String? = null,
    val household: HouseholdDTO? = null,
    val neighbourhoods: List<NeighbourhoodDTO> = emptyList(),
)

@Serializable
data class HouseholdDTO(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String,
    val headid: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String,
)

@Serializable
data class NeighbourhoodDTO(
    val neighbourhoodid: Int,
    val name: String,
    val geofence: String,
    val access: Int,
    val parent: UserDTO? = null,
)
