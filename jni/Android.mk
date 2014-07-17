LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE :=	gles

LOCAL_SRC_FILES := com_lge_gles_GLESShader.c
LOCAL_LDLIBS :=  -llog -lGLESv2 -landroid

include $(BUILD_SHARED_LIBRARY)