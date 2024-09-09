package com.neighbourly.app.d_entity.interf

interface HttpImageLoader {
    suspend fun fetchImage(url: String): ByteArray?
}
