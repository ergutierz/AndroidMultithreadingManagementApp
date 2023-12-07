//
// Created by erikg on 12/7/2023.
//

#include <iostream>
#include <deque>
#include <mutex>
#include <chrono>
#include <condition_variable>
#include <functional>
#include <thread>
#include "adaptive_queue.h"

namespace adaptive_queue {

    void AdaptiveQueue::push(const Task& task) {
        auto position = std::find_if(taskDeque.begin(), taskDeque.end(),
                                     [&task](const Task& t) { return t.estimatedDuration > task.estimatedDuration; });
        taskDeque.insert(position, task);
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

    auto AdaptiveQueue::begin() -> decltype(taskDeque.begin()) {
        return taskDeque.begin();
    }

    auto AdaptiveQueue::end() -> decltype(taskDeque.end()) {
        return taskDeque.end();
    }

    void AdaptiveQueue::erase(typename std::deque<Task>::iterator it) {
        taskDeque.erase(it);
    }

    void AdaptiveQueue::push_back(const Task& task) {
        taskDeque.push_back(task);
    }
}
