#include <jni.h>
#include <string>
#include "world_synthesis.hpp"

#define BIT_64 8

using namespace audio_analysis_lib;

struct DataBuffer
{
    double* frame_data;
    double* output_data;
    int frame_len;
};


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_voiceconvertion_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_com_example_voiceconvertion_AudioRecordSample_getTestSize(
        JNIEnv* env,
        jobject in_thiz )
{
    //return sizeof(void*);
    return BIT_64;
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_voiceconvertion_AudioRecordSample_initSynthesis(
        JNIEnv* env,
        jobject /*this*/,
        jint sampling_rate,
        jint frame_rate,
        jfloat frame_period,
        jbyteArray func_pointer,
        jbyteArray data_pointer) {
    DataBuffer *data_p = new DataBuffer();
    data_p->frame_len = sampling_rate / frame_rate;//* (64.0 / 1000.0);
    data_p->frame_data = new double[data_p->frame_len];
    data_p->output_data = new double[data_p->frame_len];
    auto *func_p = new world_synthesis(sampling_rate, frame_period, data_p->frame_len);
    env->SetByteArrayRegion(func_pointer, 0, BIT_64, (jbyte*)&func_p);
    env->SetByteArrayRegion(data_pointer, 0, BIT_64, (jbyte*)&data_p);
}


extern "C" JNIEXPORT void JNICALL
Java_com_example_voiceconvertion_AudioRecordSample_realtimeSynth(
        JNIEnv* env,
        jobject /*this*/,
        jdouble f0_shift,
        jdoubleArray frame,
        jdoubleArray output,
        jbyteArray func_pointer,
        jbyteArray data_pointer) {
    void *func;
    void* buffer;
    DataBuffer* dataBuf;
    env->GetByteArrayRegion(func_pointer, 0, BIT_64, (jbyte*)&func);
    env->GetByteArrayRegion(data_pointer, 0, BIT_64, (jbyte*)&buffer);
    dataBuf = static_cast<DataBuffer*>(buffer);
    //dataBufがnull化する
    jboolean flg = JNI_TRUE;
    dataBuf->frame_data = (double*)env->GetDoubleArrayElements(frame, &flg);
    //env->GetDoubleArrayRegion(frame, 0, dataBuf->frame_len, (jdouble*)&(dataBuf->frame_data));
    static_cast<world_synthesis*>(func)->realtime_synth(f0_shift, dataBuf->frame_data, dataBuf->output_data);
    //env->SetDoubleArrayRegion(output, 0, dataBuf->frame_len, dataBuf->output_data);
    env->SetDoubleArrayRegion(output, 0, dataBuf->frame_len, dataBuf->output_data);
}


extern "C" JNIEXPORT void JNICALL
Java_com_example_voiceconvertion_AudioRecordSample_deleteSynthesis(
        JNIEnv* env,
        jobject /*this*/,
        jbyteArray func_pointer,
        jbyteArray data_pointer) {
    void* func;
    void* buffer;
    env->GetByteArrayRegion(func_pointer, 0, BIT_64, (jbyte*)&func);
    env->GetByteArrayRegion(data_pointer, 0, BIT_64, (jbyte*)&buffer);
    delete static_cast<world_synthesis*>(func);
    delete[] static_cast<DataBuffer*>(buffer)->frame_data;
    delete[] static_cast<DataBuffer*>(buffer)->output_data;
    delete static_cast<DataBuffer*>(buffer);
}