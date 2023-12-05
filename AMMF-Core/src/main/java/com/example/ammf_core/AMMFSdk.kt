package com.example.ammf_core

import com.example.ammf_core.performance.PerformanceManager

class AMMFSdk private constructor(
    val threadPoolSize: Int,
    val threadPriority: Int,
    val monitoringInterval: Int,
) {

    val performanceManager = PerformanceManager

    companion object {
        @Volatile private var INSTANCE: AMMFSdk? = null

        fun initialize(
            threadPoolSize: Int = 10,
            threadPriority: Int = Thread.MAX_PRIORITY,
            monitoringInterval: Int = 1000
        ) {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Builder()
                        .setThreadPoolSize(threadPoolSize)
                        .setThreadPriority(threadPriority)
                        .setMonitoringInterval(monitoringInterval)
                        .build()
                }
            }
        }

        fun getInstance(): AMMFSdk {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: throw IllegalStateException("AMMFSdk is not initialized")
            }
        }

        init {
            System.loadLibrary("ammf_core")
        }
    }

    class Builder {
        private var threadPoolSize: Int = 10
        private var threadPriority: Int = Thread.MAX_PRIORITY
        private var monitoringInterval: Int = 1000

        fun setThreadPoolSize(size: Int) = apply { this.threadPoolSize = size }
        fun setThreadPriority(priority: Int) = apply { this.threadPriority = priority }
        fun setMonitoringInterval(interval: Int) = apply { this.monitoringInterval = interval }

        fun build(): AMMFSdk {
            return AMMFSdk(
                threadPoolSize = threadPoolSize,
                threadPriority = threadPriority,
                monitoringInterval = monitoringInterval
            )
        }
    }
}