package com.example.ammf_core.performancemanagement

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.ammf_core.AMMFSdk

class PerformanceMonitoringService : Service() {

    private val binder = LocalBinder()
    private var isMonitoring = false

    inner class LocalBinder : Binder() {
        fun getService(): PerformanceMonitoringService = this@PerformanceMonitoringService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun startMonitoring() {
        if (!isMonitoring) {
            isMonitoring = true
            AMMFSdk.getInstance().performanceManager.startMonitoringPerformance()
        }
    }

    fun stopMonitoring() {
        if (isMonitoring) {
            isMonitoring = false
            AMMFSdk.getInstance().performanceManager.stopMonitoringPerformance()
        }
    }

    override fun onDestroy() {
        stopMonitoring()
        super.onDestroy()
    }
}

