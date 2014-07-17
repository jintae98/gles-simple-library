package com.gomdev.gles;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

import com.gomdev.gles.GLESConfig.ProjectionType;

public abstract class GLESParticleController {
    private static final String CLASS = "GLESParticleController";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    protected Context mContext = null;
    protected GLESShader mShader = null;
    protected DummyObject mDummyObject = null;

    protected ArrayList<GLESParticleSet> mParticleSetList = new ArrayList();

    private boolean mEnableAlphaBlending = false;
    private int mSrcAlpha;
    private int mSrcColor;
    private int mDstAlpha;
    private int mDstColor;

    private boolean mDisableDepthTest;

    protected float mWidth = 0.0F;
    protected float mHeight = 0.0F;

    private boolean mIsVisible = false;

    public GLESParticleController(Context context) {
        mContext = context;
        mDummyObject = new DummyObject(context, false, false);
    }

    public void addParticleSet(GLESParticleSet particleSet) {
        mParticleSetList.add(particleSet);
    }

    public void create(float width, float height) {
        mParticleSetList.clear();
        createParticleSet();

        for (GLESParticleSet particleSet : mParticleSetList) {
            particleSet.create(width, height);
            particleSet.setShader(mShader);
        }
        getUniformLocations();
    }

    protected abstract void createParticleSet();

    public void drawObject() {
        if (mIsVisible == false) {
            return;
        }

        mShader.useProgram();
        if (mEnableAlphaBlending == true) {
            GLESTransform.enableAlphaBlending(mSrcColor, mDstColor, mSrcAlpha,
                    mDstAlpha, mDisableDepthTest);
        }

        for (GLESParticleSet particleSet : mParticleSetList) {
            particleSet.update();
            particleSet.draw();
        }

        if (mEnableAlphaBlending == true) {
            GLESTransform.disableAlphaBlending();
        }

    }

    public void enableAlphaBlending(int srcColor, int dstColor, int srcAlpha,
            int dstAlpha, boolean disableDepthTest) {
        mSrcColor = srcColor;
        mSrcAlpha = srcAlpha;
        mDstColor = dstColor;
        mDstAlpha = dstAlpha;

        mDisableDepthTest = disableDepthTest;

        mEnableAlphaBlending = true;
    }

    public void enableAlphaBlending(boolean disableDepthTest) {
        mSrcColor = GLES20.GL_SRC_ALPHA;
        mSrcAlpha = GLES20.GL_SRC_ALPHA;
        mDstColor = GLES20.GL_ONE_MINUS_SRC_ALPHA;
        mDstAlpha = GLES20.GL_ONE_MINUS_SRC_ALPHA;

        mDisableDepthTest = disableDepthTest;

        mEnableAlphaBlending = true;
    }

    protected ArrayList<GLESParticleSet> getParticleSetList() {
        if (mParticleSetList == null) {
            Log.e(TAG, "getParticleSetList() mParticleSetList is null");
            return null;
        }
        return mParticleSetList;
    }

    protected GLESShader getShader() {
        if (mShader == null) {
            Log.e(TAG, "getShader() mShader is null");
            return null;
        }
        return mShader;
    }

    protected void getUniformLocations() {
    }

    public void hide() {
        mIsVisible = false;
    }

    public void reset() {
        for (GLESParticleSet particleSet : mParticleSetList) {
            particleSet.reset();
        }
    }

    public void setAlpha(float alpha) {
    }

    public void setShader(GLESShader paramGLESShader) {
        mShader = paramGLESShader;
        mShader.useProgram();
        mDummyObject.setShader(paramGLESShader);
    }

    public void setupSpace(ProjectionType projectionType, int width, int height) {
        mWidth = width;
        mHeight = height;
        mDummyObject.setupSpace(projectionType, width, height);
    }

    public void setupSpace(GLESConfig.ProjectionType projectionType, int width,
            int height, float projScale) {
        mWidth = width;
        mHeight = height;
        mDummyObject.setupSpace(projectionType, width, height, projScale);
    }

    public void setupSpace(GLESProjection projection, int width, int height) {
        mWidth = width;
        mHeight = height;
        mDummyObject.setupSpace(projection, width, height);
    }

    public void show() {
        mIsVisible = true;
    }

    public void syncAll() {
        mDummyObject.syncAll();
    }

    protected class DummyObject extends GLESObject {
        public DummyObject(Context context, boolean useTexture,
                boolean useNormal) {
            super(context, useTexture, useNormal);
        }

        protected void draw() {
        }

        protected void getUniformLocations() {
        }

        protected void update() {
        }
    }
}