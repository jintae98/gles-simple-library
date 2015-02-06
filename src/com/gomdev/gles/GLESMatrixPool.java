package com.gomdev.gles;

import java.util.LinkedList;

/**
 * Created by gomdev on 15. 2. 6..
 */
public class GLESMatrixPool {
    static final String CLASS = "GLESMatrixPool";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private static LinkedList<float[]> sMatrixs = new LinkedList<>();

    private GLESMatrixPool() {

    }

    static float[] getMatrix() {
        if (sMatrixs.isEmpty() == true) {
            return new float[16];
        }

        return sMatrixs.pop();
    }

    static void releaseMatrix(float[] matrix) {
        sMatrixs.push(matrix);
    }
}
