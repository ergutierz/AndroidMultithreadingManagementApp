//
// Created by Erik Gutierrez on 12/3/23.
//

#ifndef CITYTOUR_PERFORMANCE_UTILITY_H
#define CITYTOUR_PERFORMANCE_UTILITY_H

#include <vector>

namespace performance {

    struct MonitoringData {
        std::vector<double> cpuUtilizations;
        std::vector<double> memoryUsages;
        std::vector<int> threadUsages;
    };

    class MonitoringListener {
    public:
        virtual void onDataUpdated(const MonitoringData& data) = 0;
    };

    class performance_utility {
        static std::vector<MonitoringListener*> listeners;
    public:
        static double getCPUUtilization();
        static double getMemoryUsage();
        static int getThreadUsage();

        static void startMonitoring();
        static void stopMonitoring();

        static void addListener(MonitoringListener* listener) {
            listeners.push_back(listener);
        }

        static void removeListener(MonitoringListener* listener) {
            listeners.erase(std::remove(listeners.begin(), listeners.end(), listener), listeners.end());
        }

        static void notifyListeners() {
            for (auto* listener : listeners) {
                listener->onDataUpdated(monitoringData);
            }
        }

        static const MonitoringData & getMonitoringData();

    private:
        static MonitoringData monitoringData;
        static void monitorPerformance();
    };

} // performance

#endif //CITYTOUR_PERFORMANCE_UTILITY_H
