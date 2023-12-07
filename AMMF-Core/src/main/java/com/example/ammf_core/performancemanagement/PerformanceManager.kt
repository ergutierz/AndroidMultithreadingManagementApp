package com.example.ammf_core.performancemanagement

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object PerformanceManager : PerformanceListener {

    private val _performanceState = MutableStateFlow(MonitoringData())
    val performanceState: StateFlow<MonitoringData> = _performanceState.asStateFlow()


    private val binding: PerformanceBinding = PerformanceBinding(this)

    override fun register() {
        binding.register()
    }

    override fun unregister() {
        binding.unregister()
    }

    override fun onUpdates(cpuUtilizations: List<Double>?, memoryUsages: List<Double>?, threadUsages: List<Int>?) {
        _performanceState.update { oldState ->
            oldState.copy(
                cpuUtilization = CpuUsageManager.getCpuUsage(),
                memoryUsages = memoryUsages ?: emptyList(),
                threadUsages = threadUsages ?: emptyList()
            ) }
    }

    fun clearData() {
        _performanceState.update { MonitoringData() }
    }

    fun cpuUtilization(): CpuUsage {
        return CpuUsageManager.getCpuUsage()
    }

    fun memoryUsage(): Double {
        return getMemoryUsage()
    }

    fun threadUsage(): Int {
        return getThreadUsage()
    }

    fun startMonitoringPerformance() {
        startMonitoring()
    }

    fun stopMonitoringPerformance() {
        stopMonitoring()
    }

    fun getMonitoringData(): MonitoringData {
        return getMonitoredData()
    }

    private external fun getMonitoredData(): MonitoringData
    private external fun getCPUUtilization(): Double
    private external fun getMemoryUsage(): Double
    private external fun getThreadUsage(): Int
    private external fun startMonitoring()
    private external fun stopMonitoring()
}