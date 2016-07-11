/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class local_LocalDatabase */

#ifndef _Included_LocalDatabase
#define _Included_LocalDatabase
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     local_LocalDatabase
 * Method:    addDevice
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Lmds/NidData;
 */
  JNIEXPORT jobject JNICALL Java_local_LocalDatabase_addDevice(JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     local_LocalDatabase
 * Method:    addNode
 * Signature: (Ljava/lang/String;I)Lmds/NidData;
 */
  JNIEXPORT jobject JNICALL Java_local_LocalDatabase_addNode(JNIEnv *, jobject, jstring, jint);

/*
 * Class:     local_LocalDatabase
 * Method:    close
 * Signature: ()V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_close(JNIEnv *, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    executeDelete
 * Signature: ()V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_executeDelete(JNIEnv *, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    getData
 * Signature: (Lmds/NidData;)Lmds/Data;
 */
  JNIEXPORT jobject JNICALL Java_local_LocalDatabase_getData(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    getDefault
 * Signature: ()Lmds/NidData;
 */
  JNIEXPORT jobject JNICALL Java_local_LocalDatabase_getDefault(JNIEnv *, jobject);

 /*
  * Class:     local_LocalDatabase
  * Method:    getFlags
  * Signature: (Lmds/NidData;)I;
  */
  JNIEXPORT jint JNICALL Java_local_LocalDatabase_getFlags(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    getInfo
 * Signature: (Lmds/NidData;)Lmds/NodeInfo;
 */
  JNIEXPORT jobject JNICALL Java_local_LocalDatabase_getInfo(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    getMembers
 * Signature: (Lmds/NidData;)[Lmds/NidData;
 */
  JNIEXPORT jobjectArray JNICALL Java_local_LocalDatabase_getMembers(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    getSons
 * Signature: (Lmds/Data/NidData;)[Lmds/Data/NidData;
 */
  JNIEXPORT jobjectArray JNICALL Java_local_LocalDatabase_getSons(JNIEnv *, jobject, jobject);

  /*
  * Class:     local_LocalDatabase
  * Method:    getTags
  * Signature: (Lmds/Data/NidData;)[Ljava/lang/String;
  */
  JNIEXPORT jobjectArray JNICALL Java_local_LocalDatabase_getTags(JNIEnv *, jobject, jobject);

  /*
 * Class:     local_LocalDatabase
 * Method:    isOn
 * Signature: (Lmds/Data/NidData;)Z
 */
  JNIEXPORT jboolean JNICALL Java_local_LocalDatabase_isOn(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    open
 * Signature: ()V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_open(JNIEnv *, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    putData
 * Signature: (Lmds/Data/NidData;Lmds/Data;)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_putData(JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    quit
 * Signature: ()V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_quit(JNIEnv *, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    renameNode
 * Signature: (Lmds/Data/NidData;Ljava/lang/String;)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_renameNode(JNIEnv *, jobject, jobject, jstring);

/*
 * Class:     local_LocalDatabase
 * Method:    resolve
 * Signature: (Lmds/Data/PathData;)Lmds/NidData;
 */
  JNIEXPORT jobject JNICALL Java_local_LocalDatabase_resolve(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    setDefault
 * Signature: (Lmds/Data/NidData;)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_setDefault(JNIEnv *, jobject, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    setOn
 * Signature: (Lmds/Data/NidData;Z)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_setOn(JNIEnv *, jobject, jobject, jboolean);

/*
 * Class:     local_LocalDatabase
 * Method:    setTags
 * Signature: (Lmds/Data/NidData;[Ljava/lang/String;)V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_setTags(JNIEnv *, jobject, jobject, jobjectArray);

/*
* Class:     local_LocalDatabase
* Method:    setFlags
* Signature: (Lmds/Data/NidData;I;)
*/
JNIEXPORT void JNICALL Java_local_LocalDatabase_setFlags(JNIEnv *, jobject, jobject, jint);
/*
* Class:     local_LocalDatabase
* Method:    clearFlags
* Signature: (Lmds/Data/NidData;I;)
*/
JNIEXPORT void JNICALL Java_local_LocalDatabase_clearFlags(JNIEnv *, jobject, jobject, jint);

 /*
 * Class:     local_LocalDatabase
 * Method:    startDelete
 * Signature: ([Lmds/Data/NidData;)[Lmds/NidData;
 */
  JNIEXPORT jobjectArray JNICALL Java_local_LocalDatabase_startDelete
      (JNIEnv *, jobject, jobjectArray);

/*
 * Class:     local_LocalDatabase
 * Method:    write
 * Signature: ()V
 */
  JNIEXPORT void JNICALL Java_local_LocalDatabase_write(JNIEnv *, jobject);

/*
 * Class:     local_LocalDatabase
 * Method:    restoreContext
 * Signature: ()I
 */
 JNIEXPORT jint JNICALL Java_local_LocalDatabase_saveContext(JNIEnv * env, jobject obj) {

/*
 * Class:     local_LocalDatabase
 * Method:    restoreContext
 * Signature: ()V
 */
 JNIEXPORT void JNICALL Java_local_LocalDatabase_restoreContext(JNIEnv * env, jobject obj, void *context) {


#ifdef __cplusplus
}
#endif
#endif
