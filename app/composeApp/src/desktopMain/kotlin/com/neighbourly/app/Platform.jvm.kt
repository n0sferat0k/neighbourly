package com.neighbourly.app

class JVMPlatform : Platform {
    override val isWide: Boolean = true
}

actual fun getPlatform(): Platform = JVMPlatform()