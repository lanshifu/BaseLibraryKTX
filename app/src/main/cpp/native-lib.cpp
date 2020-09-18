#include <jni.h>
#include <string>


void crash(){
    int i = 10;
    int j = 0;
    int k = i / j;
}

extern "C" JNIEXPORT void JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_native_1crash(JNIEnv *env,jobject clazz) {
    std::string hello = "hello";
    crash();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_createKey(
        JNIEnv *env,
        jobject clazz) {
    std::string hello = "lizhifm183gameun";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_lanshifu_baselibraryktx_native_NativeClass_crash(JNIEnv *env, jobject thiz) {


}