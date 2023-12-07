//
// Created by erikg on 12/7/2023.
//

#include <iostream>
#include <deque>
#include <mutex>
#include <condition_variable>
#include <functional>
#include <thread>
#include "adaptive_queue.h"

namespace adaptive_queue {

    Task::Task(std::function<void()> act, int priority) {
        action = act;
        this->priority = priority;
        lastExecutionTime = std::chrono::steady_clock::now();
    }

    void AdaptiveQueue::push(const Task& task) {
        taskDeque.push_back(task);
    }

    Task AdaptiveQueue::pop() {
        if (taskDeque.empty()) {
            throw std::runtime_error("Attempt to pop from an empty queue");
        }
        Task task = taskDeque.front();
        taskDeque.pop_front();
        return task;
    }

    bool AdaptiveQueue::empty() const {
        return taskDeque.empty();
    }

    size_t AdaptiveQueue::size() const {
        return taskDeque.size();
    }
}
