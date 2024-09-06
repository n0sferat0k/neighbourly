package com.neighbourly.app

import android.content.SharedPreferences
import com.neighbourly.app.d_entity.interf.KeyValueRegistry

class SharedPrefsBasedRegistry(
    val pref: SharedPreferences,
) : KeyValueRegistry {
    override fun putStrings(map: Map<String, String>) {
        pref.edit().apply {
            map.forEach { key, value ->
                putString(key, value)
            }
            apply()
        }
    }

    override fun putString(
        key: String,
        value: String?,
    ) {
        pref.edit().putString(key, value).apply()
    }

    override fun contains(key: String): Boolean = pref.contains(key)

    override fun getString(key: String): String? = pref.getString(key, null)

    override fun getString(
        key: String,
        defaultValue: String,
    ) = pref.getString(key, defaultValue) ?: defaultValue

    override fun remove(key: String) {
        pref.edit().remove(key).apply()
    }

    override fun clear() = pref.edit().clear().apply()
}
