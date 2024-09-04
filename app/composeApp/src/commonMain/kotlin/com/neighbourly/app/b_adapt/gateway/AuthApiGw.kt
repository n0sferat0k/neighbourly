package com.neighbourly.app.b_adapt.gateway

import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.neighbourly.app.a_device.api.KtorAuthApi
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class AuthApiGw(
    val api: KtorAuthApi,
) : AuthApi {
    override suspend fun updateProfileImage(
        token: String,
        file: MPFile<Any>,
    ) {
        try {
            return withContext(Dispatchers.IO) {
                api.uploadImage(token, file)
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        fullName: String,
        email: String,
        phoneNumber: String,
    ): User {
        try {
            return withContext(Dispatchers.IO) {
                api.register(
                    RegisterInput(
                        username = username,
                        password = password,
                        fullname = fullName,
                        phone = phoneNumber,
                        email = email,
                    ),
                )
            }.let {
                it.toUser()
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
                api.login(LoginInput(username, password))
            }.let {
                it.toUser()
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
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
data class UserDTO(
    val id: Int,
    val username: String,
    val about: String,
    val password: String? = null,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String? = null,
    val authtoken: String,
    val household: HouseholdDTO? = null,
)

@Serializable
data class HouseholdDTO(
    val householdid: Int,
    val name: String,
    val about: String,
    val imageurl: String,
    val headid: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String,
)
