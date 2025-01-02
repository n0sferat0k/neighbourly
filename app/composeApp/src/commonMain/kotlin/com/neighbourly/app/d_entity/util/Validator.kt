package com.neighbourly.app.d_entity.util

val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
val phoneRegex = """^\+?[0-9\s\-()]+$""".toRegex()
val urlRegex =
    """(?:[a-zA-Z][a-zA-Z0-9+.-]*://)?(?:\S+(?::\S*)?@)?(?:[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}|localhost|\d{1,3}(\.\d{1,3}){3})(?::\d+)?(?:/[^\s]*)?""".toRegex()

fun String.isValidEmail(): Boolean = matches(emailRegex)

fun String.isValidPhone(): Boolean = matches(phoneRegex)

fun String.isValidUrl(): Boolean = matches(urlRegex)
