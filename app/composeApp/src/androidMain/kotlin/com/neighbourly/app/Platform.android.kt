package com.neighbourly.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_PHONE_NUMBERS
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Looper
import android.provider.OpenableColumns
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionManager.DEFAULT_SUBSCRIPTION_ID
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.neighbourly.app.MainActivity.Companion.locationProvider
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.nio.file.Paths

class AndroidPlatform : Platform {
    override val isWide: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun requestPermissions() {
    // Initialize the state for managing multiple location permissions.
    val permissionState =
        rememberMultiplePermissionsState(
            listOf(
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

actual fun loadImageFromFile(file: String): BitmapPainter =
    BitmapPainter(
        BitmapFactory
            .decodeStream(
                MainActivity.mainActivity.contentResolver.openInputStream(
                    file.toUri(),
                ),
            ).asImageBitmap(),
    )

actual fun loadImageFromFileContents(content: ByteArray): BitmapPainter =
    BitmapPainter(
        BitmapFactory.decodeByteArray(content, 0, content.size).asImageBitmap(),
    )

actual fun loadContentsFromFile(file: String): FileContents? {
    val fileUri = file.toUri()
    MainActivity.mainActivity.contentResolver.let { contentResolver ->

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
actual fun getPhoneNumber(): String {
    val subscriptionManager =
        MainActivity.mainActivity.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    return subscriptionManager.getPhoneNumber(DEFAULT_SUBSCRIPTION_ID)
}

actual val httpClientEngine: HttpClientEngine = OkHttp.create()
actual val keyValueRegistry: KeyValueRegistry =
    SharedPrefsBasedRegistry(
        MainActivity.mainActivity.getSharedPreferences(
            "default",
            Application.MODE_PRIVATE,
        ),
    )
actual val isLargeLandscape: Boolean
    get() = TODO("Not yet implemented")
