//
// Created by erikg on 11/1/2023.
//

#include "thread_utility.h"
#include <fstream>
#include <sstream>
#include <string>
#include <iostream>
#include <deque>
#include <mutex>
#include <random>
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

    void thread_utility::allocateThread(std::function<void()> task, bool isCpuIntensive) {
        std::unique_lock<std::mutex> lock(queueMutex);
        adaptive_queue::Task cpuIntensive(task, 0); // 0 is the default priority
        cpuIntensive.isCpuIntensive = isCpuIntensive;
        tasks.push(cpuIntensive);
        lock.unlock();
        condition.notify_one();
    }

    void thread_utility::shutdown() {
        stop = true;
        condition.notify_all();
        for (auto &worker : workers) {
            releaseThread(worker);
        }
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
            auto task = tasks.pop();
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
                auto task = tasks.pop();
                lock.unlock();
                task.action();
            }
        }
    }

    void thread_utility::roundRobinPolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this]{ return !tasks.empty() || stop; });

            if (stop) {
                break;
            }

            if (!tasks.empty()) {
                auto task = tasks.pop();
                lock.unlock();
                task.action();
                lock.lock();

                if (!stop) {
                    tasks.push(task);
                }
            }
        }
    }


    void thread_utility::leastRecentlyUsedPolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this]{ return !tasks.empty() || stop; });

            if (stop) {
                break;
            }

            if (!tasks.empty()) {
                // Find the least recently used task
                auto leastRecentlyUsedIt = std::min_element(
                        tasks.begin(), tasks.end(),
                        [](const adaptive_queue::Task& a, const adaptive_queue::Task& b) {
                            return a.lastExecutionTime < b.lastExecutionTime;
                        }
                );

                if (leastRecentlyUsedIt != tasks.end()) {
                    adaptive_queue::Task lruTask = *leastRecentlyUsedIt;
                    tasks.erase(leastRecentlyUsedIt);

                    lock.unlock();
                    lruTask.action(); // Execute the task
                    lock.lock();

                    if (!stop) {
                        lruTask.lastExecutionTime = std::chrono::steady_clock::now();
                        tasks.push_back(lruTask);
                    }
                }
            }
        }
    }


    void thread_utility::priorityBasedPolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop && tasks.empty()) break;

            if (!tasks.empty()) {
                adaptive_queue::Task task = tasks.pop();
                lock.unlock();
                task.action();
            }
        }
    }

    void thread_utility::shortestJobNextPolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop && tasks.empty()) break;

            if (!tasks.empty()) {
                adaptive_queue::Task task = tasks.pop();
                lock.unlock();
                task.action();
            }
        }
    }

    void thread_utility::adaptivePolicy() {
        while (true) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop) {
                return;
            }

            bool isHighLoad = checkSystemLoad();

            adaptive_queue::Task selectedTask;
            if (isHighLoad) {
                // Under high load, prioritize high-priority tasks
                selectedTask = selectHighPriorityTask();
            } else {
                // Under normal conditions, use a regular FIFO approach or other criteria
                selectedTask = tasks.pop();
            }

            lock.unlock();
            selectedTask.action();
            lock.lock();

        }
    }

    bool thread_utility::checkSystemLoad() {
        // Check Memory Load
        std::ifstream memInfoFile("/proc/meminfo");
        if (!memInfoFile.is_open()) {
            // Handle error for memory info file
            return false;
        }

        std::string line;
        int totalMemory = 0;
        int freeMemory = 0;
        while (std::getline(memInfoFile, line)) {
            std::istringstream iss(line);
            std::string key;
            int value;
            std::string unit;
            iss >> key >> value >> unit;

            if (key == "MemTotal:") {
                totalMemory = value;
            } else if (key == "MemFree:") {
                freeMemory = value;
            }
        }
        memInfoFile.close();
        double usedMemoryPercentage = totalMemory > 0 ? ((totalMemory - freeMemory) / static_cast<double>(totalMemory)) * 100.0 : 0.0;
        const double highMemoryUsageThreshold = 75.0; // Example threshold

        // Check CPU Load
        std::ifstream cpuInfoFile("/proc/cpuinfo");
        if (!cpuInfoFile.is_open()) {
            // Handle error for CPU info file
            return false;
        }

        int cpuCount = 0;
        while (std::getline(cpuInfoFile, line)) {
            if (line.substr(0, 9) == "processor") {
                cpuCount++;
            }
        }
        cpuInfoFile.close();

        // Define a threshold for considering high CPU load
        const int highCpuCountThreshold = 4; // For example, more than 4 CPUs is considered high load

        // Return true if either memory usage or CPU count is above the threshold
        return usedMemoryPercentage > highMemoryUsageThreshold || cpuCount > highCpuCountThreshold;
    }

    // Helper method to select a high-priority task
    adaptive_queue::Task thread_utility::selectHighPriorityTask() {
        int highestPriority = std::numeric_limits<int>::min();
        auto highestPriorityTaskIter = tasks.begin();
        for (auto it = tasks.begin(); it != tasks.end(); ++it) {
            if (it->priority > highestPriority) {
                highestPriority = it->priority;
                highestPriorityTaskIter = it;
            }
        }

        adaptive_queue::Task selectedTask = *highestPriorityTaskIter;
        tasks.erase(highestPriorityTaskIter);
        return selectedTask;
    }

    void thread_utility::timeSlicedPolicy() {
        // Create a new TimeSlicedPolicy object with a specific time slice duration
        tsPolicy = std::make_unique<time_slice::TimeSlicedPolicy>(std::chrono::milliseconds(100));
        tsPolicy->start(); // Start the time-sliced execution

        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop) {
                tsPolicy->stop();
                break;
            }

            if (!tasks.empty()) {
                auto task = tasks.pop();
                lock.unlock();
                tsPolicy->addTask(task.action); // Add the task to the time-sliced scheduler
            }
        }
    }

    void thread_utility::backgroundForegroundPolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop) {
                break;
            }

            if (!tasks.empty()) {
                // Prioritize foreground tasks
                auto foregroundTaskIter = std::find_if(tasks.begin(), tasks.end(), [](const auto& task) {
                    return task.isForeground;
                });

                if (foregroundTaskIter != tasks.end()) {
                    // Execute the foreground task
                    adaptive_queue::Task task = *foregroundTaskIter;
                    tasks.erase(foregroundTaskIter);
                    lock.unlock();
                    task.action();
                } else {
                    // If no foreground task, execute the next task (background)
                    auto task = tasks.pop();
                    lock.unlock();
                    task.action();
                }
            }
        }
    }


    void thread_utility::icpuIntensivePolicy() {
        while (!stop) {
            std::unique_lock<std::mutex> lock(queueMutex);
            condition.wait(lock, [this] { return !tasks.empty() || stop; });

            if (stop) {
                break;
            }

            if (!tasks.empty()) {
                std::deque<adaptive_queue::Task> cpuIntensiveTasks;
                std::deque<adaptive_queue::Task> ioIntensiveTasks;

                while (!tasks.empty()) {
                    auto task = tasks.pop();
                    if (task.isCpuIntensive) {
                        cpuIntensiveTasks.push_back(task);
                    } else {
                        ioIntensiveTasks.push_back(task);
                    }
                }

                // Process CPU intensive tasks first
                while (!cpuIntensiveTasks.empty()) {
                    auto task = cpuIntensiveTasks.front();
                    cpuIntensiveTasks.pop_front();
                    lock.unlock();
                    task.action();
                    lock.lock();
                }

                // Then process I/O intensive tasks
                while (!ioIntensiveTasks.empty()) {
                    auto task = ioIntensiveTasks.front();
                    ioIntensiveTasks.pop_front();
                    lock.unlock();
                    task.action();
                    lock.lock();
                }
            }
        }
    }

    void thread_utility::errorHandler() {
        std::cerr << "Error: Invalid scheduling policy selected. Reverting to default FIFO policy." << std::endl;
        // Revert to the default FIFO policy
        fifoPolicy();
    }


    void thread_utility::nativeExecuteTask(std::function<void()> task) {
        std::unique_lock<std::mutex> lock(queueMutex);
        adaptive_queue::Task newTask(task, defaultPriority);
        tasks.push(newTask);
        lock.unlock();
        condition.notify_one();
    }

} // thread_utility