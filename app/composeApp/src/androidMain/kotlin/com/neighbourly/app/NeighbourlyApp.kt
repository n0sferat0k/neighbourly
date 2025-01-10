package com.neighbourly.app

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.neighbourly.app.MainActivity.Companion.EXTRA_ITEM_ID
import com.neighbourly.app.a_device.service.GpsTracker
import java.util.concurrent.TimeUnit

class NeighbourlyApp : Application() {
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        appContext = this
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        KoinProvider.initKoin()
        GpsTracker.initialize()

        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                IMPORTANCE_HIGH
            )
        )
    }

    @SuppressLint("NotificationPermission")
    fun showBasicNotification(id: Int, title: String, text: String) {
        val actionIntent = Intent(this, MainActivity::class.java)
        actionIntent.putExtra(EXTRA_ITEM_ID, id)
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(NOTIFICATION_TIMEOUT_MS)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    1,
                    actionIntent,
                    PendingIntent.FLAG_MUTABLE
                )
            )
            .build()

        notificationManager.notify(id, notification)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "neighbourly_notification"
        val NOTIFICATION_TIMEOUT_MS = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS)

        lateinit var appContext: NeighbourlyApp
        val locationProvider: FusedLocationProviderClient
            get() =
                LocationServices.getFusedLocationProviderClient(appContext)
    }
}
