#ifndef NREAL_HELPER_JNI_HELPER_H
#define NREAL_HELPER_JNI_HELPER_H

#include <jni.h>

namespace nreal {

class JNIHelper {
public:
    static void Initialize(JavaVM *vm);
    static const JNIHelper &Get();

    JNIEnv *Env() const;
    void CallVoidMethod(jobject obj, jmethodID methodId, ...) const;

private:
    JNIHelper(JavaVM *vm);
    ~JNIHelper();

    JavaVM *vm_;

    static JNIHelper *instance_;
};

}

#endif //NREAL_HELPER_JNI_HELPER_H
