package com.gomdev.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLESTexture2D extends GLESTexture {
    static final String CLASS = "GLESTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public GLESTexture2D() {
        super();

        init(null);
    }

    public GLESTexture2D(int width, int height, Bitmap bitmap) {
        super();

        mWidth = width;
        mHeight = height;

        init(bitmap);
    }

    public GLESTexture2D(Bitmap bitmap) {
        super();

        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        init(bitmap);
    }

    public GLESTexture2D(Bitmap bitmap, int wrapMode) {
        super();

        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        mWrapMode = wrapMode;

        init(bitmap);

    }

    private void init(Bitmap bitmap) {
        mTarget = GLES20.GL_TEXTURE_2D;

        if (bitmap == null) {
            return;
        }

        Bitmap bitmaps[] = new Bitmap[] {
                bitmap
        };
        makeTexture(bitmaps);
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
    protected void makeTexture(Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "makeTexture() bitmaps is null");
            return;
        }

        Bitmap bitmap = bitmaps[0];

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

    @Override
    protected void makeSubTexture(int width, int height, Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "makeSubTexture() bitmap is null");
            return;
        }

        Bitmap bitmap = bitmaps[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, width, height, bitmap);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void changeTexture(Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "changeTexture() bitmap is null");
        }

        if (GLES20.glIsTexture(mTextureID) == false) {
            makeTexture(bitmaps);
            return;
        }

        Bitmap bitmap = bitmaps[0];

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

            makeTexture(bitmaps);
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return;
    }

}