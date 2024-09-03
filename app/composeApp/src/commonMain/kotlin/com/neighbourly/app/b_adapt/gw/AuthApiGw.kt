package com.neighbourly.app.b_adapt.gw

import com.neighbourly.app.a_device.api.KtorAuthApi
import com.neighbourly.app.d_entity.data.Household
import com.neighbourly.app.d_entity.data.OpException
import com.neighbourly.app.d_entity.data.User
import com.neighbourly.app.d_entity.interf.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class AuthApiGw(val api: KtorAuthApi) : AuthApi {

    override suspend fun login(username: String, password: String): User {
        try {
            return withContext(Dispatchers.IO) {
                api.login(LoginInput(username, password))
            }.let {
                User(
                    id = it.id,
                    username = it.username,
                    about = it.about,
                    password = it.password,
                    fullname = it.fullname,
                    email = it.email,
                    phone = it.phone,
                    imageurl = it.imageurl,
                    authtoken = it.authtoken,
                    household = it.household?.let {
                        Household(
                            householdid = it.householdid,
                            name = it.name,
                            about = it.about,
                            imageurl = it.imageurl,
                            headid = it.headid,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            address = it.address
                        )
                    }
                )
            }
        } catch (e: ApiException) {
            throw OpException(e.msg)
        }
    }
}

class ApiException(val msg: String) : RuntimeException(msg)

@Serializable
data class LoginInput(val username: String, val password: String)

@Serializable
data class UserDTO(
    val id: Int,
    val username: String,
    val about: String,
    val password: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val imageurl: String,
    val authtoken: String,
    val household: HouseholdDTO?
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
    val address: String
)