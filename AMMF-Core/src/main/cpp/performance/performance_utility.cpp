//
// Created by Erik Gutierrez on 12/3/23.
//
#include <fstream>
#include <sstream>
#include <vector>
#include <string>
#include <numeric>
#include <thread>
#include <atomic>
#include <chrono>
#include <iostream>
#include <unistd.h>
#include <android/log.h>
#include <stdio.h>
#include "performance_utility.h"

namespace performance {
    std::vector<MonitoringListener*> performance_utility::listeners;
    MonitoringData performance_utility::monitoringData;
    std::thread monitoringThread;
    std::atomic<bool> isMonitoring(false);

    // [/proc/stat] is only accessible by system apps, works on Android <= 6.0
    double performance_utility::getCPUUtilization() {
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "getCPUUtilization() called");
        std::ifstream procStatFile("/proc/stat");
        std::string line;

        if (!procStatFile.is_open()) {
            __android_log_print(ANDROID_LOG_ERROR, "performance_utility", "Failed to open /proc/stat");
            return 0.0;
        }

        if (std::getline(procStatFile, line)) {
            // Process the first line to read the overall CPU statistics
            std::istringstream iss(line);
            std::vector<std::string> results((std::istream_iterator<std::string>(iss)), std::istream_iterator<std::string>());

            // The values are in the format: cpu user nice system idle iowait irq softirq steal guest guest_nice
            // We need to calculate (user + nice + system + irq + softirq + steal) / total
            if (results.size() >= 8) {
                long userTime = std::stol(results[1]);
                long niceTime = std::stol(results[2]);
                long systemTime = std::stol(results[3]);
                long idleTime = std::stol(results[4]);
                long ioWait = std::stol(results[5]);
                long irq = std::stol(results[6]);
                long softIrq = std::stol(results[7]);
                long steal = std::stol(results[8]);

                long totalTime = userTime + niceTime + systemTime + idleTime + ioWait + irq + softIrq + steal;
                long workingTime = totalTime - idleTime - ioWait;

                return totalTime > 0 ? (double(workingTime) / totalTime) * 100.0 : 0.0;
            }
        }
        __android_log_print(ANDROID_LOG_WARN, "performance_utility", "No data read from /proc/stat");
        return 0.0;
    }

    double performance_utility::getMemoryUsage() {
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "getMemoryUsage() called");
        std::ifstream meminfoFile("/proc/meminfo");
        std::string line;
        long totalMemory = 0;
        long freeMemory = 0;

        if (!meminfoFile.is_open()) {
            __android_log_print(ANDROID_LOG_ERROR, "performance_utility", "Failed to open /proc/meminfo");
            return 0.0;
        }

        while (std::getline(meminfoFile, line)) {
            std::istringstream iss(line);
            std::string key;
            long value;
            std::string kb;

            iss >> key >> value >> kb;

            if (key == "MemTotal:") {
                totalMemory = value;
            } else if (key == "MemFree:") {
                freeMemory = value;
            }

            if (totalMemory != 0 && freeMemory != 0) {
                break;
            }
        }

        if (totalMemory == 0) {
            return 0.0; // Unable to determine total memory
        }

        return 100.0 * (1 - (double(freeMemory) / totalMemory)); // Return memory usage percentage
    }


    int performance_utility::getThreadUsage() {
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "getThreadUsage() called");
        int pid = getpid();  // Get the process ID of the current process
        std::string statusFilePath = "/proc/" + std::to_string(pid) + "/status";
        std::ifstream statusFile(statusFilePath);
        std::string line;
        int threadCount = 0;

        if (!statusFile.is_open()) {
            __android_log_print(ANDROID_LOG_ERROR, "performance_utility", "Failed to open thread status file");
            return 0;
        }


        while (std::getline(statusFile, line)) {
            std::istringstream iss(line);
            std::string key;
            iss >> key;
            if (key == "Threads:") {
                iss >> threadCount;
                break;
            }
        }

        return threadCount;
    }


    void performance_utility::monitorPerformance() {
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "monitorPerformance() called");
        while (isMonitoring.load()) {
            // Collect performance data
            double cpuUtilization = performance_utility::getCPUUtilization();
            double memoryUsage = performance_utility::getMemoryUsage();
            int threadUsage = performance_utility::getThreadUsage();

            // Store the collected performance data
            monitoringData.cpuUtilizations.push_back(cpuUtilization);
            monitoringData.memoryUsages.push_back(memoryUsage);
            monitoringData.threadUsages.push_back(threadUsage);

            // Log the size of the data vectors
            __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "Data Size - CPU: %d, Memory: %d, Threads: %d",
                                monitoringData.cpuUtilizations.size(), monitoringData.memoryUsages.size(), monitoringData.threadUsages.size());

            // Process or store the collected performance data
            std::cout << "CPU Utilization: " << cpuUtilization << "%" << std::endl;
            std::cout << "Memory Usage: " << memoryUsage << " MB" << std::endl;
            std::cout << "Thread Usage: " << threadUsage << std::endl;
            __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "CPU Utilization: %f%%, Memory Usage: %f MB, Thread Usage: %d", cpuUtilization, memoryUsage, threadUsage);

            notifyListeners();
            // Add appropriate delay between performance data collection cycles
            std::this_thread::sleep_for(std::chrono::seconds(1)); // Collect data every second
        }
    }

    void performance_utility::stopMonitoring() {
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "stopMonitoring() called");
        if (isMonitoring.load()) {
            isMonitoring.store(false);
            if (monitoringThread.joinable()) {
                monitoringThread.join(); // Ensure the thread completes execution
                //clear the data vectors
                monitoringData.cpuUtilizations.clear();
                monitoringData.memoryUsages.clear();
                monitoringData.threadUsages.clear();
            }
        }
    }

    void performance_utility::startMonitoring() {
        __android_log_print(ANDROID_LOG_INFO, "performance_utility", "startMonitoring() called");
        if (!isMonitoring.load()) {
            __android_log_print(ANDROID_LOG_INFO, "performance_utility", "startMonitoring() called entered if statement");
            isMonitoring.store(true);
            monitoringThread = std::thread(monitorPerformance);
        }
    }

    const MonitoringData & performance_utility::getMonitoringData() {
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "getMonitoringData() called");

        // Log the size of the data vectors
        __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "Data Size - CPU: %d, Memory: %d, Threads: %d",
                            monitoringData.cpuUtilizations.size(), monitoringData.memoryUsages.size(), monitoringData.threadUsages.size());

        // Example to log the individual elements (for demonstration, might be too verbose for real use)
        for(size_t i = 0; i < monitoringData.cpuUtilizations.size(); ++i) {
            __android_log_print(ANDROID_LOG_DEBUG, "performance_utility", "CPU[%zu]: %f, Memory[%zu]: %f, Threads[%zu]: %d",
                                i, monitoringData.cpuUtilizations[i], i, monitoringData.memoryUsages[i], i, monitoringData.threadUsages[i]);
        }

        return monitoringData;
    }

} // performance