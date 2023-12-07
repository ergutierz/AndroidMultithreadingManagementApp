// thread_utility.h
#ifndef CITYTOUR_THREAD_UTILITY_H
#define CITYTOUR_THREAD_UTILITY_H

#include <thread>
#include <vector>
#include <functional>
#include <mutex>
#include <deque>
#include <condition_variable>
#include "../data_structures/adaptive_queue.h"
#include "time_slice.h"

namespace threading {

    class thread_utility {
    public:
        thread_utility(int poolSize);
        ~thread_utility();

        void initializeThreadManager(int threadCount);
        void setThreadPriority(int priority);
        void releaseThread(std::thread &thread);
        void setSchedulingPolicy(int policy);
        void nativeExecuteTask(std::function<void()> task);

        void allocateThread(std::function<void()> task, bool isCpuIntensive);

        void shutdown();

    private:
        std::unique_ptr<time_slice::TimeSlicedPolicy> tsPolicy;
        adaptive_queue::AdaptiveQueue tasks;
        std::vector<std::thread> workers;
        std::mutex queueMutex;
        std::condition_variable condition;
        int currentPolicy;
        bool stop;
        int defaultPriority;

        void workerThread();
        void fifoPolicy();
        void roundRobinPolicy();
        void leastRecentlyUsedPolicy();
        void priorityBasedPolicy();
        void shortestJobNextPolicy();
        void adaptivePolicy();
        void timeSlicedPolicy();
        void backgroundForegroundPolicy();
        void icpuIntensivePolicy();
        void applySchedulingPolicy(int policy);
        void errorHandler();

        adaptive_queue::Task selectHighPriorityTask();

        bool checkSystemLoad();

    };
} // namespace threading

#endif // CITYTOUR_THREAD_UTILITY_H
