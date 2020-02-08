#include <jni.h>
#include <string>
#include "WlCallJava.h"
#include "WlFFmpeg.h"
extern "C"{
#include <libavformat/avformat.h>
}

JavaVM *javaVm=NULL;
WlCallJava *callJava =NULL;
WlFFmpeg *wlFFmpeg=NULL;
WlPlaystatus *playstatus=NULL;
bool nexit=true;
pthread_t  thread_start;

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm,void * reserved){

    jint result=-1;
    javaVm =vm;
    JNIEnv *env;
    if(vm->GetEnv((void **)&env,JNI_VERSION_1_6)!=JNI_OK){
        return  result;
    }

    return  JNI_VERSION_1_6;

}


extern "C"
JNIEXPORT void JNICALL
Java_com_xurent_myplayer_player_WLPlayer_n_1parpared(JNIEnv *env, jobject thiz, jstring source_) {

    const  char *source=env->GetStringUTFChars(source_,0);
    if(wlFFmpeg==NULL){
        if(callJava==NULL){
            callJava=new WlCallJava(javaVm,env,&thiz);
        }
        callJava->onCallLoad(MAIN_THRBAD, true);
        playstatus=new WlPlaystatus();
        wlFFmpeg=new WlFFmpeg(playstatus,callJava,source);
        wlFFmpeg->parpared();
        LOGD("调用C++解码音视频")
    }else{
        wlFFmpeg->parpared();
    }


}

void *startCallBack(void *data){

    WlFFmpeg *fFmpeg= (WlFFmpeg *)(data);
    fFmpeg->start();
    return 0;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_xurent_myplayer_player_WLPlayer_n_1start(JNIEnv *env, jobject thiz) {

    if(wlFFmpeg!=NULL){
       // wlFFmpeg->start();
       pthread_create(&thread_start,NULL,startCallBack,wlFFmpeg);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_xurent_myplayer_player_WLPlayer_n_1pause(JNIEnv *env, jobject thiz) {

    if(wlFFmpeg!=NULL){
        wlFFmpeg->pause();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xurent_myplayer_player_WLPlayer_n_1resume(JNIEnv *env, jobject thiz) {
    if(wlFFmpeg!=NULL){
        wlFFmpeg->resume();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xurent_myplayer_player_WLPlayer_n_1stop(JNIEnv *env, jobject thiz) {

    if(!nexit){
        return;
    }

    jclass  jlz=env->GetObjectClass(thiz);
    jmethodID  jmid_next=env->GetMethodID(jlz,"onCallNext","()V");


    nexit= false;
    if(wlFFmpeg!=NULL){
        wlFFmpeg->release();

        pthread_join(thread_start,NULL);

        delete(wlFFmpeg);
        wlFFmpeg=NULL;
        if(callJava!=NULL){
            delete(callJava);
            callJava=NULL;
        }
        if(playstatus!=NULL){
            delete(playstatus);
            playstatus=NULL;
        }
    }
    nexit= true;
    env->CallVoidMethod(thiz,jmid_next);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xurent_myplayer_player_WLPlayer_n_1seek(JNIEnv *env, jobject thiz, jint sedc) {

    if(wlFFmpeg!=NULL){
        wlFFmpeg->seek(sedc);
    }

}
