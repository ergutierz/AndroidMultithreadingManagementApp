package com.example.ammf_core.schedulers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import com.example.ammf_core.threadmanagement.AMMFExecutor

/**
 * Provides a custom Coroutine Dispatcher that utilizes AMMF's thread management.
 */
object AMMFCoroutineDispatcher {
    var isCpuIntensive: Boolean = false
    /**
     * Creates a custom Coroutine Dispatcher that uses AMMF's thread management.
     *
     * @return A custom Coroutine Dispatcher.
     */
    fun create(): CoroutineDispatcher {
        val executor: Executor = AMMFExecutor(isCpuIntensive)
        return executor.asCoroutineDispatcher()
    }
}
