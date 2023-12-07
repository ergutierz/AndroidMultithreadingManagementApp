#include <jni.h>
#include <map>
#include <android/log.h>
#include "performance/performance_utility.h"
//
// Created by Erik Gutierrez on 12/3/23.
//

jobject convertToJavaMonitoringData(JNIEnv *pEnv, const performance::MonitoringData &data);

JavaVM *g_JavaVM = nullptr;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_JavaVM = vm;
    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_ammf_1core_performance_PerformanceManager_getCPUUtilization(JNIEnv *env,
                                                                             jobject thiz) {
    return performance::performance_utility::getCPUUtilization();
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_example_ammf_1core_performance_PerformanceManager_getMemoryUsage(JNIEnv *env,
                                                                          jobject thiz) {
    return performance::performance_utility::getMemoryUsage();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_ammf_1core_performance_PerformanceManager_getThreadUsage(JNIEnv *env,
                                                                          jobject thiz) {
    return performance::performance_utility::getThreadUsage();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_performance_PerformanceManager_startMonitoring(JNIEnv *env,
                                                                           jobject thiz) {
    performance::performance_utility::startMonitoring();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_performance_PerformanceManager_stopMonitoring(JNIEnv *env,
                                                                          jobject thiz) {
    performance::performance_utility::stopMonitoring();
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_ammf_1core_performance_PerformanceManager_getMonitoredData(JNIEnv *env,
                                                                            jobject thiz) {
    // Retrieve the MonitoringData from your C++ code
    const performance::MonitoringData &data = performance::performance_utility::getMonitoringData();

    // Find and construct an ArrayList
    jclass arrayListClass = env->FindClass("java/util/ArrayList");
    jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    jmethodID arrayListAdd = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

    // Create ArrayLists for cpuUtilizations, memoryUsages, and threadUsages
    jobject cpuUtilizationsList = env->NewObject(arrayListClass, arrayListConstructor);
    jobject memoryUsagesList = env->NewObject(arrayListClass, arrayListConstructor);
    jobject threadUsagesList = env->NewObject(arrayListClass, arrayListConstructor);

    // Fill the lists with data
    jclass doubleClass = env->FindClass("java/lang/Double");
    jclass integerClass = env->FindClass("java/lang/Integer");

    // Populate the CPU utilization list
    for (double value: data.cpuUtilizations) {
        jobject doubleObject = env->NewObject(doubleClass,
                                              env->GetMethodID(doubleClass, "<init>", "(D)V"),
                                              value);
        env->CallBooleanMethod(cpuUtilizationsList, arrayListAdd, doubleObject);
        env->DeleteLocalRef(doubleObject);
    }

    // Populate the memory usage list
    for (double value: data.memoryUsages) {
        jobject doubleObject = env->NewObject(doubleClass,
                                              env->GetMethodID(doubleClass, "<init>", "(D)V"),
                                              value);
        env->CallBooleanMethod(memoryUsagesList, arrayListAdd, doubleObject);
        env->DeleteLocalRef(doubleObject);
    }

    // Populate the thread usage list
    for (int value: data.threadUsages) {
        jobject integerObject = env->NewObject(integerClass,
                                               env->GetMethodID(integerClass, "<init>", "(I)V"),
                                               value);
        env->CallBooleanMethod(threadUsagesList, arrayListAdd, integerObject);
        env->DeleteLocalRef(integerObject);
    }

    // Create MonitoringData object and set its fields
    jclass monitoringDataClass = env->FindClass("com/example/ammf_core/performancemanagement/MonitoringData");
    jmethodID monitoringDataConstructor = env->GetMethodID(monitoringDataClass, "<init>",
                                                           "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V");
    jobject monitoringDataObject = env->NewObject(monitoringDataClass, monitoringDataConstructor,
                                                  cpuUtilizationsList, memoryUsagesList,
                                                  threadUsagesList);

    // Cleanup local references
    env->DeleteLocalRef(cpuUtilizationsList);
    env->DeleteLocalRef(memoryUsagesList);
    env->DeleteLocalRef(threadUsagesList);

    return monitoringDataObject;
}

class JavaMonitoringListener : public performance::MonitoringListener {
public:
    JavaMonitoringListener(JNIEnv *env, jobject javaListener) {
        this->env = env;
        this->javaListener = env->NewGlobalRef(javaListener);  // Store a global reference
        this->javaListenerClass = static_cast<jclass>(env->GetObjectClass(javaListener));
        // Update method signature to match the new onDataUpdated method in PerformanceBinding
        this->onDataUpdatedMethod = env->GetMethodID(javaListenerClass, "onDataUpdated",
                                                     "(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V");
    }

    ~JavaMonitoringListener() {
        env->DeleteGlobalRef(javaListener);
        env->DeleteGlobalRef(javaListenerClass);
    }

    void onDataUpdated(const performance::MonitoringData &data) override {
        bool shouldDetach = false;
        if (g_JavaVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
            g_JavaVM->AttachCurrentThread(&env, NULL);
            shouldDetach = true;
        }

        // Convert MonitoringData to Java Lists and call the onDataUpdated method
        jobject jCpuUtilizationsList, jMemoryUsagesList, jThreadUsagesList;
        convertToJavaMonitoringData(env, data, jCpuUtilizationsList, jMemoryUsagesList,
                                    jThreadUsagesList);
        env->CallVoidMethod(javaListener, onDataUpdatedMethod, jCpuUtilizationsList,
                            jMemoryUsagesList, jThreadUsagesList);

        if (shouldDetach) {
            g_JavaVM->DetachCurrentThread();
        }
    }

    void convertToJavaMonitoringData(JNIEnv *pEnv,
                                     const performance::MonitoringData &data,
                                     jobject &jCpuUtilizationsList,
                                     jobject &jMemoryUsagesList,
                                     jobject &jThreadUsagesList
    ) {
        // Find Java classes and get constructor method IDs
        jclass arrayListClass = pEnv->FindClass("java/util/ArrayList");
        jmethodID arrayListConstructor = pEnv->GetMethodID(arrayListClass, "<init>", "()V");
        jmethodID arrayListAdd = pEnv->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");

        jclass doubleClass = pEnv->FindClass("java/lang/Double");
        jmethodID doubleConstructor = pEnv->GetMethodID(doubleClass, "<init>", "(D)V");

        jclass integerClass = pEnv->FindClass("java/lang/Integer");
        jmethodID integerConstructor = pEnv->GetMethodID(integerClass, "<init>", "(I)V");

        // Create ArrayLists
        jobject cpuUtilizationsList = pEnv->NewObject(arrayListClass, arrayListConstructor);
        jobject memoryUsagesList = pEnv->NewObject(arrayListClass, arrayListConstructor);
        jobject threadUsagesList = pEnv->NewObject(arrayListClass, arrayListConstructor);

        // Populate the ArrayLists
        for (double value: data.cpuUtilizations) {
            jobject doubleObject = pEnv->NewObject(doubleClass, doubleConstructor, value);
            pEnv->CallBooleanMethod(cpuUtilizationsList, arrayListAdd, doubleObject);
            pEnv->DeleteLocalRef(doubleObject);
        }

        for (double value: data.memoryUsages) {
            jobject doubleObject = pEnv->NewObject(doubleClass, doubleConstructor, value);
            pEnv->CallBooleanMethod(memoryUsagesList, arrayListAdd, doubleObject);
            pEnv->DeleteLocalRef(doubleObject);
        }

        for (int value: data.threadUsages) {
            jobject integerObject = pEnv->NewObject(integerClass, integerConstructor, value);
            pEnv->CallBooleanMethod(threadUsagesList, arrayListAdd, integerObject);
            pEnv->DeleteLocalRef(integerObject);
        }

        jCpuUtilizationsList = cpuUtilizationsList;
        jMemoryUsagesList = memoryUsagesList;
        jThreadUsagesList = threadUsagesList;

    }

private:
    JNIEnv *env;
    jobject javaListener;
    jclass javaListenerClass;
    jmethodID onDataUpdatedMethod;
};

std::map<jobject, JavaMonitoringListener *> g_listenersMap;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_performance_PerformanceBinding_registerPerformanceListener(JNIEnv *env,
                                                                                       jobject thiz) {
    if (g_listenersMap.find(thiz) == g_listenersMap.end()) {
        auto *javaListener = new JavaMonitoringListener(env, thiz);

        // Get the class of the 'thiz' object
        jclass clazz = env->GetObjectClass(thiz);

        // Get the class's name
        jmethodID mid = env->GetMethodID(clazz, "getClass", "()Ljava/lang/Class;");
        jobject clsObj = env->CallObjectMethod(thiz, mid);
        clazz = env->GetObjectClass(clsObj);
        mid = env->GetMethodID(clazz, "getName", "()Ljava/lang/String;");
        jstring strObj = (jstring) env->CallObjectMethod(clsObj, mid);

        // Convert the class name to a C-string
        const char *str = env->GetStringUTFChars(strObj, NULL);

        // Log the class name
        __android_log_print(ANDROID_LOG_INFO, "PerformanceManager", "Listener class: %s", str);

        // Release the C-string
        env->ReleaseStringUTFChars(strObj, str);

        g_listenersMap[env->NewGlobalRef(thiz)] = javaListener; // Store a global reference
        performance::performance_utility::addListener(javaListener);
        __android_log_print(ANDROID_LOG_INFO, "PerformanceManager", "Listener registered");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_performance_PerformanceBinding_unregisterPerformanceListener(
        JNIEnv *env, jobject thiz) {
    jobject globalThiz = env->NewGlobalRef(thiz);
    if (g_listenersMap.find(globalThiz) != g_listenersMap.end()) {
        auto *javaListener = g_listenersMap[globalThiz];
        performance::performance_utility::removeListener(javaListener);
        delete javaListener;  // Free the memory
        g_listenersMap.erase(globalThiz);
        env->DeleteGlobalRef(globalThiz); // Release the global reference
        __android_log_print(ANDROID_LOG_INFO, "PerformanceManager", "Listener unregistered");
    }
}