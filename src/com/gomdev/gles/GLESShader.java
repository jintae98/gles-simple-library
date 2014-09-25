package com.gomdev.gles;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class GLESShader {
    static final String CLASS = "GLESShader";
    static final String TAG = GLESConfig.TAG + " " + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private final Context mContext;
    private final Resources mRes;

    private int mProgram;

    private String mVertexShaderSource = null;
    private String mFragmentShaderSource = null;

    private int mVertexIndex = -1;
    private int mTexCoordIndex = -1;
    private int mColorIndex = -1;
    private int mNormalIndex = -1;

    private Map<String, Integer> mUniforms = new HashMap<String, Integer>();

    private StringBuilder mCompileLog = new StringBuilder();

    public GLESShader(Context context) {
        mContext = context;
        mRes = context.getResources();
        mProgram = GLES20.glCreateProgram();

        if (mProgram == 0) {
            Log.e(TAG, "glCreateProgram() error=" + GLES20.glGetError());
            throw new IllegalStateException("glCreateProgram() error="
                    + GLES20.glGetError());
        }

        useProgram();
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
        Integer location = mUniforms.get(uniformName);
        if (location == null) {
            int loc = GLES20.glGetUniformLocation(mProgram, uniformName);
            mUniforms.put(uniformName, loc);
            return loc;
        } else {
            return location;
        }
    }

    public boolean load() {
        return compileAndLink();
    }

    private boolean compileAndLink() {
        GLESContext context = GLESContext.getInstance();
        context.setShaderErrorLog(null);

        if (mVertexShaderSource == null || mFragmentShaderSource == null) {
            Log.e(TAG, "compileAndLink() shader source is not set!!!");
            return false;
        }

        boolean result = true;
        if (setShaderFromString(GLES20.GL_VERTEX_SHADER, mVertexShaderSource) == false) {
            result = false;
        }

        if (setShaderFromString(GLES20.GL_FRAGMENT_SHADER,
                mFragmentShaderSource) == false) {
            result = false;
        }

        if (result == false) {
            GLESContext.getInstance().setShaderErrorLog(mCompileLog.toString());
            return false;
        }

        if (linkProgram() == false) {
            GLESContext.getInstance().setShaderErrorLog(mCompileLog.toString());
            return false;
        }

        return true;
    }

    private boolean setShaderFromResource(int shaderType, int resourceID) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            String source = GLESUtils.getStringFromReosurce(mContext,
                    resourceID);
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

    private boolean setShaderFromString(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");

                String log = GLES20.glGetShaderInfoLog(shader);
                Log.e(TAG, log);
                mCompileLog.append(log);

                GLES20.glDeleteShader(shader);
                shader = 0;
                return false;
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

            String log = GLES20.glGetProgramInfoLog(mProgram);
            Log.e(TAG, log);
            mCompileLog.append(log);

            GLES20.glDeleteProgram(mProgram);
            mProgram = 0;
            return false;
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

        String filePath = getBinaryFilePath(fileName);

        if (DEBUG) {
            Log.d(TAG, "load() filePath=" + filePath);
        }

        if (GLESFileUtils.isExist(filePath) == false) {
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
            GLESFileUtils.delete(filePath);
            retrieveProgramBinary(filePath);

            return result;
        }

        return true;
    }

    private String getBinaryFilePath(String prefix) {
        String appDataPath = GLESUtils.getAppDataPathName(mContext);
        String versionName = GLESUtils.getAppVersionName(mContext);
        String path = appDataPath + prefix + "_" + versionName + ".dat";

        return path;
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

    public boolean setShaderSource(String vertexShaderSource,
            String fragmentShaderSource) {
        mVertexShaderSource = vertexShaderSource;
        mFragmentShaderSource = fragmentShaderSource;
        return true;
    }

    public boolean setVertexShaderSource(String vertexShaderSource) {
        mVertexShaderSource = vertexShaderSource;
        return true;
    }

    public boolean setFragmentShaderSource(String fragmentShaderSource) {
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