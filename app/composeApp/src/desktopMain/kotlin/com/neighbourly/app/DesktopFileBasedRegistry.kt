package com.neighbourly.app

import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.Properties

class DesktopFileBasedRegistry : KeyValueRegistry {
    private val properties = Properties()

    private val appDirName = ".Neighbourly"
    private val fileName = "settings.properties"
    private val userHome = System.getProperty("user.home")
    private val appDirectory = File(userHome, appDirName)
    private val file = File(appDirectory, fileName)

    init {
        // Create directory if it doesn't exist
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }

        // Load properties from file if it exists
        if (file.exists()) {
            FileReader(file).use { properties.load(it) }
        }
    }

    override fun putStrings(map: Map<String, String>) {
        properties.putAll(map)
        saveToFile()
    }

    override fun contains(key: String) = properties.containsKey(key)

    override fun getString(key: String): String? = properties.getProperty(key, null)

    override fun getString(
        key: String,
        defaultValue: String,
    ): String = properties.getProperty(key, defaultValue)

    override fun clear() {
        properties.clear()
        saveToFile()
    }

    override fun remove(key: String) {
        properties.remove(key)
        saveToFile()
    }

    override fun putString(
        key: String,
        value: String?,
    ) {
        if (value != null) {
            properties[key] = value
        } else {
            properties.remove(key)
        }
        saveToFile()
    }

    private fun saveToFile() {
        FileWriter(file).use { properties.store(it, null) }
    }
}
