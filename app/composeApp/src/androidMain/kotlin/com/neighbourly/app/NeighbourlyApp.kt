package com.neighbourly.app

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.neighbourly.app.MainActivity.Companion.EXTRA_INDIRECTION
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
    fun showBasicNotification(id: String?, title: String, text: String) {
        val actionIntent = Intent(this, MainActivity::class.java)
        actionIntent.putExtra(EXTRA_INDIRECTION, id)
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setPriority(IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(NOTIFICATION_TIMEOUT_MS)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setContentIntent(
                getActivity(
                    this,
                    1,
                    actionIntent,
                    FLAG_UPDATE_CURRENT or FLAG_MUTABLE
                )
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1234
        const val NOTIFICATION_CHANNEL_ID = "neighbourly_notification"
        val NOTIFICATION_TIMEOUT_MS = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS)

        lateinit var appContext: NeighbourlyApp
        val locationProvider: FusedLocationProviderClient
            get() =
                LocationServices.getFusedLocationProviderClient(appContext)
    }
}
