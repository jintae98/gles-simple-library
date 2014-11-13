package com.gomdev.gles;

public class GLESRect {
    static final String CLASS = "GLESRect";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public int mX = 0;
    public int mY = 0;
    public int mWidth = 0;
    public int mHeight = 0;

    public GLESRect() {

    }

    public GLESRect(int x, int y, int width, int height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
    }

    @Override
    public String toString() {
        return "GLESRect [mX=" + mX + ", mY=" + mY + ", mWidth=" + mWidth
                + ", mHeight=" + mHeight + "]";
    }
}
