@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.neighbourly.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_PHONE_NUMBERS
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.provider.OpenableColumns
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionManager.DEFAULT_SUBSCRIPTION_ID
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.net.toUri
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.neighbourly.app.NeighbourlyApp.Companion.locationProvider
import com.neighbourly.app.a_device.store.StatusMemoryStore
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import java.nio.file.Paths
import kotlin.math.ceil
import kotlin.math.max

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun requestPermissions() {
    // Initialize the state for managing multiple location permissions.
    val permissionState =
        rememberMultiplePermissionsState(
            listOf(
                CAMERA,
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
                READ_EXTERNAL_STORAGE,
                READ_PHONE_STATE,
                READ_PHONE_NUMBERS,
            ),
        )

    // Use LaunchedEffect to handle permissions logic when the composition is launched.
    LaunchedEffect(key1 = permissionState) {
        // Check if all previously granted permissions are revoked.
        val allPermissionsRevoked =
            permissionState.permissions.size == permissionState.revokedPermissions.size

        // Filter permissions that need to be requested.
        val permissionsToRequest =
            permissionState.permissions.filter {
                !it.status.isGranted
            }

        // If there are permissions to request, launch the permission request.
        if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()
    }
}

actual object GetLocation {
    private val delegateCallbacks = mutableSetOf<GeoLocationCallback>()
    private val actualCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    if (it.accuracy < 30) {
                        for (delegateCallback in delegateCallbacks) {
                            delegateCallback(it.latitude, it.longitude, it.accuracy)
                        }
                    }
                }
            }
        }

    actual fun addCallback(callback: GeoLocationCallback) {
        if (delegateCallbacks.isEmpty()) {
            locationProvider.requestLocationUpdates(
                LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 5000).build(),
                actualCallback,
                Looper.getMainLooper(),
            )
        }
        delegateCallbacks.add(callback)
    }

    actual fun removeCallback(callback: GeoLocationCallback) {
        delegateCallbacks.remove(callback)
        if (delegateCallbacks.isEmpty()) {
            locationProvider.removeLocationUpdates(actualCallback)
        }
    }
}

actual fun loadImageFromFile(file: String, maxSizePx: Int): BitmapPainter? {
    val stream = {
        NeighbourlyApp.appContext.contentResolver.openInputStream(file.toUri())
    }

    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    options.inSampleSize = 1
    BitmapFactory.decodeStream(stream(), null, options)

    // Calculate inSampleSize
    val maxDim = max(options.outHeight, options.outWidth)
    if (maxDim > maxSizePx) {
        options.inSampleSize = ceil(maxDim.toDouble() / maxSizePx.toDouble()).toInt()
    }

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeStream(stream(), null, options)?.let { bitmap ->
        BitmapPainter(bitmap.asImageBitmap())
    }
}

actual fun loadImageFromFile(file: String): BitmapPainter? =
    BitmapPainter(
        BitmapFactory
            .decodeStream(
                NeighbourlyApp.appContext.contentResolver.openInputStream(
                    file.toUri(),
                ),
            ).asImageBitmap(),
    )

actual fun loadContentsFromFile(file: String): FileContents? {
    val fileUri = file.toUri()
    NeighbourlyApp.appContext.contentResolver.let { contentResolver ->

        var fileName = Paths.get(file).fileName.toString()

        contentResolver
            .query(fileUri, null, null, null, null)
            ?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }

        return contentResolver
            .openInputStream(fileUri)
            ?.readAllBytes()
            ?.let {
                FileContents(
                    content = it,
                    type = contentResolver.getType(fileUri).orEmpty(),
                    name = fileName,
                )
            }
    }
}

@SuppressLint("MissingPermission")
actual fun getPhoneNumber(): String? =
    runCatching {
        (NeighbourlyApp.appContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager).getPhoneNumber(
            DEFAULT_SUBSCRIPTION_ID
        )
    }.getOrNull()


actual val httpClientEngine: HttpClientEngine = OkHttp.create()
actual val keyValueRegistry: KeyValueRegistry =
    SharedPrefsBasedRegistry(
        NeighbourlyApp.appContext.getSharedPreferences(
            "default",
            Application.MODE_PRIVATE,
        ),
    )

actual class PlatformBitmap actual constructor(width: Int, height: Int) {
    private val innerBmp: Bitmap

    init {
        innerBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    actual fun setPixel(x: Int, y: Int, value: Int) =
        innerBmp.setPixel(x, y, value)

    actual fun asImageBitmap(): ImageBitmap = innerBmp.asImageBitmap()
}

actual val databaseDriver: SqlDriver
    get() = AndroidSqliteDriver(NeighbourlyDB.Schema, NeighbourlyApp.appContext, "neighbourly.db")

actual val statusConfigSource = object : StatusMemoryStore() {
    init {
        NeighbourlyApp.appContext.registerComponentCallbacks(object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                _wideScreenFlow.update { wideScreen() }
            }

            override fun onLowMemory() {}
        })
    }

    private fun wideScreen() = NeighbourlyApp.appContext.resources.getBoolean(R.bool.widescreen)
    private val _wideScreenFlow = MutableStateFlow(wideScreen())
    override val wideScreenFlow: Flow<Boolean>
        get() = _wideScreenFlow.asSharedFlow()

}