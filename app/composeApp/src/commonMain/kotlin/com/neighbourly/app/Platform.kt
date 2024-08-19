package com.neighbourly.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform