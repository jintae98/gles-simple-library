#include "com_lge_gles_GLESShader.h"

#include <jni.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <android/asset_manager.h>

#define  LOG_TAG    "gomdevs"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

jstring JNICALL Java_com_lge_gles_GLESShader_nGetShaderCompileLog
  (JNIEnv * env, jobject obj, jint shader) {
    GLint infoLen = 0;

    glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
    if(infoLen <= 1) {
        infoLen = 1024;
    }

    char* infoLog = malloc(sizeof(char) * infoLen);

    glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
    jstring result = (*env)->NewStringUTF(env, infoLog);

    free(infoLog);

    return result;

}

static int sBinaryFormat = -1;
int JNICALL Java_com_lge_gles_GLESShader_nRetrieveProgramBinary
  (JNIEnv * env, jobject obj, jint program, jstring str)
{
    GLint   binaryLength;
    GLvoid* binary;
    FILE*   outfile;
    GLenum	binaryFormat;

    const char* fileName = (*env)->GetStringUTFChars(env, str, NULL);
    if(fileName != NULL)
    {
        //
        //  Retrieve the binary from the program object
        //
        glGetProgramiv(program, GL_PROGRAM_BINARY_LENGTH_OES, &binaryLength);
//      checkGLError("retrieve() glGetProgramiv");
//      LOGE("retrieve() binaryLength=%d", binaryLength);
        binary = (GLvoid*)malloc(binaryLength);
        if(binary == NULL)
        {
            LOGE("retrieve() malloc fail");
        }
        glGetProgramBinaryOES(program, binaryLength, NULL, &binaryFormat, binary);
//       checkGLError("retrieve() glGetProgramBinaryOES");

        //
        //  Cache the program binary for future runs
        //
        outfile = fopen(fileName, "wb");
        if(outfile == NULL)
        {
            LOGE("retrieve() fopen error");	
            free(binary);
            return 0;
        }
        fwrite(binary, binaryLength, 1, outfile);
        fclose(outfile);
        free(binary);

        (*env)->ReleaseStringUTFChars(env, str, fileName);
    }

    sBinaryFormat = binaryFormat;

    return binaryFormat;
}

long * binaryLength;
GLvoid* * binary;
static int binarySize = 0;
char* * binaryFileName;

int readFile(JNIEnv * env, const char* fileName, int shaderNumber) {
    binaryFileName[shaderNumber] = (char*) malloc (strlen(fileName)+1);
    strcpy(binaryFileName[shaderNumber],fileName);

    FILE *fp = fopen(fileName, "rb");
    if(fp == NULL)
    {
        LOGE("load() file open fail");
        return 0;
    }

    fseek( fp, 0, SEEK_END );
    binaryLength[shaderNumber] = ftell(fp);
    LOGI("binaryLength=%d",binaryLength[shaderNumber]);
    fseek( fp, 0, SEEK_SET );

    binary[shaderNumber] = (GLvoid*) malloc (binaryLength[shaderNumber]);

    if(binary[shaderNumber] == NULL)
    {
        LOGE("load() malloc fail");
        return 0;
    }

    ssize_t size = fread(binary[shaderNumber], 1, binaryLength[shaderNumber], fp);

    if(size == -1)
    {
        LOGE("file read fail %d",size);
        return 0;
    }

    fclose(fp);

    return 1;
}

int JNICALL Java_com_lge_gles_GLESShader_nLoadProgramBinary
  (JNIEnv * env, jobject obj, jint program, jint binaryFormat, jstring str)
{
    GLint success;
    int i = 0;
    int shaderNumber = -1;
    const char* fileName = (*env)->GetStringUTFChars(env, str, NULL);

    if(fileName != NULL)
    {
        if(binarySize == 0) {//Intialize memory space
            binarySize+=10;
            shaderNumber = 0;

            LOGI("malloc binarySize=%d",binarySize);
            binaryLength = (long *)malloc(binarySize*sizeof(int));
            binary = (void *)malloc(binarySize*sizeof(int));
            binaryFileName = (char *)malloc(binarySize*sizeof(int));

            for(i=0; i<binarySize; i++) {
                binaryLength[i] = 0l;
                binary[i] = NULL;
                binaryFileName[i] = NULL;
            }
        }
        else {
            for(i=0; i<binarySize; i++) {
                if(binaryFileName[i] != NULL) {//Compare each binary file name
                    int result = strcmp(binaryFileName[i], fileName);

                    if(result == 0) {//Found shader number at binaryFileName array.
                        shaderNumber = i;
                        break;
                    }
                }
                else {//binaryFileName[i] is NULL.
                    shaderNumber = i;
                    break;
                }
            }

            if(shaderNumber == -1) {//No more space at array, realloc.
                shaderNumber = binarySize;
                binarySize+=10;

                LOGI("realloc binarySize=%d",binarySize);
                binaryLength = (long *)realloc(binaryLength,binarySize*sizeof(int));
                binary = (void *)realloc(binary,binarySize*sizeof(int));
                binaryFileName = (char *)realloc(binaryFileName,binarySize*sizeof(int));

                for(i=shaderNumber; i<binarySize; i++) {
                    binaryLength[i] = 0l;
                    binary[i] = NULL;
                    binaryFileName[i] = NULL;
                }
            }
        }

        if(binary[shaderNumber] == NULL) {
            int result = readFile(env, fileName, shaderNumber);
            if(result == 0) {
                return 0;
            }
        }

        //
        //  Load the binary into the program object -- no need to link!
        //
//        glProgramBinaryOES(program, binaryFormat, binary[shaderNumber], binaryLength[shaderNumber]);
        glProgramBinaryOES(program, sBinaryFormat, binary[shaderNumber], binaryLength[shaderNumber]);

        glGetProgramiv(program, GL_LINK_STATUS, &success);

        (*env)->ReleaseStringUTFChars(env, str, fileName);

        if (!success)
        {
            LOGE("load() link fail");
            return 0;
        }

        return 1;
    }
    else {
        LOGE("fileName is NULL");
        return 0;
    }
}

int JNICALL Java_com_lge_gles_GLESShader_nFreeBinary(JNIEnv * env, jobject obj) {
	int i;
    for(i=0; i<binarySize; i++) {
        if(binaryFileName[i] != NULL) {
            free(binary[i]);
            free(binaryFileName[i]);
        }
    }
    
    free(binary);
    free(binaryFileName);
    free(binaryLength);
}

void checkGLError(char* str)
{
    int error = 0;
    if((error = glGetError()) != 0x0)
    {
        LOGE("%s error=0x%x", str, error);
    }
}
