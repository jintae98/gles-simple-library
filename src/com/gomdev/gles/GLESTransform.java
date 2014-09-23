package com.gomdev.gles;

import android.opengl.Matrix;
import android.util.Log;

public class GLESTransform {
    private static final String CLASS = "GLESTransform";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESSpatial mOwner = null;

    private GLESVector3 mTranslate = new GLESVector3(0f, 0f, 0f);
    private GLESVector3 mPreTranslate = new GLESVector3(0f, 0f, 0f);
    private float mScale = 1f;
    private float[] mRotate = new float[16];

    private float[] mTempMatrix = new float[16];
    private float[] mMMatrix = new float[16];

    private boolean mIsPreTranslate = false;
    private boolean mIsTranslate = false;
    private boolean mIsScale = false;
    private boolean mIsRotate = false;

    private boolean mNeedToUpdate = false;

    public GLESTransform() {
        init();
    }

    public GLESTransform(GLESSpatial owner) {
        mOwner = owner;

        init();
    }

    private void init() {
        mIsTranslate = false;
        mTranslate.set(0f, 0f, 0f);

        mIsPreTranslate = false;
        mPreTranslate.set(0f, 0f, 0f);

        mIsScale = false;
        mScale = 1f;

        mIsRotate = false;
        Matrix.setIdentityM(mRotate, 0);

        Matrix.setIdentityM(mMMatrix, 0);
    }

    public void destroy() {
    }

    public GLESSpatial getOwner() {
        return mOwner;
    }

    public void setIdentity() {
        init();
    }

    public void setTranslate(float x, float y, float z) {
        mTranslate.set(x, y, z);
        mIsTranslate = true;
        mNeedToUpdate = true;
    }

    public void setTranslate(GLESVector3 vec) {
        mTranslate.set(vec);
        mIsTranslate = true;
        mNeedToUpdate = true;
    }

    public void translate(float x, float y, float z) {
        mTranslate.mX += x;
        mTranslate.mY += y;
        mTranslate.mZ += z;

        mIsTranslate = true;
        mNeedToUpdate = true;
    }

    public void translate(GLESVector3 vec) {
        mTranslate.mX += vec.mX;
        mTranslate.mY += vec.mY;
        mTranslate.mZ += vec.mZ;

        mIsTranslate = true;
        mNeedToUpdate = true;
    }

    public GLESVector3 getTranslate() {
        return mTranslate;
    }

    public boolean isSetTranslate() {
        return mIsTranslate;
    }

    public void setPreTranslate(float x, float y, float z) {
        mPreTranslate.set(x, y, z);
        mIsPreTranslate = true;
        mNeedToUpdate = true;
    }

    public void setPreTranslate(GLESVector3 vec) {
        mPreTranslate.set(vec);
        mIsPreTranslate = true;
        mNeedToUpdate = true;
    }

    public void preTranslate(float x, float y, float z) {
        mPreTranslate.mX += x;
        mPreTranslate.mY += y;
        mPreTranslate.mZ += z;

        mIsPreTranslate = true;
        mNeedToUpdate = true;
    }

    public void preTranslate(GLESVector3 vec) {
        mPreTranslate.mX += vec.mX;
        mPreTranslate.mY += vec.mY;
        mPreTranslate.mZ += vec.mZ;

        mIsPreTranslate = true;
        mNeedToUpdate = true;
    }

    public GLESVector3 getPreTranslate() {
        return mPreTranslate;
    }

    public boolean isSetPreTranslate() {
        return mIsPreTranslate;
    }

    public void setRotate(float angle, float x, float y, float z) {
        Matrix.setRotateM(mRotate, 0, angle, x, y, z);
        mIsRotate = true;
        mNeedToUpdate = true;
    }

    public void setRotate(float[] rotate) {
        System.arraycopy(rotate, 0, mRotate, 0, mRotate.length);
        mIsRotate = true;
        mNeedToUpdate = true;
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mRotate, 0, angle, x, y, z);
        mIsRotate = true;
        mNeedToUpdate = true;
    }

    public void rotate(float[] rotate) {
        Matrix.multiplyMM(mTempMatrix, 0, mRotate, 0, rotate, 0);
        System.arraycopy(mTempMatrix, 0, mRotate, 0, mRotate.length);
        mIsRotate = true;
        mNeedToUpdate = true;
    }

    public float[] getRotate() {
        return mRotate;
    }

    public boolean isSetRotate() {
        return mIsRotate;
    }

    public void setScale(float scale) {
        mScale = scale;
        mIsScale = true;
        mNeedToUpdate = true;
    }

    public void scale(float scale) {
        mScale *= scale;
        mIsScale = true;
        mNeedToUpdate = true;
    }

    public float getScale() {
        return mScale;
    }

    public boolean isSetScale() {
        return mIsScale;
    }

    public float[] getMatrix() {
        if (mNeedToUpdate == false) {
            return mMMatrix;
        }

        Matrix.setIdentityM(mMMatrix, 0);

        if (mIsTranslate == true) {
            Matrix.translateM(mMMatrix, 0, mTranslate.mX, mTranslate.mY,
                    mTranslate.mZ);
        }

        if (mIsScale == true) {
            Matrix.scaleM(mMMatrix, 0, mScale, mScale, mScale);
        }

        if (mIsRotate == true) {
            System.arraycopy(mMMatrix, 0, mTempMatrix, 0, mMMatrix.length);
            Matrix.multiplyMM(mMMatrix, 0, mTempMatrix, 0, mRotate, 0);
        }

        if (mIsPreTranslate == true) {
            Matrix.translateM(mMMatrix, 0, mPreTranslate.mX,
                    mPreTranslate.mY,
                    mPreTranslate.mZ);
        }

        mNeedToUpdate = false;

        return mMMatrix;
    }

    public void dump(String str) {
        if (!DEBUG) {
            return;
        }

        Log.d(TAG, "dump()");
        Log.d(TAG, "\t Translate " + mTranslate);
        Log.d(TAG, "\t Scale " + mScale);
        Log.d(TAG, "\t Rotate " + mRotate);
    }
}