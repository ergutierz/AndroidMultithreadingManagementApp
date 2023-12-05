package com.example.ammf_core.performance

data class MonitoringData(
    val cpuUtilization: CpuUsage? = null,
    val memoryUsages: List<Double> = emptyList(),
    val threadUsages: List<Int> = emptyList()
)
