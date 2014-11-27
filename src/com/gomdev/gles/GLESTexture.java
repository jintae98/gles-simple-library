package com.gomdev.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

public abstract class GLESTexture {
    static final String CLASS = "GLESTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    protected int mTextureID;
    protected int mWidth;
    protected int mHeight;

    protected int mTarget = GLES20.GL_TEXTURE_2D;
    protected int mWrapMode = GLES20.GL_CLAMP_TO_EDGE;// 33071;
    protected int mMinFilter = GLES20.GL_LINEAR;
    protected int mMagFilter = GLES20.GL_LINEAR;

    public GLESTexture() {
    }

    public int getTarget() {
        return mTarget;
    }

    public void setWrapMode(int wrapMode) {
        mWrapMode = wrapMode;
    }

    public void setFilter(int minFilter, int magFilter) {
        mMinFilter = minFilter;
        mMagFilter = magFilter;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getTextureID() {
        if (!GLES20.glIsTexture(mTextureID)) {
            Log.e(TAG, "mTextureID is invalid");
            return -1;
        }
        return mTextureID;
    }

    public abstract void destroy();

    protected abstract void makeTexture(Bitmap[] bitmap);

    protected abstract void makeSubTexture(int width, int height,
            Bitmap[] bitmap);

    public abstract void changeTexture(Bitmap[] bitmap);

}