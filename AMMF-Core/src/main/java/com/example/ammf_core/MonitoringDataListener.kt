package com.example.ammf_core

import com.example.ammf_core.performancemanagement.MonitoringData

interface MonitoringDataListener {
    fun onDataUpdated(data: MonitoringData)
}
