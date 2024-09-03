package com.neighbourly.app.a_device.api

import com.neighbourly.app.b_adapt.gw.ApiException
import com.neighbourly.app.b_adapt.gw.LoginInput
import com.neighbourly.app.b_adapt.gw.UserDTO
import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
    private val client = HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json(json = Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }, contentType = ContentType.Any)
        }
    }

    suspend fun login(loginInput: LoginInput): UserDTO {
        val response: HttpResponse = client.post("http://neighbourly.go.ro:8080/login") {
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
        fileContent: ByteArray,
        fileName: String
    ): HttpResponse {
        return client.submitFormWithBinaryData(
            url = "http://neighbourly.go.ro:8080/profile/upload",
            formData = formData {
                append("image", fileContent, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer " + token)
            }
        }
    }
}