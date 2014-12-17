package com.gomdev.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLESTexture2D extends GLESTexture {
    static final String CLASS = "GLESTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private Bitmap mBitmap = null;

    protected GLESTexture2D(int width, int height) {
        super();

        mWidth = width;
        mHeight = height;

        init();
    }

    protected GLESTexture2D(int width, int height, Bitmap bitmap) {
        super();

        mWidth = width;
        mHeight = height;

        mBitmap = bitmap;

        init();
    }

    private void init() {
        mTarget = GLES20.GL_TEXTURE_2D;
    }

    @Override
    public void destroy() {
        if (GLES20.glIsTexture(mTextureID) == true) {
            int[] textureIDs = new int[1];
            textureIDs[0] = mTextureID;
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glDeleteTextures(1, textureIDs, 0);
        }
    }

    @Override
    protected void makeTexture() {
        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1, textureIDs, 0);
        mTextureID = textureIDs[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        if (mBitmap != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        } else {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mWidth, mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        }
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                mWrapMode);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                mWrapMode);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, mMagFilter);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, mMinFilter);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void makeSubTexture(int offsetX, int offsetY, Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "makeSubTexture() bitmap is null");
            return;
        }

        mBitmap = bitmap;

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, offsetX, offsetY, bitmap);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void makeSubTexture(int width, int height, Bitmap[] bitmaps) {
        Log.e(TAG, "should use makeSubTexture(..., Bitmap bitmap)");
    }

    @Override
    public void changeTexture(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "changeTexture() bitmap is null");
        }

        mBitmap = bitmap;

        if (GLES20.glIsTexture(mTextureID) == false) {
            makeTexture();
            return;
        }

        float bitmapWidth = mBitmap.getWidth();
        float bitmapHeight = mBitmap.getHeight();
        if ((Float.compare(bitmapWidth, mWidth) == 0)
                && (Float.compare(bitmapHeight, mHeight) == 0)) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mBitmap);
        } else {
            int[] textureIDs = new int[1];
            textureIDs[0] = mTextureID;

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glDeleteTextures(1, textureIDs, 0);

            makeTexture();
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return;
    }

    @Override
    public void changeTexture(Bitmap[] bitmaps) {
        Log.e(TAG, "should use changeTexture(..., Bitmap bitmap)");

        return;
    }

}