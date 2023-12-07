package com.example.ammf_core.threadmanagement

/**
 * Sealed class representing various scheduling policies for thread management in the AMMF framework.
 */
sealed class SchedulingPolicy(val ordinal: Int) {

    /**
     * First-In, First-Out (FIFO): Executes tasks in the order they were added to the queue.
     * It ensures a predictable execution order.
     */
    data object FIFO : SchedulingPolicy(0)

    /**
     * Round-Robin (RR): Allocates time slices to each thread in a rotating order.
     * It ensures fairness by giving equal CPU time to all threads.
     */
    data object RoundRobin : SchedulingPolicy(1)

    /**
     * Least Recently Used (LRU): Prioritizes tasks that have not been executed for the longest time.
     * This policy is effective in ensuring that older tasks are not starved.
     */
    data object LeastRecentlyUsed : SchedulingPolicy(2)

    /**
     * Priority-Based Scheduling: Assigns a priority to each thread.
     * Threads with higher priority are executed before those with lower priority.
     */
    data class PriorityBased(val priority: Int) : SchedulingPolicy(3)

    /**
     * Shortest Job Next (SJN): Schedules tasks based on the shortest duration first.
     * It aims to reduce the average waiting time in the queue.
     */
    data object ShortestJobNext : SchedulingPolicy(4)

    /**
     * Adaptive Scheduling: Adjusts the scheduling strategy based on current system conditions,
     * such as CPU load, available memory, or application state.
     */
    data object Adaptive : SchedulingPolicy(5)

    /**
     * Time-Slice (Quantum) Based Scheduling: Gives each thread a fixed time slice (quantum) for execution.
     * The scheduler rotates through the threads, ensuring equal opportunity for execution.
     */
    data object TimeSliceBased : SchedulingPolicy(6)

    /**
     * Background/Foreground Scheduling: Categorizes tasks as foreground (interactive) or background (non-interactive).
     * It schedules them accordingly, favoring foreground tasks for better responsiveness.
     */
    data object BackgroundForeground : SchedulingPolicy(7)

    /**
     * I/O Intensive and CPU Intensive Scheduling: Uses separate queues for I/O-bound and CPU-bound tasks.
     * This optimizes throughput by scheduling tasks based on their nature (I/O or computation).
     */
    data object IOCPUIntensive : SchedulingPolicy(8)
}
