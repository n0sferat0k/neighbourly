package com.neighbourly.app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_PHONE_NUMBERS
import android.Manifest.permission.READ_PHONE_STATE
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Looper
import android.telephony.SubscriptionManager
import android.telephony.SubscriptionManager.DEFAULT_SUBSCRIPTION_ID
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.net.toUri
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.neighbourly.app.MainActivity.Companion.locationProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp


class AndroidPlatform : Platform {
    override val isWide: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun RequestPermissions() {

    // Initialize the state for managing multiple location permissions.
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            READ_EXTERNAL_STORAGE,
            READ_PHONE_STATE,
            READ_PHONE_NUMBERS
        )
    )

    // Use LaunchedEffect to handle permissions logic when the composition is launched.
    LaunchedEffect(key1 = permissionState) {
        // Check if all previously granted permissions are revoked.
        val allPermissionsRevoked =
            permissionState.permissions.size == permissionState.revokedPermissions.size

        // Filter permissions that need to be requested.
        val permissionsToRequest = permissionState.permissions.filter {
            !it.status.isGranted
        }

        // If there are permissions to request, launch the permission request.
        if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()
    }
}


actual object GetLocation {
    private val delegateCallbacks = mutableSetOf<GeoLocationCallback>()
    private val actualCallback = object : LocationCallback() {
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
                Looper.getMainLooper()
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

actual fun loadImageFromFile(file: MPFile<Any>): BitmapPainter {
    val uri = file.platformFile.toString().toUri()
    val stream = MainActivity.mainActivity.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(stream).asImageBitmap()
    return BitmapPainter(bitmap)
}

@SuppressLint("MissingPermission")
actual fun getPhoneNumber(): String {
    val subscriptionManager = MainActivity.mainActivity.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    return subscriptionManager.getPhoneNumber(DEFAULT_SUBSCRIPTION_ID)
}

actual val httpClientEngine: HttpClientEngine = OkHttp.create()