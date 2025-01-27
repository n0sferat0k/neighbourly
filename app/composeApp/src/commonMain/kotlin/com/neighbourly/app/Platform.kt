@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import app.cash.sqldelight.db.SqlDriver
import com.neighbourly.app.a_device.store.StatusMemoryStore
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.data.ScheduledWork
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import io.ktor.client.engine.HttpClientEngine

@Composable
expect fun requestPermissions()

typealias GeoLocationCallback = (latitude: Double, longitude: Double, accuracy: Float) -> Unit

expect object GetLocation {
    fun addCallback(callback: GeoLocationCallback)

    fun removeCallback(callback: GeoLocationCallback)
}

expect fun loadImageFromFile(file: String): BitmapPainter?

expect fun loadImageFromFile(file: String, maxSizePx: Int): BitmapPainter?

expect fun loadNameFromFile(file: String): String

expect fun loadContentsFromFile(file: String): FileContents?

expect fun getPhoneNumber(): String?

expect suspend fun postSystemNotification(id: String? = null, title: String? = null, text: String)

expect val httpClientEngine: HttpClientEngine

expect val keyValueRegistry: KeyValueRegistry

expect val statusConfigSource: StatusMemoryStore

expect val appVersionString: String

expect class PlatformBitmap(width: Int, height: Int) {
    fun setPixel(x: Int, y: Int, value: Int)
    fun asImageBitmap(): ImageBitmap
}

expect val databaseDriver: SqlDriver

expect fun requestFutureWork(work: ScheduledWork)

fun createDatabase(): NeighbourlyDB {
    return NeighbourlyDB(databaseDriver)
}