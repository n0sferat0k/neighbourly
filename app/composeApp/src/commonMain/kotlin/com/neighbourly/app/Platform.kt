package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import io.ktor.client.engine.HttpClientEngine

interface Platform {
    val isWide: Boolean
}

expect fun getPlatform(): Platform

@Composable
expect fun requestPermissions()

typealias GeoLocationCallback = (latitude: Double, longitude: Double, accuracy: Float) -> Unit

expect object GetLocation {
    fun addCallback(callback: GeoLocationCallback)

    fun removeCallback(callback: GeoLocationCallback)
}

expect fun loadImageFromFile(file: String): BitmapPainter

expect fun loadContentsFromFile(file: String): FileContents?

expect fun getPhoneNumber(): String

expect val httpClientEngine: HttpClientEngine

expect val keyValueRegistry: KeyValueRegistry

expect val isLargeLandscape: Boolean
