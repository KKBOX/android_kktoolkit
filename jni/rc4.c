#include <jni.h>
#include <android/log.h>

//#define LOGE(a...) __android_log_print(ANDROID_LOG_ERROR, __FILE__, a)
#define LOGE(a...)
#define assert(a...)

typedef struct {
  unsigned char engineState[256];
  int i, j;
} RC4Struct;

typedef struct {
  RC4Struct current;
  RC4Struct marked;
} RC4Object;

static jlong JNICALL Java_com_kkbox_toolkit_crypto_Rc4_setKey
  (JNIEnv *env, jobject obj, jlong ptr, jbyteArray _key)
{
  RC4Object *this;
  unsigned char *key, *state;
  unsigned int b, a, i, tmp, length;

  if(ptr == -1) {
    this = (RC4Object *) malloc(sizeof(RC4Object));
  } else {
    this = (RC4Object *) ptr;
  }
  state = this->current.engineState;
  memset(&this->current, 0, sizeof(this->current));

  key = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, _key, 0);
  length = (*env)->GetArrayLength(env, _key);
  if(length > 256)
    length = 256;

  for(i = 0; i < 256; i++) state[i] = (unsigned char) i;

  a = b = i = 0;
  do {
    unsigned char tmp = state[a];
    b += key[i] + tmp; b &= 0xff;
    state[a] = state[b]; state[b] = tmp;

    if(++i >= length)
      i -= length;
  } while(++a < 256);

  (*env)->ReleasePrimitiveArrayCritical(env, _key, key, 0);
  return (jlong) this;
}

#define	PROCESS() \
  { unsigned char t;if(++i>=256)i=0; t=engineState[i];j+=t;j&=0xff; engineState[i] = engineState[j]; engineState[j] = t; }
  

static inline int nextState(RC4Object *this, int count)
{
  unsigned char *engineState = this->current.engineState;
  unsigned int i = this->current.i, j = this->current.j;

  while(count >= 16) {
    PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS();
    PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS();
    count -= 16;
  }

  if(count & 0x8) {
    PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS();
  }

  if(count & 0x4) {
    PROCESS(); PROCESS(); PROCESS(); PROCESS();
  }

  if(count & 0x2) {
    PROCESS(); PROCESS();
  }

  if(count & 0x1) {
    PROCESS();
  }

  this->current.i = i;
  this->current.j = j;
  return (engineState[i] + engineState[j]) & 0xff;
}

#undef PROCESS
//#define	PROCESS() \
//  LOGE("src=%x dst=%x i=%d j=%d *src=%x *dst=%x", src, dst, i, j, *src, *dst);\
//  { unsigned char t;if(++i>=256)i=0; t=engineState[i];j+=t;j&=0xff;engineState[i]=engineState[j]; engineState[j]=t; *dst++=*src++^engineState[(i+j)&0xff]; } \
//  LOGE("after *src=%x *dst=%x", *(src-1), *(dst-1));
#define	PROCESS() \
  { unsigned char t,s;if(++i>=256)i=0; t=engineState[i];j+=t;j&=0xff;s=engineState[i]=engineState[j]; engineState[j]=t; *dst++=*src++^engineState[(s+t)&0xff]; } \


static inline void processCryptString(RC4Object *this, char *src, int count, char *dst)
{
  unsigned char *engineState = this->current.engineState;
  unsigned int i = this->current.i, j = this->current.j;

  while(count >= 16) {
    PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS();
    PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS();
    count -= 16;
  }

  if(count & 0x8) {
    PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS(); PROCESS();
  }

  if(count & 0x4) {
    PROCESS(); PROCESS(); PROCESS(); PROCESS();
  }

  if(count & 0x2) {
    PROCESS(); PROCESS();
  }

  if(count & 0x1) {
    PROCESS();
  }

  this->current.i = i;
  this->current.j = j;
}
#undef PROCESS

static inline int getNextCode(RC4Object *this, int count)
{
  return this->current.engineState[nextState(this, count)];
}

static jint JNICALL Java_com_kkbox_toolkit_crypto_Rc4__getNextOutput
  (JNIEnv *env, jobject obj, jlong ptr)
{
  // 假設傳進來的 ptr 是對的, 不做 check
  RC4Object *this = (RC4Object *) ptr;
  return getNextCode(this, 1);
}

static void JNICALL Java_com_kkbox_toolkit_crypto_Rc4__1skip
  (JNIEnv *env, jobject obj, jlong ptr, jlong length)
{
  RC4Object *this = (RC4Object *) ptr;
  nextState(this, length);
}

static void JNICALL Java_com_kkbox_toolkit_crypto_Rc4__1crypt__J_3BI_3BII
  (JNIEnv *env, jobject obj, jlong ptr, jbyteArray _src, jint _srcoff, jbyteArray _dst, jint _dstoff, jint _len)
{
  LOGE("srcoff=%d dstoff=%d len=%d", _srcoff, _dstoff, _len);
  if(_src == _dst && _srcoff == _dstoff) {		// 如果是同一個來源, 表示蓋過去
    unsigned char *src = (unsigned char *) (*env)->GetPrimitiveArrayCritical(env, _src, 0);
    LOGE("src=dst, srcoff=dstoff, get origin src=%x", src);
    processCryptString((RC4Object *) ptr, src + _srcoff, _len, src + _srcoff);
    (*env)->ReleasePrimitiveArrayCritical(env, _src, src, 0);
  } else {						// 不同來源
    unsigned char *src = (unsigned char *) malloc(sizeof(unsigned char) * _len);
    unsigned char *p = src;
    (*env)->GetByteArrayRegion(env, _src, _srcoff, _len, src);
    processCryptString((RC4Object *) ptr, src, _len, src);
    (*env)->SetByteArrayRegion(env, _dst, _dstoff, _len, src);
    free(src);
  }
}

static void JNICALL Java_com_kkbox_toolkit_crypto_Rc4__1release
  (JNIEnv *env, jobject obj, jlong ptr)
{
  if(ptr != -1 && ptr != 0)
    free((RC4Object *) ptr);
}

static void JNICALL Java_com_kkbox_toolkit_crypto_Rc4__1mark
  (JNIEnv *env, jobject obj, jlong ptr)
{
  RC4Object *this = (RC4Object *) ptr;
  memcpy(&this->marked, &this->current, sizeof(this->current));
}

static void JNICALL Java_com_kkbox_toolkit_crypto_Rc4__1restore
  (JNIEnv *env, jobject obj, jlong ptr)
{
  RC4Object *this = (RC4Object *) ptr;
  memcpy(&this->current, &this->marked, sizeof(this->current));
}

#define	registerMethods(a,b,c)	_registerMethods(a,b,c,sizeof(c)/sizeof(*c))
static int _registerMethods(JNIEnv *env, char *className, JNINativeMethod *list, int methods)
{
  jclass class;
  int res = 1;

  class = (*env)->FindClass(env, className);
  if(class == 0) {
    LOGE("ERROR: findClass('%s') Error\n", className);
    return 0;
  }

  if((*env)->RegisterNatives(env, class, list, methods) < 0) {
    LOGE("ERROR: registerMethods('%s') Error\n", className);
    res = 0;
  }
  return res;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
  JNIEnv* env = 0;
  jint res = -1;
  jclass class;

  do {
    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
      LOGE("ERROR: GetEnv failed\n");
      break;
    }
    assert(env != 0);

    {
      static JNINativeMethod jniMethods[] = {
	{ "setKey", "(J[B)J", (void *) Java_com_kkbox_toolkit_crypto_Rc4_setKey },
	{ "_getNextOutput", "(J)I", (void *) Java_com_kkbox_toolkit_crypto_Rc4__getNextOutput },
	{ "_skip", "(JJ)V", (void *) Java_com_kkbox_toolkit_crypto_Rc4__1skip },
	{ "_crypt", "(J[BI[BII)V", (void *) Java_com_kkbox_toolkit_crypto_Rc4__1crypt__J_3BI_3BII },
	{ "_release", "(J)V", (void *) Java_com_kkbox_toolkit_crypto_Rc4__1release },
	{ "_mark", "(J)V", (void *) Java_com_kkbox_toolkit_crypto_Rc4__1mark },
	{ "_restore", "(J)V", (void *) Java_com_kkbox_toolkit_crypto_Rc4__1restore }
      };

      if(!registerMethods(env, "com/kkbox/toolkit/crypto/Rc4", jniMethods))
	break;
    }


    res = JNI_VERSION_1_4;
  } while(0);

  return res;
}
