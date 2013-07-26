LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := rc4
LOCAL_LDLIBS	:= -llog
LOCAL_SRC_FILES := rc4.c

include $(BUILD_SHARED_LIBRARY)

