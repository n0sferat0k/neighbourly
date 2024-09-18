package com.neighbourly.app.a_device.api

import com.neighbourly.app.b_adapt.gateway.ApiException
import com.neighbourly.app.b_adapt.gateway.GpsItemDTO
import com.neighbourly.app.b_adapt.gateway.GpsLogInput
import com.neighbourly.app.b_adapt.gateway.LoginInput
import com.neighbourly.app.b_adapt.gateway.RegisterInput
import com.neighbourly.app.b_adapt.gateway.UpdateHouseholdInput
import com.neighbourly.app.b_adapt.gateway.UpdateProfileInput
import com.neighbourly.app.b_adapt.gateway.UserDTO
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorAuthApi {
    private val client =
        HttpClient(httpClientEngine) {
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            println("HTTP Client " + message)
                        }
                    }
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                    contentType = ContentType.Any,
                )
            }
        }

    suspend fun register(
        baseUrl: String,
        registerInput: RegisterInput,
    ): UserDTO {
        val response: HttpResponse =
            client.post(baseUrl + "register") {
                contentType(ContentType.Application.Json)
                setBody(registerInput)
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun logout(
        baseUrl: String,
        token: String,
        logoutAll: Boolean,
    ) {
        client.post(baseUrl + "logout") {
            parameter("logoutAll", logoutAll)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
    }

    suspend fun login(
        baseUrl: String,
        loginInput: LoginInput,
    ): UserDTO {
        val response: HttpResponse =
            client.post(baseUrl + "login") {
                contentType(ContentType.Application.Json)
                setBody(loginInput)
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun updateProfile(
        baseUrl: String,
        token: String,
        updateProfileInput: UpdateProfileInput,
    ): UserDTO {
        val response: HttpResponse =
            client.post(baseUrl + "profile/update") {
                contentType(ContentType.Application.Json)
                setBody(updateProfileInput)
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun updateHousehold(
        baseUrl: String,
        token: String,
        updateHouseholdInput: UpdateHouseholdInput,
    ): UserDTO {
        val response: HttpResponse =
            client.post(baseUrl + "profile/updateHousehold") {
                contentType(ContentType.Application.Json)
                setBody(updateHouseholdInput)
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun refreshProfile(
        baseUrl: String,
        token: String,
    ): UserDTO {
        val response: HttpResponse =
            client.post(baseUrl + "profile/refresh") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun uploadImage(
        baseUrl: String,
        token: String,
        target: String,
        fileContents: FileContents,
    ): String {
        val response: HttpResponse =
            client.submitFormWithBinaryData(
                url = baseUrl + "profile/upload?target=" + target,
                formData =
                    formData {
                        append(
                            "image",
                            fileContents.content,
                            Headers.build {
                                append(HttpHeaders.ContentType, fileContents.type)
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${fileContents.name}\"",
                                )
                            },
                        )
                    },
            ) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }

        if (response.status.value == 201) {
            return response.bodyAsText()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun gpsLog(
        baseUrl: String,
        token: String,
        gpsLogInput: GpsLogInput,
    ) {
        client.post(baseUrl + "gps/log") {
            contentType(ContentType.Application.Json)
            setBody(gpsLogInput)
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
    }

    suspend fun getGpsHeatmap(
        baseUrl: String,
        token: String,
    ): List<GpsItemDTO>? {
        val response =
            client.get(baseUrl + "gps/heatmap?onlyNight=false") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }

        if (response.status.value == 200) {
            return response.body<List<GpsItemDTO>?>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun getGpsCandidate(
        baseUrl: String,
        token: String,
    ): GpsItemDTO {
        val response =
            client.get(baseUrl + "gps/candidate") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer " + token)
                }
            }
        if (response.status.value == 200) {
            return response.body<GpsItemDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }
}
