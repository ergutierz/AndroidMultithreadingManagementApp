#include <jni.h>
#include <android/log.h>
#include "threading/thread_utility.h"

//
// Created by erikg on 11/1/2023.
//
threading::thread_utility globalThreadUtility(10);

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeInitializeThreadManager(
        JNIEnv *env, jobject thiz, jint thread_count) {
    globalThreadUtility.initializeThreadManager(thread_count);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeSetSchedulingPolicy(
        JNIEnv *env, jobject thiz, jint policy) {
    globalThreadUtility.setSchedulingPolicy(policy);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeExecuteTask(JNIEnv *env,
                                                                                 jobject thiz,
                                                                                 jobject runnable) {
    std::function<void()> task = [env, runnable]() {
        jclass runnableClass = env->GetObjectClass(runnable);
        jmethodID runMethod = env->GetMethodID(runnableClass, "run", "()V");
        env->CallVoidMethod(runnable, runMethod);
        env->DeleteLocalRef(runnableClass);
    };
    globalThreadUtility.nativeExecuteTask(task);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeSetThreadPriority(
        JNIEnv *env, jobject thiz, jint priority) {
    globalThreadUtility.setThreadPriority(priority);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeAllocateThread(
        JNIEnv *env, jobject thiz, jobject runnable, jboolean isCpuIntensive) {
    std::function<void()> task = [env, runnable]() {
        jclass runnableClass = env->GetObjectClass(runnable);
        jmethodID runMethod = env->GetMethodID(runnableClass, "run", "()V");
        env->CallVoidMethod(runnable, runMethod);
        env->DeleteLocalRef(runnableClass);
    };
    globalThreadUtility.allocateThread(task, isCpuIntensive);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeShutdown(JNIEnv *env, jobject thiz) {
    // Assuming globalThreadUtility is an instance of thread_utility
    globalThreadUtility.shutdown();
}
