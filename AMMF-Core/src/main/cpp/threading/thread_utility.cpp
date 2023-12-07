//
// Created by erikg on 11/1/2023.
//

#include "thread_utility.h"
#include <iostream>
#include <deque>
#include <mutex>
#include <condition_variable>
#include <functional>
#include <thread>

namespace threading {

    thread_utility::thread_utility(int poolSize) {
        defaultPriority = 0;
        stop = false;
    }

    thread_utility::~thread_utility() {
        stop = true;
        condition.notify_all();
        for (auto &worker : workers) {
            if (worker.joinable()) {
                worker.join();
            }
        }
    }

    void thread_utility::initializeThreadManager(int threadCount) {
        for (int i = 0; i < threadCount; ++i) {
            workers.emplace_back(std::bind(&thread_utility::workerThread, this));
        }
    }

    void thread_utility::setThreadPriority(int priority) {
        defaultPriority = priority;
        // Setting priority on Android is limited by its security model
        // This implementation is more of a placeholder
    }

    void thread_utility::allocateThread(std::function<void()> task) {
        std::unique_lock<std::mutex> lock(queueMutex);
        adaptive_queue::Task newTask(task);
        tasks.push(newTask); // Using the push method of AdaptiveQueue
        lock.unlock();
        condition.notify_one();
    }


    void thread_utility::releaseThread(std::thread &thread) {
        if (thread.joinable()) {
            thread.join();
        }
    }

    void thread_utility::workerThread() {
        while (true) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });
            if (stop && tasks.empty()) {
                return;
            }
            auto task = tasks.pop(); // Using the pop method of AdaptiveQueue
            lock.unlock();
            task.action();
        }
    }

    void thread_utility::setSchedulingPolicy(int policy) {
        applySchedulingPolicy(policy);
    }

    void thread_utility::applySchedulingPolicy(int policyOrdinal) {
        currentPolicy = policyOrdinal;
        switch (policyOrdinal) {
            case 0: fifoPolicy(); break;
            case 1: roundRobinPolicy(); break;
            case 2: leastRecentlyUsedPolicy(); break;
            case 3: priorityBasedPolicy(); break;
            case 4: shortestJobNextPolicy(); break;
            case 5: adaptivePolicy(); break;
            case 6: timeSlicedPolicy(); break;
            case 7: backgroundForegroundPolicy(); break;
            case 8: icpuIntensivePolicy(); break;
            default: errorHandler(); break;
        }
    }

    void thread_utility::fifoPolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop) {
                break; // Exit the loop if stop flag is set
            }

            if (!tasks.empty()) {
                auto task = tasks.pop(); // FIFO: Pop the first task in the queue
                lock.unlock();
                task.action(); // Execute the task
            }
        }
    }

    void thread_utility::roundRobinPolicy() {

    }

    void thread_utility::leastRecentlyUsedPolicy() {

    }

    void thread_utility::priorityBasedPolicy() {

    }

    void thread_utility::shortestJobNextPolicy() {

    }

    void thread_utility::adaptivePolicy() {

    }

    void thread_utility::timeSlicedPolicy() {
        // Implement Time-Slice (Quantum) Based scheduling logic
    }

    void thread_utility::backgroundForegroundPolicy() {

    }

    void thread_utility::icpuIntensivePolicy() {

    }

    void thread_utility::errorHandler() {

    }

    void thread_utility::nativeExecuteTask(std::function<void()> task) {

    }

} // thread_utility