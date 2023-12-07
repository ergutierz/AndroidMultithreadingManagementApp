// adaptive_queue.h

#ifndef CITYTOUR_ADAPTIVE_QUEUE_H
#define CITYTOUR_ADAPTIVE_QUEUE_H

#include <functional>
#include <deque>
#include <chrono>

namespace adaptive_queue {

    struct Task {
        Task() {};

        // Constructor for basic tasks
        explicit Task(std::function<void()> act, int priority = 0)
                : action(std::move(act)), priority(priority), lastExecutionTime(std::chrono::steady_clock::now()) {}

        // Constructor for tasks with estimated duration
        static Task withDuration(std::function<void()> act, int priority, std::chrono::milliseconds dur) {
            Task task(std::move(act), priority);
            task.estimatedDuration = dur;
            return task;
        }

        // Constructor for CPU intensive tasks
        static Task cpuIntensive(std::function<void()> act, int prio = 0) {
            Task task(std::move(act), prio);
            task.isCpuIntensive = true;
            return task;
        }

        std::function<void()> action;
        int priority;
        bool isCpuIntensive;
        std::chrono::steady_clock::time_point lastExecutionTime;
        std::chrono::milliseconds estimatedDuration;
        bool isForeground;
    };

    class AdaptiveQueue {
    private:
        std::deque<Task> taskDeque;

    public:
        void push(const Task& task);
        Task pop();
        bool empty() const;
        size_t size() const;

        // Adding new methods
        auto begin() -> decltype(taskDeque.begin());
        auto end() -> decltype(taskDeque.end());
        void erase(typename std::deque<Task>::iterator it);
        void push_back(const Task& task);
    };
}

#endif // CITYTOUR_ADAPTIVE_QUEUE_H
