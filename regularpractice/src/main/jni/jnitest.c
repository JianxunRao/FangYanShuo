//
// Created by Administrator on 2016/2/21.
//
#include "com_trojx_regularpractice_NdkJniUtils.h"
JNIEXPORT jstring JNICALL Java_com_trojx_regularpractice_NdkJniUtils_getCLanguageString
        (JNIEnv *env, jobject obj){
    return (*env)->NewStringUTF(env,"This just a test for Android Studio NDK JNI developer!");
}

