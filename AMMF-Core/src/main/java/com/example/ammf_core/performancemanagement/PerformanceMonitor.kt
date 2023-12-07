package com.example.ammf_core.performancemanagement

/**
 * Interface for performance monitoring in the AMMF framework.
 */
interface PerformanceMonitor {

    /**
     * Starts the performance monitoring process.
     */
    fun startMonitoring()

    /**
     * Stops the performance monitoring process.
     */
    fun stopMonitoring()

    /**
     * Retrieves the current CPU utilization percentage.
     *
     * @return The current CPU utilization as a percentage.
     */
    fun getCPUUtilization(): Double

    /**
     * Retrieves the current memory usage percentage.
     *
     * @return The current memory usage as a percentage.
     */
    fun getMemoryUsage(): Double

    /**
     * Retrieves the current number of threads in use.
     *
     * @return The number of threads currently in use.
     */
    fun getThreadUsage(): Int

    /**
     * Registers a listener for performance updates.
     *
     * @param listener The listener to receive updates.
     */
    fun registerPerformanceListener(listener: PerformanceListener)

    /**
     * Unregisters a previously registered performance listener.
     *
     * @param listener The listener to be removed.
     */
    fun unregisterPerformanceListener(listener: PerformanceListener)

    /**
     * Interface for receiving performance data updates.
     */
    interface PerformanceListener {
        /**
         * Called when there is an update in the performance metrics.
         *
         * @param cpuUtilization Current CPU utilization.
         * @param memoryUsage Current memory usage.
         * @param threadUsage Current thread usage.
         */
        fun onPerformanceUpdate(cpuUtilization: Double, memoryUsage: Double, threadUsage: Int)
    }
}
