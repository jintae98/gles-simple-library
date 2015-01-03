package com.gomdev.gles;

import java.nio.ByteBuffer;

/**
 * Created by gomdev on 15. 1. 2..
 */
public class GLESCompressedTextureInfo {
    static final String CLASS = "GLESCompressedTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private int mWidth;
    private int mHeight;
    private ByteBuffer mData;

    public GLESCompressedTextureInfo(int width, int height, ByteBuffer data) {
        mWidth = width;
        mHeight = height;
        mData = data;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public ByteBuffer getData() {
        return mData;
    }

}
