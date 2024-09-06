package com.neighbourly.app.d_entity.interf

interface KeyValueRegistry {
    fun putStrings(map: Map<String, String>)

    fun contains(key: String): Boolean

    fun getString(key: String): String?

    fun getString(
        key: String,
        defaultValue: String,
    ): String

    fun clear()

    fun remove(key: String)

    fun putString(
        key: String,
        value: String?,
    )
}
