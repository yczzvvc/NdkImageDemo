//
// Created by Yuan Chao on 3/5/21.
//
#include <jni.h>
#include <string>
#include <media/NdkImage.h>
#include <media/NdkImageReader.h>
#include <android/hardware_buffer_jni.h>
#include <android/native_window_jni.h>
#include "jni_helper.h"
#include<android/log.h>
#define LOG_TAG "nativeimage"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


void AImageReaderImageCallback(void* context, AImageReader* reader) {
    AImage *image = nullptr;
    int fd;//-1也许才能用
    AImageReader_acquireNextImageAsync(reader, &image, &fd);

    LOGD("AImageReaderImageCallback");

    AHardwareBuffer *aHardwareBuffer = nullptr;
    AImage_getHardwareBuffer(image, &aHardwareBuffer);
    jobject jHardwareBuffer = AHardwareBuffer_toHardwareBuffer(nreal::JNIHelper::Get().Env(), aHardwareBuffer);

    const char* class_from_java = "com/me/ndkimage/demo/ImageReaderBridge";
    const char* method_name_from_java = "onSurfaceUpdate";
    jclass clazz = (nreal::JNIHelper::Get().Env())->FindClass(class_from_java);
    jmethodID methodID = (nreal::JNIHelper::Get().Env())->GetStaticMethodID(clazz, method_name_from_java,"(Landroid/hardware/HardwareBuffer;)V");
    (nreal::JNIHelper::Get().Env())->CallStaticVoidMethod(clazz, methodID, jHardwareBuffer);
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    LOGI("JNI_OnLoad");
    nreal::JNIHelper::Initialize(vm);
    return JNI_VERSION_1_6;
}


#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_me_ndkimage_demo_ImageReaderBridge_nativeInitImageReader(
        JNIEnv *env,
        jclass thiz
        ) {

}

JNIEXPORT jobject JNICALL Java_com_me_ndkimage_demo_ImageReaderBridge_nativeGetHardwareBuffer(
        JNIEnv *env,
        jclass thiz) {

    AImageReader* aImageReader = nullptr;
    AImageReader_newWithUsage(400, 600, 0x23, 3, 2, &aImageReader);

    AImageReader_ImageListener listener;
    listener.onImageAvailable = AImageReaderImageCallback;
    AImageReader_setImageListener(aImageReader, &listener);

    ANativeWindow* aNativeWindow = nullptr;
    AImageReader_getWindow(aImageReader, &aNativeWindow);

    jobject surface = ANativeWindow_toSurface(env, aNativeWindow);
    return surface;
}

JNIEXPORT jobject JNICALL Java_com_me_ndkimage_demo_ImageReaderBridge_nativeGetSurfaceFromImageReader(
        JNIEnv *env,
        jclass thiz) {

    LOGI("nativeGetSurfaceFromImageReader");

    AImageReader* aImageReader = nullptr;
    AImageReader_newWithUsage(1080, 1080, 1, 3, 2, &aImageReader);

    AImageReader_ImageListener listener;
    listener.onImageAvailable = AImageReaderImageCallback;
    AImageReader_setImageListener(aImageReader, &listener);

    ANativeWindow* aNativeWindow = nullptr;
    AImageReader_getWindow(aImageReader, &aNativeWindow);

    jobject surface = ANativeWindow_toSurface(env, aNativeWindow);
    return surface;
}

}


