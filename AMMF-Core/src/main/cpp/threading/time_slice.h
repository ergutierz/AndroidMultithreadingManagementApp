//
// Created by Erik Gutierrez on 12/7/23.
//

#ifndef CITYTOUR_TIME_SLICE_H
#define CITYTOUR_TIME_SLICE_H

#include <chrono>
#include <functional>
#include <mutex>
#include <condition_variable>
#include <deque>
#include <thread>

namespace time_slice {

    class TimeSlicedPolicy {
    public:
        explicit TimeSlicedPolicy(std::chrono::milliseconds ts);
        void addTask(const std::function<void()>& task);
        void start();
        void stop();

    private:
        std::deque<std::function<void()>> taskQueue;
        std::mutex queueMutex;
        std::condition_variable cv;
        bool stopFlag;
        std::chrono::milliseconds timeSlice;
        std::thread worker;

        void workerThread();
    };

} // time_slice

#endif //CITYTOUR_TIME_SLICE_H
