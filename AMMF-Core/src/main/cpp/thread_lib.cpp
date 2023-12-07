#include <jni.h>
#include <android/log.h>
#include "/threading/thread_utility.h"

//
// Created by erikg on 11/1/2023.
//

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeInitializeThreadManager(
        JNIEnv *env, jobject thiz, jint thread_count) {
    threading::thread_utility::initializeThreadManager(thread_count);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ammf_1core_threadmanagement_AMMFThreadManager_nativeSetSchedulingPolicy(
        JNIEnv *env, jobject thiz, jint policy) {
    threading::thread_utility::setSchedulingPolicy(policy);
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
    threading::thread_utility::nativeExecuteTask(task);
}