package com.example.ammf_core.performancemanagement

interface PerformanceListener {
    fun onUpdates(cpuUtilizations: List<Double>?, memoryUsages: List<Double>?, threadUsages: List<Int>?)
    fun register()
    fun unregister()
}
