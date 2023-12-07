//
// Created by Erik Gutierrez on 12/7/23.
//

#include "time_slice.h"

namespace time_slice {

    TimeSlicedPolicy::TimeSlicedPolicy(std::chrono::milliseconds ts)
            : timeSlice(ts), stopFlag(false), worker(&TimeSlicedPolicy::workerThread, this) {}

    void TimeSlicedPolicy::addTask(const std::function<void()>& task) {
        std::unique_lock<std::mutex> lock(queueMutex);
        taskQueue.push_back(task);
        cv.notify_one();
    }

    void TimeSlicedPolicy::start() {
        stopFlag = false;
        if (!worker.joinable()) {
            worker = std::thread(&TimeSlicedPolicy::workerThread, this);
        }
    }

    void TimeSlicedPolicy::stop() {
        stopFlag = true;
        cv.notify_all();
        if (worker.joinable()) {
            worker.join();
        }
    }

    void TimeSlicedPolicy::workerThread() {
        while (true) {
            std::function<void()> task;
            {
                std::unique_lock<std::mutex> lock(queueMutex);
                cv.wait(lock, [this]{ return stopFlag || !taskQueue.empty(); });
                if (stopFlag && taskQueue.empty())
                    return;

                task = taskQueue.front();
                taskQueue.pop_front();
            }

            auto start = std::chrono::steady_clock::now();
            task();  // Execute the task
            auto end = std::chrono::steady_clock::now();

            if (std::chrono::duration_cast<std::chrono::milliseconds>(end - start) > timeSlice) {
                // If task exceeded time slice, push it back to the queue
                std::unique_lock<std::mutex> lock(queueMutex);
                taskQueue.push_front(task);
            }
        }
    }

} // time_slice