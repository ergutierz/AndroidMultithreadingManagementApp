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
        nativeSetSchedulingPolicy(policy.ordinal) // Assuming ordinal value of enum is used
    }

    // Native method declarations
    private external fun nativeInitializeThreadManager(threadCount: Int)
    private external fun nativeExecuteTask(task: Runnable)
    private external fun nativeSetSchedulingPolicy(policy: Int)
}
