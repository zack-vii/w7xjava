/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class local_LocalDataProvider */

#ifndef _Included_local_LocalDataProvider
#define _Included_local_LocalDataProvider
#ifdef __cplusplus
extern "C" {
#endif
#undef local_LocalDataProvider_RESAMPLE_TRESHOLD
#define local_LocalDataProvider_RESAMPLE_TRESHOLD 1000000000i64
#undef local_LocalDataProvider_MAX_PIXELS
#define local_LocalDataProvider_MAX_PIXELS 2000L
/*
 * Class:     local_LocalDataProvider
 * Method:    nativeIsSegmentedNode
 * Signature: (Ljava/lang/String;)Z
 */
  JNIEXPORT jboolean JNICALL Java_local_LocalDataProvider_nativeIsSegmentedNode(JNIEnv *, jclass, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetSegment
 * Signature: (Ljava/lang/String;II)[B
 */
  JNIEXPORT jbyteArray JNICALL Java_local_LocalDataProvider_nativeGetSegment(JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetAllFrames
 * Signature: (Ljava/lang/String;II)[B
 */
  JNIEXPORT jbyteArray JNICALL Java_local_LocalDataProvider_nativeGetAllFrames(JNIEnv *, jclass, jstring, jint, jint);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetInfo
 * Signature: (Ljava/lang/String;Z)[I
 */
  JNIEXPORT jintArray JNICALL Java_local_LocalDataProvider_nativeGetInfo(JNIEnv *, jclass, jstring, jboolean);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetSegmentTimes
 * Signature: (Ljava/lang/String;Ljava/lang/String;FF)[F
 */
  JNIEXPORT jfloatArray JNICALL Java_local_LocalDataProvider_nativeGetSegmentTimes(JNIEnv *, jclass, jstring, jstring, jfloat, jfloat);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetAllTimes
 * Signature: (Ljava/lang/String;Ljava/lang/String;)[F
 */
  JNIEXPORT jfloatArray JNICALL Java_local_LocalDataProvider_nativeGetAllTimes(JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetSegmentIdxs
 * Signature: (Ljava/lang/String;FF)[I
 */
  JNIEXPORT jintArray JNICALL Java_local_LocalDataProvider_nativeGetSegmentIdxs(JNIEnv *, jclass, jstring, jfloat, jfloat);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeSetEnvironmentSpecific
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDataProvider_nativeSetEnvironmentSpecific(JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeUpdate
 * Signature: (Ljava/lang/String;J)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDataProvider_nativeUpdate(JNIEnv *, jobject, jstring, jlong);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetString
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
  JNIEXPORT jstring JNICALL Java_local_LocalDataProvider_nativeGetString(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetFloat
 * Signature: (Ljava/lang/String;)F
 */
  JNIEXPORT jfloat JNICALL Java_local_LocalDataProvider_nativeGetFloat(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetLongArray
 * Signature: (Ljava/lang/String;)[J
 */
  JNIEXPORT jlongArray JNICALL Java_local_LocalDataProvider_nativeGetLongArray(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetFloatArray
 * Signature: (Ljava/lang/String;)[F
 */
  JNIEXPORT jfloatArray JNICALL Java_local_LocalDataProvider_nativeGetFloatArray(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetDoubleArray
 * Signature: (Ljava/lang/String;)[D
 */
  JNIEXPORT jdoubleArray JNICALL Java_local_LocalDataProvider_nativeGetDoubleArray(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetIntArray
 * Signature: (Ljava/lang/String;)[I
 */
  JNIEXPORT jintArray JNICALL Java_local_LocalDataProvider_nativeGetIntArray(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeGetByteArray
 * Signature: (Ljava/lang/String;)[B
 */
  JNIEXPORT jbyteArray JNICALL Java_local_LocalDataProvider_nativeGetByteArray(JNIEnv *, jobject, jstring);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeErrorString
 * Signature: ()Ljava/lang/String;
 */
  JNIEXPORT jstring JNICALL Java_local_LocalDataProvider_nativeErrorString(JNIEnv *, jobject);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeRegisterEvent
 * Signature: (Ljava/lang/String;I)I
 */
  JNIEXPORT jint JNICALL Java_local_LocalDataProvider_nativeRegisterEvent(JNIEnv *, jobject, jstring, jint);

/*
 * Class:     local_LocalDataProvider
 * Method:    nativeUnregisterEvent
 * Signature: (I)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDataProvider_nativeUnregisterEvent(JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
