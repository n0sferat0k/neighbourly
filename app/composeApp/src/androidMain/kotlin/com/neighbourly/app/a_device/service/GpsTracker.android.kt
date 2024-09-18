package com.neighbourly.app.a_device.service

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.MainActivity
import com.neighbourly.app.NeighbourlyApp.Companion.appContext
import com.neighbourly.app.R
import com.neighbourly.app.c_business.usecase.profile.HouseholdLocalizeUseCase
import com.neighbourly.app.d_entity.interf.SessionStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

object GpsTracker {
    var isTracking = false

    fun initialize() {
        KoinProvider.KOIN
            .get<SessionStore>()
            .user
            .onEach { user ->
                (user?.localizing ?: false).let {
                    if (it != isTracking) {
                        isTracking = it
                        when (isTracking) {
                            true -> startTracking()
                            false -> stopTracking()
                        }
                    }
                }
            }.launchIn(MainScope())
    }

    fun startTracking() {
        val intent = Intent(appContext, GpsTrackingService::class.java)
        ContextCompat.startForegroundService(appContext, intent)
    }

    fun stopTracking() {
        val intent = Intent(appContext, GpsTrackingService::class.java)
        appContext.stopService(intent)
    }
}

class GpsTrackingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val householdLocalizeUseCase: HouseholdLocalizeUseCase = KoinProvider.KOIN.get()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startForeground(1, createNotification())
        startLocationUpdates()
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "GPS_TRACKING_CHANNEL"

        // Create a notification manager
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For API 26 and above, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    notificationChannelId,
                    "GPS Tracking Service",
                    NotificationManager.IMPORTANCE_LOW,
                )
            notificationManager.createNotificationChannel(channel)
        }

        // Create an intent that will open your main activity when the notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

        // Build the notification
        val notificationBuilder =
            NotificationCompat
                .Builder(this, notificationChannelId)
                .setContentTitle("GPS Tracking Active")
                .setContentText("Your household location is being established")
                .setSmallIcon(R.drawable.ic_location) // Replace with your app's icon
                .setContentIntent(pendingIntent)
                .setOngoing(true)

        return notificationBuilder.build()
    }

    private fun startLocationUpdates() {
        val locationRequest =
            LocationRequest
                .Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    600000L,
                ).setMinUpdateIntervalMillis(600000L)
                .build()

        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
            checkSelfPermission(ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )
    }

    private val locationCallback =
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    MainScope().launch {
                        kotlin.runCatching {
                            householdLocalizeUseCase.reportLocation(
                                it.latitude.toFloat(),
                                it.longitude.toFloat(),
                            )
                        }
                    }
                }
            }
        }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not using IPC, so return null
    }
}
