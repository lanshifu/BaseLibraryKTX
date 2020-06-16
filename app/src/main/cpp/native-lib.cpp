#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_lanshifu_baselibraryktx_native_createKey(
        JNIEnv *env,
        jclass clazz) {
    std::string hello = "lizhifm183gameun";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_lanshifu_baselibraryktx_native_native_crash(
        JNIEnv *env,
        jclass clazz) {
    std::string hello = "lizhifm183gameun";
    return env->NewStringUTF(hello.c_str());
}
