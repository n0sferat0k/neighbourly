package com.neighbourly.app

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.neighbourly.app.a_device.service.GpsTracker

class NeighbourlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        KoinProvider.initKoin()
        GpsTracker.initialize()
    }

    companion object {
        lateinit var appContext: NeighbourlyApp
        val locationProvider: FusedLocationProviderClient
            get() =
                LocationServices.getFusedLocationProviderClient(appContext)
    }
}
