package com.example.ammf_core.performance;

import java.util.List;

public class ThreadData {

    private final List<Double> cpuUtilizations;
    private final List<Double> memoryUsages;
    private final List<Integer> threadUsages;

    public ThreadData(List<Double> cpuUtilizations, List<Double> memoryUsages, List<Integer> threadUsages) {
        this.cpuUtilizations = cpuUtilizations;
        this.memoryUsages = memoryUsages;
        this.threadUsages = threadUsages;
    }

    public List<Double> getCpuUtilizations() {
        return cpuUtilizations;
    }

    public List<Double> getMemoryUsages() {
        return memoryUsages;
    }

    public List<Integer> getThreadUsages() {
        return threadUsages;
    }

    @Override
    public String toString() {
        return "MonitoringData{" +
                "cpuUtilizations=" + cpuUtilizations +
                ", memoryUsages=" + memoryUsages +
                ", threadUsages=" + threadUsages +
                '}';
    }
}

