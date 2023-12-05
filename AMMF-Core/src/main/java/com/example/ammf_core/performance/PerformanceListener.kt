package com.example.ammf_core.performance

interface PerformanceListener {
    fun onUpdates(cpuUtilizations: List<Double>?, memoryUsages: List<Double>?, threadUsages: List<Int>?)
    fun register()
    fun unregister()
}
