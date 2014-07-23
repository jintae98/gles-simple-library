package com.gomdev.gles;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class GLESShader {
    private static final String CLASS = "GLESShader";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private final Context mContext;
    private final Resources mRes;

    private int mProgram;

    private String mVertexShaderSource = null;
    private String mFragmentShaderSource = null;

    private boolean mUseResourceID = true;
    private int mVertexShaderID = -1;
    private int mFragmentShaderID = -1;

    private int mVertexIndex = -1;
    private int mTexCoordIndex = -1;
    private int mColorIndex = -1;
    private int mNormalIndex = -1;

    public GLESShader(Context context) {
        mContext = context;
        mRes = context.getResources();
        mProgram = GLES20.glCreateProgram();

        if (mProgram == 0) {
            Log.e(TAG, "glCreateProgram() error=" + GLES20.glGetError());
            throw new IllegalStateException("glCreateProgram() error="
                    + GLES20.glGetError());
        }
    }

    public int getProgram() {
        return mProgram;
    }

    public int getVertexAttribIndex() {
        if (mVertexIndex == -1) {
            Log.e(TAG, "getVertexAttribIndex() mVertexIndex is not set");
        }

        return mVertexIndex;
    }

    public int getColorAttribIndex() {
        if (mColorIndex == -1) {
            Log.e(TAG, "getColorAttribIndex() mColorIndex is not set");
        }

        return mColorIndex;
    }

    public int getNormalAttribIndex() {
        if (mNormalIndex == -1) {
            Log.e(TAG, "getNormalAttribIndex() mNormalIndex is not set");
        }

        return mNormalIndex;
    }

    public int getTexCoordAttribIndex() {
        if (mTexCoordIndex == -1) {
            Log.e(TAG, "getTexCoordAttribIndex() mTexCoordIndex is not set");
        }

        return mTexCoordIndex;
    }

    public int getAttribLocation(String attribName) {
        return GLES20.glGetAttribLocation(mProgram, attribName);
    }

    public int getUniformLocation(String uniformName) {
        return GLES20.glGetUniformLocation(mProgram, uniformName);
    }

    public boolean load() {
        return compileAndLink();
    }

    private boolean compileAndLink() {
        if (mUseResourceID == true) {
            setShaderFromResource(GLES20.GL_VERTEX_SHADER, mVertexShaderID);
            setShaderFromResource(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderID);
        } else {
            setShaderFromString(GLES20.GL_VERTEX_SHADER, mVertexShaderSource);
            setShaderFromString(GLES20.GL_FRAGMENT_SHADER,
                    mFragmentShaderSource);
        }

        if (linkProgram() == false) {
            return false;
        }

        return true;
    }

    private boolean setShaderFromResource(int shaderType, int resourceID) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            String source = getShaderFromReosurce(resourceID);
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
                throw new RuntimeException("glShaderSource() Error");
            }
        }

        GLES20.glAttachShader(mProgram, shader);

        return true;
    }

    private String getShaderFromReosurce(int resourceID) {
        byte[] str;
        int strLength;
        String shader = null;
        InputStream is = mRes.openRawResource(resourceID);
        try {
            try {
                str = new byte[1024];
                strLength = 0;
                while (true) {
                    int bytesLeft = str.length - strLength;
                    if (bytesLeft == 0) {
                        byte[] buf2 = new byte[str.length * 2];
                        System.arraycopy(str, 0, buf2, 0, str.length);
                        str = buf2;
                        bytesLeft = str.length - strLength;
                    }
                    int bytesRead = is.read(str, strLength, bytesLeft);
                    if (bytesRead <= 0) {
                        break;
                    }
                    strLength += bytesRead;
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new Resources.NotFoundException();
        }

        try {
            shader = new String(str, 0, strLength, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Could not decode shader string");
        }

        return shader;
    }

    private boolean setShaderFromString(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
                throw new RuntimeException("glShaderSource() Error");
            }
        }

        GLES20.glAttachShader(mProgram, shader);

        return true;
    }

    private boolean linkProgram() {
        GLES20.glLinkProgram(mProgram);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(mProgram));
            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
            throw new RuntimeException("glLinkProgram() Error");
        }

        GLES20.glUseProgram(mProgram);

        return true;
    }

    public boolean load(String fileName) {
        boolean needToCompile = false;

        if (fileName == null) {
            Log.e(TAG, "load() fileName is null");
            return load();
        }

        StringBuilder path = GLESUtils.getDataPathName(mContext);
        String filePath = GLESUtils.makeAppStringPath(mContext, path,
                fileName);

        if (DEBUG) {
            Log.d(TAG, "load() filePath=" + filePath);
        }

        if (GLESUtils.checkFileExists(filePath) == false) {
            Log.d(TAG, "load() file is not exist");
            needToCompile = true;
        }

        if (needToCompile == false) {
            if (loadProgramBinary(filePath, -1) == 1) {
                if (DEBUG) {
                    Log.d(TAG, "load() loadProgramBinary() success");
                }
                return true;
            } else {
                if (DEBUG) {
                    Log.d(TAG, "load() loadProgramBinary() fail");
                }
            }

            needToCompile = true;
        }

        if (needToCompile == true) {
            Log.e(TAG, "Link Error file=" + filePath + " Compile again");

            boolean result = compileAndLink();
            GLESUtils.deleteFile(filePath);
            retrieveProgramBinary(filePath);

            return result;
        }

        return true;
    }

    public int loadProgramBinary(String fileName, int binaryFormat) {
        return nLoadProgramBinary(mProgram, binaryFormat, fileName);
    }

    public void retrieveProgramBinary(String fileName) {
        nRetrieveProgramBinary(mProgram, fileName);
    }

    public void setColorAttribIndex(String colorAttribName) {
        mColorIndex = GLES20.glGetAttribLocation(mProgram, colorAttribName);
    }

    public void setNormalAttribIndex(String normalAttribName) {
        mNormalIndex = GLES20.glGetAttribLocation(mProgram, normalAttribName);
    }

    public boolean setShadersFromResource(int vertexShaderID,
            int fragmentShaderID) {
        mUseResourceID = true;
        mVertexShaderID = vertexShaderID;
        mFragmentShaderID = fragmentShaderID;
        return true;
    }

    public boolean setShadersFromString(String vertexShaderSource,
            String fragmentShaderSource) {
        mUseResourceID = false;
        mVertexShaderSource = vertexShaderSource;
        mFragmentShaderSource = fragmentShaderSource;
        return true;
    }

    public void setTexCoordAttribIndex(String attribname) {
        mTexCoordIndex = GLES20.glGetAttribLocation(mProgram, attribname);
    }

    public void setVertexAttribIndex(String attribname) {
        mVertexIndex = GLES20.glGetAttribLocation(mProgram, attribname);
    }

    public void useProgram() {
        GLES20.glUseProgram(mProgram);
    }

    private native int nLoadProgramBinary(int paramInt1, int paramInt2,
            String paramString);

    private native int nRetrieveProgramBinary(int paramInt, String paramString);
}