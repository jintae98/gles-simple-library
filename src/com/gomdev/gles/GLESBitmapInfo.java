package com.gomdev.gles;

import android.graphics.Bitmap;

public class GLESBitmapInfo {
    public int mWidth = 0;
    public int mHeight = 0;

    public int[] mData = null;

    public GLESBitmapInfo() {

    }

    public GLESBitmapInfo(Bitmap bitmap) {
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        mData = new int[mWidth * mHeight];
        bitmap.getPixels(mData, 0, mWidth, 0, 0, mWidth, mHeight);
    }

    public GLESBitmapInfo(GLESBitmapInfo info) {
        mWidth = info.mWidth;
        mHeight = info.mHeight;
        mData = info.mData;
    }
}
