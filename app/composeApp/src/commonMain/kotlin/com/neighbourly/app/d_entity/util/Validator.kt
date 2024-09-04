package com.neighbourly.app.d_entity.util

val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
val phoneRegex = """^\+?[0-9\s\-()]+$"""

fun String.isValidEmail(): Boolean = matches(emailRegex.toRegex())

fun String.isValidPhone(): Boolean = matches(phoneRegex.toRegex())
