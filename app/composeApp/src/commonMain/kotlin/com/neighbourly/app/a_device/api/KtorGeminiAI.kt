package com.neighbourly.app.a_device.api

import com.neighbourly.app.b_adapt.gateway.ai.AIResponseGemini
import com.neighbourly.app.b_adapt.gateway.ai.GenerateInputGemini
import com.neighbourly.app.b_adapt.gateway.ai.bean.AiException
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

object KtorGeminiAI {
    private val client = HttpClient(httpClientEngine) {
        install(HttpTimeout) {
            requestTimeoutMillis = TimeUnit.MINUTES.toMillis(10)
            connectTimeoutMillis = TimeUnit.MINUTES.toMillis(10)
            socketTimeoutMillis = TimeUnit.MINUTES.toMillis(10)
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
        apiKey: String,
        generateInput: GenerateInputGemini,
    ): AIResponseGemini {
        val response: HttpResponse =
            client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody(generateInput)
            }
        if (response.status.value == 200) {
            return response.body<AIResponseGemini>()
        } else {
            throw AiException(response.bodyAsText())
        }
    }
}