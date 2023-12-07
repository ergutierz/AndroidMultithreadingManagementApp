package com.example.ammf_core.asyncmanagement

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Interface for managing asynchronous operations in the AMMF framework.
 */
interface AsyncManager {

    /**
     * Returns a custom CoroutineDispatcher integrated with AMMF's thread management.
     *
     * @return CoroutineDispatcher for coroutine execution.
     */
    fun getCoroutineDispatcher(): CoroutineDispatcher

    /**
     * Returns a custom RxJava Scheduler integrated with AMMF's thread management.
     *
     * @return Scheduler for RxJava observable execution.
     */
    fun getRxScheduler(): Scheduler

    /**
     * Executes a coroutine within the AMMF managed scope.
     *
     * @param block The suspend function to be executed.
     */
    fun launchCoroutine(block: suspend CoroutineScope.() -> Unit)

    /**
     * Executes a RxJava task using AMMF's custom Scheduler.
     *
     * @param block The task to be executed, expressed as a lambda that matches a Runnable.
     * @return Disposable instance that allows the task to be cancelled.
     */
    fun executeRxTask(block: () -> Unit): Disposable
}
