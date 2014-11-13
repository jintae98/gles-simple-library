package com.gomdev.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLESTexture {
    static final String CLASS = "GLESTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private int mTextureID;

    private int mWidth;
    private int mHeight;

    private int mWrapMode = GLES20.GL_CLAMP_TO_EDGE;// 33071;

    public GLESTexture() {
    }

    public GLESTexture(int width, int height, Bitmap bitmap) {
        mWidth = width;
        mHeight = height;

        makeTexture(bitmap);
    }

    public GLESTexture(Bitmap bitmap) {
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        makeTexture(bitmap);
    }

    public GLESTexture(Bitmap bitmap, int wrapMode) {
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        mWrapMode = wrapMode;

        makeTexture(bitmap);
    }

    public void destroy() {
        if (GLES20.glIsTexture(mTextureID) == true) {
            int[] textureIDs = new int[1];
            textureIDs[0] = mTextureID;
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glDeleteTextures(1, textureIDs, 0);
        }
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

    private void makeTexture(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "makeTexture() bitmap is null");
            return;
        }

        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1, textureIDs, 0);
        mTextureID = textureIDs[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                mWrapMode);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                mWrapMode);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void makeSubTexture(int width, int height, boolean needToRecycle,
            Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "makeSubTexture() bitmap is null");
            return;
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, width, height, bitmap);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        if (needToRecycle == true) {
            bitmap.recycle();
        }
    }

    public void changeTexture(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "changeTexture() bitmap is null");
        }

        if (GLES20.glIsTexture(mTextureID) == false) {
            makeTexture(bitmap);
            return;
        }

        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        if ((Float.compare(bitmapWidth, mWidth) == 0)
                && (Float.compare(bitmapHeight, mHeight) == 0)) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        } else {
            int[] textureIDs = new int[1];
            textureIDs[0] = mTextureID;

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glDeleteTextures(1, textureIDs, 0);

            makeTexture(bitmap);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return;
    }

}