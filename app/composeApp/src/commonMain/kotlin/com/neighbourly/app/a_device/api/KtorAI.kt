package com.neighbourly.app.a_device.api

import com.neighbourly.app.b_adapt.gateway.ai.AIResponse
import com.neighbourly.app.b_adapt.gateway.ai.AiException
import com.neighbourly.app.b_adapt.gateway.ai.GenerateInput
import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object KtorAI {
    private val client = HttpClient(httpClientEngine) {
        install(HttpTimeout) {
            requestTimeoutMillis = TimeUnit.MINUTES.toMillis(5)
            connectTimeoutMillis = TimeUnit.MINUTES.toMillis(5)
            socketTimeoutMillis = TimeUnit.MINUTES.toMillis(5)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("HTTP Client " + message)
                }
            }
            level = LogLevel.BODY
        }
        install(ContentNegotiation) {
            json(
                Json {
                    encodeDefaults = true
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                },
                contentType = ContentType.Any,
            )
        }

    }

    suspend fun generate(
        generateInput: GenerateInput,
    ): AIResponse {
        val response: HttpResponse = client.post("http://localhost:11434/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(generateInput)

        }
        if (response.status.value == 200) {
            return response.body<AIResponse>()
        } else {
            throw AiException(response.bodyAsText())
        }
    }
}