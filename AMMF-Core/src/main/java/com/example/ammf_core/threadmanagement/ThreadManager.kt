package com.example.ammf_core.threadmanagement

/**
 * Interface for managing task execution in the AMMF framework.
 */
interface ThreadManager {

    /**
     * Initializes the thread manager with a specified number of threads.
     *
     * @param threadCount The number of threads to be maintained in the pool.
     */
    fun initializeThreadManager(threadCount: Int)

    /**
     * Executes the specified task using the thread pool.
     *
     * @param task The runnable task to be executed.
     */
    fun executeTask(task: Runnable)

    /**
     * Sets the scheduling policy for task execution.
     *
     * @param policy The scheduling policy, e.g., FIFO, Round-Robin.
     */
    fun setSchedulingPolicy(policy: SchedulingPolicy)

    fun setThreadPriority(priority: Int)

    fun allocateThread(runnable: Runnable, isCpuIntensive: Boolean)

    fun shutdown()
}
