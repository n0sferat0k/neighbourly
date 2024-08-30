package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.darkrockstudios.libraries.mpfilepicker.MPFile

interface Platform {
    val isWide: Boolean
}

expect fun getPlatform(): Platform

@Composable
expect fun RequestPermissions()

typealias GeoLocationCallback = (latitude: Double, longitude: Double, accuracy: Float) -> Unit

expect object GetLocation {
    fun addCallback(callback: GeoLocationCallback)
    fun removeCallback(callback: GeoLocationCallback)
}

expect fun loadImageFromFile(file: MPFile<Any>): BitmapPainter
