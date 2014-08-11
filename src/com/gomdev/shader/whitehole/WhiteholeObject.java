package com.gomdev.shader.whitehole;

import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESCamera;
import com.gomdev.gles.GLESUtils;

public class WhiteholeObject extends GLESObject {
    private static final String CLASS = "WhiteholeObject";
    private static final String TAG = WhiteholeConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = WhiteholeConfig.DEBUG;

    private boolean mIsImageChanged;
    private Bitmap mBitmap;

    private int mPositionInVSHandle = -1;
    private int mPositionInFSHandle = -1;
    private int mRadiusHandle = -1;

    private float[] mDownPosInVS = new float[2];
    private float[] mDownPosInFS = new float[2];
    private float mRadius = 100.0f;

    public WhiteholeObject(Context context) {
        super(context);
    }

    @Override
    public void setupSpace(GLESCamera camera, int width, int height) {
        super.setupSpace(camera, width, height);

        int handle = mShader.getUniformLocation("uSpaceInfo"); // width, height,
                                                               // width / height
        GLES20.glUniform3f(handle, width, height, (float) width / height);
    }

    @Override
    protected void update() {
        checkImageChanged();

        GLES20.glUniform2f(mPositionInVSHandle, mDownPosInVS[0],
                mDownPosInVS[1]);
        GLES20.glUniform2f(mPositionInFSHandle, mDownPosInFS[0],
                mDownPosInFS[1]);
        GLES20.glUniform1f(mRadiusHandle, mRadius);
    }

    @Override
    protected void getUniformLocations() {
        mPositionInVSHandle = mShader.getUniformLocation("uTouchPosInVS");
        mPositionInFSHandle = mShader.getUniformLocation("uTouchPosInFS");

        mRadiusHandle = mShader.getUniformLocation("uRadius");
        GLES20.glUniform1f(mRadiusHandle, 0.1f);

        int handle = mShader.getUniformLocation("uBandWidth");
        GLES20.glUniform1f(handle, GLESUtils
                .getPixelFromDpi(mContext, WhiteholeConfig.BAND_WIDTH));
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        mIsImageChanged = true;
    }

    public void setPosition(float x, float y) {
        mDownPosInVS[0] = x - mWidth * 0.5f;
        mDownPosInVS[1] = mHeight * 0.5f - y;

        mDownPosInFS[0] = x / mWidth;
        mDownPosInFS[1] = y / mHeight;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    private void checkImageChanged() {
        if (mIsImageChanged == true) {
            mTexture.changeTexture(mBitmap, false);
            mBitmap = null;
            mIsImageChanged = false;
        }
    }

}
