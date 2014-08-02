#include "com_gomdev_gles_GLESShader.h"

#include <jni.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <EGL/egl.h>

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <android/asset_manager.h>

#include <vector>
#include <map>
#include <string>

#define  LOG_TAG    "gomdev"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

//#define DEBUG

#ifdef __cplusplus
extern "C" {
#endif

typedef struct _BinaryInfo {
    int length;
    std::string* name;
    void* binary;
} BinaryInfo;

static std::map<std::string, BinaryInfo*> sBinaryMap;

static PFNGLGETPROGRAMBINARYOESPROC glGetProgramBinaryOES = NULL;
static PFNGLPROGRAMBINARYOESPROC glProgramBinaryOES = NULL;

static int sBinaryFormat = -1;

BinaryInfo* readFile(JNIEnv * env, char* fileName, int shaderNumber) {
    FILE *fp = fopen(fileName, "rb");
    if (fp == NULL) {
        LOGE("\treadFile() file open fail");
        return NULL;
    }

    fseek(fp, 0, SEEK_END);
    int length = ftell(fp);
#ifdef DEBUG
    LOGI("\treadFile() length=%d", length);
#endif
    fseek(fp, 0, SEEK_SET);

    void* binary = (GLvoid*) malloc(length);
    if (binary == NULL) {
        LOGE("\treadFile() malloc fail");
        return NULL;
    }

    ssize_t size = fread(binary, 1, length, fp);

    if (size == -1) {
        LOGE("\treadFile() file read fail %d", size);
        free(binary);
        return NULL;
    }

    fclose(fp);

    std::string name = fileName;

    BinaryInfo* binaryInfo = (BinaryInfo*) malloc(sizeof(BinaryInfo));
    if (binaryInfo != NULL) {
        binaryInfo->length = length;
        binaryInfo->binary = binary;
        binaryInfo->name = new std::string(fileName);
    }

    return binaryInfo;
}

void insert(std::string name, BinaryInfo* info) {
    sBinaryMap[name] = info;
}

BinaryInfo* get(std::string name) {
    return sBinaryMap[name];
}

BinaryInfo* find(std::string name) {
    std::map<std::string, BinaryInfo*>::iterator iter = sBinaryMap.find(name);

    if (iter != sBinaryMap.end()) {
        return iter->second;
    }

    return NULL;
}

void freeCache() {
    std::map<std::string, BinaryInfo*>::iterator iter;
    for (iter = sBinaryMap.begin(); iter != sBinaryMap.end(); iter++) {
        BinaryInfo* info = iter->second;
        delete info->name;
        delete info;
    }

    sBinaryMap.clear();
}

void checkGLError(char* str) {
#ifdef DEBUG
    int error = 0;
    if ((error = glGetError()) != 0x0) {
        LOGE("%s error=0x%x", str, error);
    }
#endif
}

void dump() {
    LOGI("dump()");
    std::map<std::string, BinaryInfo*>::iterator iterator;

    for (iterator = sBinaryMap.begin(); iterator != sBinaryMap.end();
            iterator++) {
        LOGI("\tName=%s BinaryInfo=%p", (iterator->first).c_str(),
                iterator->second);
    }
}

jstring JNICALL Java_com_gomdev_gles_GLESShader_nGetShaderCompileLog(
        JNIEnv * env, jobject obj, jint shader) {
    GLint infoLen = 0;

    glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
    if (infoLen <= 1) {
        infoLen = 1024;
    }

    char* infoLog = (char*) malloc(sizeof(char) * infoLen);

    glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
    jstring result = env->NewStringUTF(infoLog);

    free(infoLog);

    return result;

}

int JNICALL Java_com_gomdev_gles_GLESShader_nRetrieveProgramBinary
(JNIEnv * env, jobject obj, jint program, jstring str)
{
    GLint binaryLength;
    GLvoid* binary;
    FILE* outfile;
    GLenum binaryFormat;

    const char* fileName = env->GetStringUTFChars(str, NULL);
    if(fileName != NULL)
    {
        glGetProgramiv(program, GL_PROGRAM_BINARY_LENGTH_OES, &binaryLength);

        checkGLError("retrieve() glGetProgramiv");

#ifdef DEBUG
        LOGI("retrieve() binaryLength=%d", binaryLength);
#endif
        binary = (GLvoid*)malloc(binaryLength);
        if(binary == NULL)
        {
            LOGE("nRetrieveProgramBinary() malloc fail");
        }

        if (glGetProgramBinaryOES == NULL) {
            glGetProgramBinaryOES = (PFNGLGETPROGRAMBINARYOESPROC) eglGetProcAddress("glGetProgramBinaryOES");
        }
        glGetProgramBinaryOES(program, binaryLength, NULL, &binaryFormat, binary);

        checkGLError("retrieve() glGetProgramBinaryOES");

        outfile = fopen(fileName, "wb");
        if(outfile == NULL)
        {
            LOGE("nRetrieveProgramBinary() fileName=%s", fileName);
            LOGE("nRetrieveProgramBinary() fopen error");
            free(binary);
            return 0;
        }
        fwrite(binary, binaryLength, 1, outfile);
        fclose(outfile);

        std::string name = fileName;

        BinaryInfo* binaryInfo = (BinaryInfo*)malloc(sizeof(BinaryInfo));
        if (binaryInfo != NULL ) {
            binaryInfo->name = new std::string(fileName);
            binaryInfo->length = binaryLength;
            binaryInfo->binary = binary;

            insert(name, binaryInfo);
        }

        env->ReleaseStringUTFChars(str, fileName);
    }

    sBinaryFormat = binaryFormat;

    return binaryFormat;
}

int JNICALL Java_com_gomdev_gles_GLESShader_nLoadProgramBinary
(JNIEnv * env, jobject obj, jint program, jint binaryFormat, jstring str)
{
    GLint success;
    int i = 0;
    int shaderNumber = -1;
    char* fileName = (char*)env->GetStringUTFChars(str, NULL);

#ifdef DEBUG
    dump();
#endif

    if(fileName != NULL)
    {
        std::string name = fileName;
        BinaryInfo* binaryInfo = find(name);
        if(binaryInfo != NULL) {
#ifdef DEBUG
            LOGI("Load() cache hit!!!");
#endif
            binaryInfo = get(name);
        } else {
#ifdef DEBUG
            LOGI("Load() cache miss!! - %s", fileName);
#endif
            binaryInfo = readFile(env, fileName, shaderNumber);
            if(binaryInfo == NULL) {
                LOGE("Load() binaryInfo is NULL");
                return 0;
            }

            insert(name, binaryInfo);
        }

        if (glProgramBinaryOES == NULL) {
            glProgramBinaryOES = (PFNGLPROGRAMBINARYOESPROC) eglGetProcAddress("glProgramBinaryOES");
        }
        glProgramBinaryOES(program,
                sBinaryFormat,
                binaryInfo->binary,
                binaryInfo->length);

        glGetProgramiv(program, GL_LINK_STATUS, &success);

        env->ReleaseStringUTFChars(str, fileName);

        if (!success)
        {
            LOGE("nLoadProgramBinary() link fail");
            return 0;
        }

        return 1;
    }
    else {
        LOGE("nLoadProgramBinary() fileName is NULL");
        return 0;
    }
}

int JNICALL Java_com_gomdev_gles_GLESShader_nFreeBinary(JNIEnv * env, jobject obj) {
    freeCache();
}

#ifdef __cplusplus
}
#endif
