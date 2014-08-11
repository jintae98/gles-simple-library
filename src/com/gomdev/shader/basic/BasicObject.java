package com.gomdev.shader.basic;

import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESCamera;
import com.gomdev.gles.GLESUtils;

public class BasicObject extends GLESObject {
    private static final String CLASS = "BasicObject";
    private static final String TAG = BasicConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = BasicConfig.DEBUG;

    private boolean mIsImageChanged;
    private Bitmap mBitmap;

    public BasicObject(Context context) {
        super(context);
    }

    @Override
    protected void update() {
        checkImageChanged();
    }

    @Override
    protected void getUniformLocations() {
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        mIsImageChanged = true;
    }

    private void checkImageChanged() {
        if (mIsImageChanged == true) {
            mTexture.changeTexture(mBitmap, false);
            mBitmap = null;
            mIsImageChanged = false;
        }
    }

}
