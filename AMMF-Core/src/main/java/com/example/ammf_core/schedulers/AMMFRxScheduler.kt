package com.example.ammf_core.schedulers

import com.example.ammf_core.threadmanagement.AMMFExecutor
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

/**
 * Provides a custom Scheduler for RxJava that utilizes AMMF's thread management.
 */
object AMMFRxScheduler {
    var isCpuIntensive: Boolean = false
    /**
     * Creates a custom RxJava Scheduler that uses AMMF's thread management.
     *
     * @return A custom Scheduler for RxJava.
     */
    fun create(): Scheduler {
        val executor: Executor = AMMFExecutor(isCpuIntensive)
        return Schedulers.from(executor)
    }
}