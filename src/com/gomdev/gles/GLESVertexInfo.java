package com.gomdev.gles;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLESVertexInfo {
    static final String CLASS = "GLESVertexInfo";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public enum PrimitiveMode {
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        LINES,
        LINE_STRIP,
        LINE_LOOP,
        POINTS
    }

    public enum RenderType {
        DRAW_ELEMENTS,
        DRAW_ARRAYS,
        DRAW_ELEMENTS_INSTANCED,
        DRAW_ARRAYS_INSTANCED,
    }

    private PrimitiveMode mPrimitiveMode = PrimitiveMode.TRIANGLES;
    private RenderType mRenderType = RenderType.DRAW_ELEMENTS;
    private int mNumOfInstance = 0;

    private boolean mUseTexture = false;
    private boolean mUseNormal = false;
    private boolean mUseColor = false;
    private boolean mUseIndex = false;

    private FloatBuffer mPositionBuffer = null;
    private int mNumOfPositionElements = 0;
    private int mPositionVBOID = -1;

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

    private int mVAOID = -1;

    public GLESVertexInfo() {

    }

    public void setPrimitiveMode(PrimitiveMode mode) {
        mPrimitiveMode = mode;
    }

    public PrimitiveMode getPrimitiveMode() {
        return mPrimitiveMode;
    }

    public void setRenderType(RenderType type) {
        mRenderType = type;
    }

    public RenderType getRenderType() {
        return mRenderType;
    }

    public void setNumOfInstance(int num) {
        mNumOfInstance = num;
    }

    public int getNumOfInstance() {
        return mNumOfInstance;
    }

    public void setPositionBuffer(float[] position, int numOfElements) {
        mPositionBuffer = GLESUtils.makeFloatBuffer(position);
        mNumOfPositionElements = numOfElements;
    }

    public void setPositionBuffer(FloatBuffer buffer) {
        mPositionBuffer = buffer;
    }

    public FloatBuffer getPositionBuffer() {
        return mPositionBuffer;
    }

    public int getNumOfPositionElements() {
        return mNumOfPositionElements;
    }

    public void setPositionVBOID(int id) {
        mPositionVBOID = id;
    }

    public int getPositionVBOID() {
        return mPositionVBOID;
    }

    public void setTexCoordBuffer(float[] texCoord, int numOfElements) {
        mTexCoordBuffer = GLESUtils.makeFloatBuffer(texCoord);
        mNumOfTexCoordElements = numOfElements;
        mUseTexture = true;
    }

    public FloatBuffer getTexCoordBuffer() {
        return mTexCoordBuffer;
    }

    public int getNumOfTexCoordElements() {
        return mNumOfTexCoordElements;
    }

    public boolean useTexCoord() {
        return mUseTexture;
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
        mUseNormal = true;
    }

    public FloatBuffer getNormalBuffer() {
        return mNormalBuffer;
    }

    public int getNumOfNormalElements() {
        return mNumOfNormalElements;
    }

    public boolean useNormal() {
        return mUseNormal;
    }

    public void setNormalVBOID(int id) {
        mNormalVBOID = id;
    }

    public int getNormalVBOID() {
        return mNormalVBOID;
    }

    public void setColorBuffer(float[] color, int numOfElements) {
        mColorBuffer = GLESUtils.makeFloatBuffer(color);
        mNumOfColorElements = numOfElements;
        mUseColor = true;
    }

    public FloatBuffer getColorBuffer() {
        return mColorBuffer;
    }

    public int getNumOfColorElements() {
        return mNumOfColorElements;
    }

    public boolean useColor() {
        return mUseColor;
    }

    public void setColorVBOID(int id) {
        mColorVBOID = id;
    }

    public int getColorVBOID() {
        return mColorVBOID;
    }

    public void setIndexBuffer(short[] index) {
        mIndexBuffer = GLESUtils.makeShortBuffer(index);
        mUseIndex = true;
    }

    public ShortBuffer getIndexBuffer() {
        return mIndexBuffer;
    }

    public boolean useIndex() {
        return mUseIndex;
    }

    public void setIndexVBOID(int id) {
        mIndexVBOID = id;
    }

    public int getIndexVBOID() {
        return mIndexVBOID;
    }

    public void setVAOID(int id) {
        mVAOID = id;
    }

    public int getVAOID() {
        return mVAOID;
    }
}
