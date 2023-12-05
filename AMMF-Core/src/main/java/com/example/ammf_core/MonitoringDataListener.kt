package com.example.ammf_core

import com.example.ammf_core.performance.MonitoringData

interface MonitoringDataListener {
    fun onDataUpdated(data: MonitoringData)
}
