#include "jni_helper.h"

#include <assert.h>

namespace nreal {

JNIHelper *JNIHelper::instance_ = nullptr;

void JNIHelper::Initialize(JavaVM *vm) {
    new JNIHelper(vm);
}

const JNIHelper &JNIHelper::Get() {
    assert(instance_);
    return *instance_;
}

JNIHelper::JNIHelper(JavaVM *vm) {
    vm_ = vm;
    if (!instance_ && vm) {
        instance_ = this;
    }
}

JNIHelper::~JNIHelper() {
    instance_ = nullptr;
}

JNIEnv *JNIHelper::Env() const {
    JNIEnv *env;
    int status = vm_->GetEnv((void **)&env, JNI_VERSION_1_6);
    if (status < 0) {
        status = vm_->AttachCurrentThread(&env, 0);
        if (status < 0) {
            return nullptr;
        }
    }
    return env;
}

void JNIHelper::CallVoidMethod(jobject obj, jmethodID methodId, ...) const {
    auto env = Env();
    va_list args;
    va_start(args, methodId);
    env->CallVoidMethodV(obj, methodId, args);
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
    }
}

}