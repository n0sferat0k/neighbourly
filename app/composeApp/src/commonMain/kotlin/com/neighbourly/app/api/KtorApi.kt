package com.neighbourly.app.api

import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val client = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }, contentType = ContentType.Any)
    }
}

suspend fun uploadImage(token: String, fileContent: ByteArray, fileName: String): HttpResponse {
    return client.submitFormWithBinaryData(
        url = "http://neighbourly.go.ro:8080/upload",
        formData = formData {
            append("image", fileContent, Headers.build {
                append(HttpHeaders.ContentType, "image/jpeg")
                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
            })
        }
    ) {
        headers {
            append(HttpHeaders.Authorization, token)
        }
    }
}