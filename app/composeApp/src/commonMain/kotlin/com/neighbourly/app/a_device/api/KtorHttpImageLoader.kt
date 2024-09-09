package com.neighbourly.app.a_device.api

import com.neighbourly.app.d_entity.interf.HttpImageLoader
import com.neighbourly.app.httpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes

object KtorHttpImageLoader : HttpImageLoader {
    override suspend fun fetchImage(url: String): ByteArray? =
        runCatching {
            val client = HttpClient(httpClientEngine)
            val response: HttpResponse = client.get(url)
            val bytes: ByteArray = response.readBytes()
            client.close()
            return bytes
        }.let {
            if (it.isFailure) {
                it.exceptionOrNull()?.printStackTrace()
            }
            it.getOrNull()
        }
}
