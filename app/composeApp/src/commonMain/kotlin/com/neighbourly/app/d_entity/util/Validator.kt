package com.neighbourly.app.d_entity.util

val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
val phoneRegex = """^\+?[0-9\s\-()]+$""".toRegex()
val urlRegex =
    """(?:[a-zA-Z][a-zA-Z0-9+.-]*://)?(?:\S+(?::\S*)?@)?(?:[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}|localhost|\d{1,3}(\.\d{1,3}){3})(?::\d+)?(?:/[^\s]*)?""".toRegex()
val macRegex = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})${'$'}".toRegex()

fun String.isValidEmail(): Boolean = this.isNotBlank() && matches(emailRegex)

fun String.isValidPhone(): Boolean = this.isNotBlank() && matches(phoneRegex)

fun String.isValidUrl(): Boolean = this.isNotBlank() && matches(urlRegex)

fun String.isValidMac(): Boolean = this.isNotBlank() && matches(macRegex)
