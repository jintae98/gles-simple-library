package com.gomdev.gles;

public class GLESRectF {
    static final String CLASS = "GLESRectF";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public float mX = 0;
    public float mY = 0;
    public float mWidth = 0;
    public float mHeight = 0;

    public GLESRectF() {

    }

    public GLESRectF(float x, float y, float width, float height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public String toString() {
        return "GLESRectF [mX=" + mX + ", mY=" + mY + ", mWidth=" + mWidth
                + ", mHeight=" + mHeight + "]";
    }
}
