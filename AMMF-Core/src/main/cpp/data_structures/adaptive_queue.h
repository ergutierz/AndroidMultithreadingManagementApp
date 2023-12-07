//
// Created by erikg on 12/7/2023.
//

#ifndef CITYTOUR_ADAPTIVE_QUEUE_H
#define CITYTOUR_ADAPTIVE_QUEUE_H

#include <functional>

namespace adaptive_queue {
    struct Task {
        std::function<void()> action;
        int priority;
        std::chrono::steady_clock::time_point lastExecutionTime;
        Task(std::function<void()> act, int priority = 0);
    };

    class AdaptiveQueue {
    private:
        std::deque<Task> taskDeque;

    public:
        void push(const Task& task);
        Task pop();
        bool empty() const;
        size_t size() const;
    };
}

#endif //CITYTOUR_ADAPTIVE_QUEUE_H
