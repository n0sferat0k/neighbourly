package com.neighbourly.app.a_device.api

import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.neighbourly.app.b_adapt.gateway.ApiException
import com.neighbourly.app.b_adapt.gateway.LoginInput
import com.neighbourly.app.b_adapt.gateway.RegisterInput
import com.neighbourly.app.b_adapt.gateway.UserDTO
import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.headers
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

    suspend fun register(registerInput: RegisterInput): UserDTO {
        val response: HttpResponse =
            client.post("http://neighbourly.go.ro:8080/register") {
                contentType(ContentType.Application.Json)
                setBody(registerInput)
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun login(loginInput: LoginInput): UserDTO {
        val response: HttpResponse =
            client.post("http://neighbourly.go.ro:8080/login") {
                contentType(ContentType.Application.Json)
                setBody(loginInput)
            }
        if (response.status.value == 200) {
            return response.body<UserDTO>()
        } else {
            throw ApiException(response.bodyAsText())
        }
    }

    suspend fun uploadImage(
        token: String,
        file: MPFile<Any>,
    ): HttpResponse =
        client.submitFormWithBinaryData(
            url = "http://neighbourly.go.ro:8080/profile/upload",
            formData =
                formData {
                    append(
                        "image",
                        fileContent,
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        },
                    )
                },
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
}
