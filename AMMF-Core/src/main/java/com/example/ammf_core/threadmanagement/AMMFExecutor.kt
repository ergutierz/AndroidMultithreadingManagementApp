package com.example.ammf_core.threadmanagement

import java.util.concurrent.Executor

/**
 * Executor implementation that leverages the AMMF framework's thread management capabilities.
 */
class AMMFExecutor(val isCpuIntensive: Boolean) : Executor {

    private val threadManager: ThreadManager = AMMFThreadManager()

    /**
     * Executes the given command at some time in the future using AMMF's thread management.
     *
     * @param command the runnable task
     */
    override fun execute(command: Runnable) {
        threadManager.allocateThread(command, isCpuIntensive)
    }

    /**
     * Releases resources and threads managed by this executor.
     */
    fun shutdown() {
        threadManager.shutdown()
    }
}
