package com.example.ammf_core.resourceallocation

/**
 * Interface for dynamic allocation of resources based on system performance.
 */
interface DynamicResourceAllocator {

    /**
     * Dynamically adjusts the number of threads based on current system performance metrics.
     *
     * @param currentMetrics The current performance metrics of the system.
     */
    fun adjustThreadResources(currentMetrics: SystemMetrics)

    /**
     * Sets the thresholds for resource adjustments.
     *
     * @param thresholds The thresholds for various performance metrics.
     */
    fun setAdjustmentThresholds(thresholds: ResourceThresholds)

    /**
     * Retrieves the current resource allocation status.
     *
     * @return The current status of resource allocation.
     */
    fun getCurrentAllocationStatus(): ResourceAllocationStatus
}

/**
 * Data class representing various system performance metrics.
 */
data class SystemMetrics(
    val cpuUsage: Double,
    val memoryUsage: Double,
    val threadCount: Int
)

/**
 * Data class representing thresholds for resource adjustments.
 */
data class ResourceThresholds(
    val cpuUsageThreshold: Double,
    val memoryUsageThreshold: Double,
    val threadCountThreshold: Int
)

/**
 * Data class representing the current status of resource allocation.
 */
data class ResourceAllocationStatus(
    val allocatedThreads: Int,
    val availableThreads: Int
)
