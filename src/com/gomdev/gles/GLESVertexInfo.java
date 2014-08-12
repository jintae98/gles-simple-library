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

    private FloatBuffer mVertexBuffer = null;
    private int mNumOfVertexElements = 0;
    private int mVertexVBOID = -1;

    private FloatBuffer mTexCoordBuffer = null;
    private int mNumOfTexCoordElements = 0;
    private int mTexCoordVBOID = -1;

    private FloatBuffer mNormalBuffer = null;
    private int mNumOfNormalElements = 0;
    private int mNormalVBOID = -1;

    private FloatBuffer mColorBuffer = null;
    private int mNumOfColorElements = 0;
    private int mColorVBOID = -1;

    private ShortBuffer mIndexBuffer = null;
    private int mIndexVBOID = -1;

    public GLESVertexInfo() {

    }

    public void setVertexBuffer(float[] vertex, int numOfElements) {
        mVertexBuffer = GLESUtils.makeFloatBuffer(vertex);
        mNumOfVertexElements = numOfElements;
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public int getNumOfVertexElements() {
        return mNumOfVertexElements;
    }

    public void setVertexVBOID(int id) {
        mVertexVBOID = id;
    }

    public int getVertexVBOID() {
        return mVertexVBOID;
    }

    public void setTexCoordBuffer(float[] texCoord, int numOfElements) {
        mTexCoordBuffer = GLESUtils.makeFloatBuffer(texCoord);
        mNumOfTexCoordElements = numOfElements;
        mIsUseTexture = true;
    }

    public FloatBuffer getTexCoordBuffer() {
        return mTexCoordBuffer;
    }

    public int getNumOfTexCoordElements() {
        return mNumOfTexCoordElements;
    }

    public boolean isUseTexCoord() {
        return mIsUseTexture;
    }

    public void setTexCoordVBOID(int id) {
        mTexCoordVBOID = id;
    }

    public int getTexCoordVBOID() {
        return mTexCoordVBOID;
    }

    public void setNormalBuffer(float[] normal, int numOfElements) {
        mNormalBuffer = GLESUtils.makeFloatBuffer(normal);
        mNumOfNormalElements = numOfElements;
        mIsUseNormal = true;
    }

    public FloatBuffer getNormalBuffer() {
        return mNormalBuffer;
    }

    public int getNumOfNormalElements() {
        return mNumOfNormalElements;
    }

    public boolean isUseNormal() {
        return mIsUseNormal;
    }

    public void setNormalVBOID(int id) {
        mNormalVBOID = id;
    }

    public int getNormalVBOID() {
        return mNormalVBOID;
    }

    public void setColorBuffer(float[] color) {
        setColorBuffer(color, GLESConfig.NUM_OF_COLOR_ELEMENT);
    }

    public void setColorBuffer(float[] color, int numOfElements) {
        mColorBuffer = GLESUtils.makeFloatBuffer(color);
        mNumOfColorElements = numOfElements;
        mIsUseColor = true;
    }

    public FloatBuffer getColorBuffer() {
        return mColorBuffer;
    }

    public int getNumOfColorElements() {
        return mNumOfColorElements;
    }

    public boolean isUseColor() {
        return mIsUseColor;
    }

    public void setColorVBOID(int id) {
        mColorVBOID = id;
    }

    public int getColorVBOID() {
        return mColorVBOID;
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

    public void setIndexVBOID(int id) {
        mIndexVBOID = id;
    }

    public int getIndexVBOID() {
        return mIndexVBOID;
    }
}
