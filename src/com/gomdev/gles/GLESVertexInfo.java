package com.gomdev.gles;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLESVertexInfo {
    private static final String CLASS = "GLESVertexInfo";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private boolean mIsUseTexture = false;
    private boolean mIsUseNormal = false;
    private boolean mIsUseColor = false;
    private boolean mIsUseIndex = false;
    private boolean mIsUseTangent = false;

    private FloatBuffer mVertexBuffer = null;
    private FloatBuffer mTexCoordBuffer = null;
    private FloatBuffer mNormalBuffer = null;
    private FloatBuffer mColorBuffer = null;
    private ShortBuffer mIndexBuffer = null;
    private FloatBuffer mTangentBuffer = null;

    public GLESVertexInfo() {

    }

    public void setVertexBuffer(float[] vertex) {
        mVertexBuffer = GLESUtils.makeFloatBuffer(vertex);
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public void setTexCoordBuffer(float[] texCoord) {
        mTexCoordBuffer = GLESUtils.makeFloatBuffer(texCoord);
        mIsUseTexture = true;
    }

    public FloatBuffer getTexCoordBuffer() {
        return mTexCoordBuffer;
    }

    public boolean isUseTexCoord() {
        return mIsUseTexture;
    }

    public void setNormalBuffer(float[] normal) {
        mNormalBuffer = GLESUtils.makeFloatBuffer(normal);
        mIsUseNormal = true;
    }

    public FloatBuffer getNormalBuffer() {
        return mNormalBuffer;
    }

    public boolean isUseNormal() {
        return mIsUseNormal;
    }

    public void setColorBuffer(float[] color) {
        mColorBuffer = GLESUtils.makeFloatBuffer(color);
        mIsUseColor = true;
    }

    public FloatBuffer getColorBuffer() {
        return mColorBuffer;
    }

    public boolean isUseColor() {
        return mIsUseColor;
    }

    public void setIndexBuffer(short[] index) {
        mIndexBuffer = GLESUtils.makeShortBuffer(index);
        mIsUseIndex = true;
    }

    public ShortBuffer getIndexBuffer() {
        return mIndexBuffer;
    }

    public boolean isUseIndex() {
        return mIsUseIndex;
    }

    public void setTangentBuffer(float[] tangent) {
        mTangentBuffer = GLESUtils.makeFloatBuffer(tangent);
        mIsUseTangent = true;
    }

    public FloatBuffer getTangentBuffer() {
        return mTangentBuffer;
    }

    public boolean isUseTangent() {
        return mIsUseTangent;
    }
}
