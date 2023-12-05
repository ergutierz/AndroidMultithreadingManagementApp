package com.example.citytour

import android.app.Application
import com.example.ammf_core.AMMFSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CityApp : Application(){

    override fun onCreate() {
        super.onCreate()
        setupAMMFSdk()
    }

    private fun setupAMMFSdk() {
        AMMFSdk.initialize(
            threadPoolSize = 10,
            threadPriority = Thread.MAX_PRIORITY,
            monitoringInterval = 1000
        )
    }
}