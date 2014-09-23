package com.gomdev.gles;

import android.opengl.Matrix;
import android.util.Log;

public class GLESTransform {
    private static final String CLASS = "GLESTransform";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESSpatial mOwner = null;

    private float[] mMMatrix = new float[16];

    public GLESTransform() {
        init();
    }

    public GLESTransform(GLESSpatial owner) {
        mOwner = owner;

        init();
    }

    private void init() {
        Matrix.setIdentityM(mMMatrix, 0);
    }

    public void destroy() {
        mMMatrix = null;
    }

    public GLESSpatial getOwner() {
        return mOwner;
    }

    public void setIdentity() {
        Matrix.setIdentityM(mMMatrix, 0);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(mMMatrix, 0, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mMMatrix, 0, angle, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(mMMatrix, 0, x, y, z);
    }

    public void setMatrix(float[] matrix) {
        System.arraycopy(matrix, 0, mMMatrix, 0, matrix.length);
    }

    public float[] getMatrix() {
        float[] matrix = new float[16];
        System.arraycopy(mMMatrix, 0, matrix, 0, matrix.length);
        return matrix;
    }

    public float[] getInverseMatrix() {
        float[] matrix = new float[16];
        Matrix.invertM(matrix, 0, mMMatrix, 0);
        return matrix;
    }

    public void dump(String str) {
        if (!DEBUG) {
            return;
        }

        Log.d(TAG, str + " mMMatrix : ");

        for (int i = 0; i < mMMatrix.length; i += 4) {
            Log.d(TAG, "\t [ " + mMMatrix[i] + ", " + mMMatrix[i + 1] + ", "
                    + mMMatrix[i + 2] + ", " + mMMatrix[i + 3] + " ]");
        }
    }
}