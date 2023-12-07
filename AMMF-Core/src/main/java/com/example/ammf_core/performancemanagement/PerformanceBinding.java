package com.example.ammf_core.performancemanagement;

import java.util.List;

public class PerformanceBinding {

    private final PerformanceListener listener;

    public PerformanceBinding(PerformanceListener listener) {
        this.listener = listener;
    }

    public void register() {
        // Register listener logic here
        registerPerformanceListener();
    }

    public void unregister() {
        // Unregister listener logic here
        unregisterPerformanceListener();
    }

    public void onDataUpdated(List<Double> cpuUtilizations, List<Double> memoryUsages, List<Integer> threadUsages) {
        // Notify the listener
        if (listener != null) {
            listener.onUpdates(cpuUtilizations, memoryUsages, threadUsages);
        }
    }

    private native void registerPerformanceListener();
    private native void unregisterPerformanceListener();
}

