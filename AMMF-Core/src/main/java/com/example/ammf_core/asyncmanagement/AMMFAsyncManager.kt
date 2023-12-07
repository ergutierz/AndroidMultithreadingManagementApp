package com.example.ammf_core.asyncmanagement

import com.example.ammf_core.schedulers.AMMFCoroutineDispatcher
import com.example.ammf_core.schedulers.AMMFRxScheduler
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Manager class for asynchronous operations, providing custom dispatchers and schedulers.
 */
class AMMFAsyncManager : AsyncManager {

    /**
     * Provides a custom Coroutine Dispatcher that uses AMMF's thread management.
     *
     * @return A custom Coroutine Dispatcher.
     */
    override fun getCoroutineDispatcher(): CoroutineDispatcher {
        return AMMFCoroutineDispatcher.create()
    }

    /**
     * Provides a custom RxJava Scheduler that uses AMMF's thread management.
     *
     * @return A custom RxJava Scheduler.
     */
    override fun getRxScheduler(): Scheduler {
        return AMMFRxScheduler.create()
    }

    /**
     * Launches a coroutine using AMMF's custom Coroutine Dispatcher.
     *
     * @param block The coroutine code block to be executed.
     */
    override fun launchCoroutine(block: suspend CoroutineScope.() -> Unit) {
        CoroutineScope(getCoroutineDispatcher()).launch {
            block()
        }
    }

    /**
     * Executes a RxJava task using AMMF's custom Scheduler.
     *
     * @param block The RxJava task to be executed.
     * @return A Disposable representing the task, allowing for cancellation.
     */
    override fun executeRxTask(block: () -> Unit): Disposable {
        return getRxScheduler().scheduleDirect(block)
    }
}
