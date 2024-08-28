package com.neighbourly.app

class AndroidPlatform : Platform {
    override val isWide: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()