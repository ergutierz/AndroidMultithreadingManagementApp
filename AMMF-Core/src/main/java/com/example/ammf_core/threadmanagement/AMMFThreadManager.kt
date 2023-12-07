package com.example.ammf_core.threadmanagement

/**
 * Implementation of ThreadManager using native thread management.
 */
class AMMFThreadManager : ThreadManager {

    override fun initializeThreadManager(threadCount: Int) {
        nativeInitializeThreadManager(threadCount)
    }

    override fun executeTask(task: Runnable) {
        nativeExecuteTask(task)
    }

    override fun setSchedulingPolicy(policy: SchedulingPolicy) {
        nativeSetSchedulingPolicy(policy.ordinal)
    }

    override fun setThreadPriority(priority: Int) {
        nativeSetThreadPriority(priority)
    }

    override fun allocateThread(runnable: Runnable, isCpuIntensive: Boolean) {
        nativeAllocateThread(runnable, isCpuIntensive)
    }

    override fun shutdown() {
        nativeShutdown()
    }

    // Native method declarations
    private external fun nativeInitializeThreadManager(threadCount: Int)
    private external fun nativeExecuteTask(task: Runnable)
    private external fun nativeSetSchedulingPolicy(policy: Int)
    private external fun nativeSetThreadPriority(priority: Int)
    private external fun nativeAllocateThread(runnable: Runnable, isCpuIntensive: Boolean)
    private external fun nativeShutdown()
}
