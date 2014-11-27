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
        if (mBitmap == null) {
            Log.e(TAG, "makeTexture() bitmaps is null");
            return;
        }

        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1, textureIDs, 0);
        mTextureID = textureIDs[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
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

    @Override
    protected void makeSubTexture(int width, int height, Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "makeSubTexture() bitmap is null");
            return;
        }

        Bitmap bitmap = bitmaps[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void changeTexture(Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "changeTexture() bitmap is null");
        }

        mBitmap = bitmaps[0];

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

}