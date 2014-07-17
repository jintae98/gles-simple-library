package com.gomdev.gles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;

public abstract class GLESParticleSet {
    private static final String CLASS = "GLESParticleSet";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private Context mContext = null;
    private GLESShader mShader = null;
    private GLESTexture mTexture = null;

    protected Random mRandom = new Random();

    protected ArrayList<GLESParticle> mParticleList = new ArrayList();

    protected float mWidth = 0.0F;
    protected float mHeight = 0.0F;
    protected float mWidthInSpace = 0.0F;
    protected float mHeightInSpace = 0.0F;

    private float mMinParticleSize = 0.0F;
    private int mNumOfParticle = 0;

    private float mParticleSizeDeviation = 0.0F;

    public GLESParticleSet(Context paramContext, int numOfParticle,
            GLESTexture texture) {
        mContext = paramContext;
        mNumOfParticle = numOfParticle;
        mTexture = texture;
    }

    public void addParticle(GLESParticle particle) {
        mParticleList.add(particle);
    }

    public void clear() {
        mParticleList.clear();
    }

    public void create(float width, float height) {
        mWidth = width;
        mHeight = height;

        mWidthInSpace = GLESUtils.convertScreenToSpace(width);
        mHeightInSpace = GLESUtils.convertScreenToSpace(height);

        onCreate();
    }

    protected void disableAlphaBlending() {
        GLESTransform.disableAlphaBlending();
    }

    @SuppressLint("WrongCall")
    public void draw() {
        mShader.useProgram();

        onDraw();
    }

    protected void enableAlphaBlending(int srcColor, int dstColor,
            int srcAlpha, int dstAlpha, boolean disableDepthTest) {
        GLESTransform.enableAlphaBlending(srcColor, dstColor, srcAlpha,
                dstAlpha, disableDepthTest);
    }

    protected void enableAlphaBlending(boolean disableDepthTest) {
        GLESTransform.enableAlphaBlending(disableDepthTest);
    }

    public int getNumOfParticle() {
        return mNumOfParticle;
    }

    public ArrayList<GLESParticle> getParticleList() {
        return mParticleList;
    }

    protected float getParticleSize() {
        return mMinParticleSize + mRandom.nextFloat() * mParticleSizeDeviation;
    }

    protected float getParticleSize(float exp) {
        float f = (float) Math.pow(mRandom.nextFloat(), exp);
        return mMinParticleSize + f * mParticleSizeDeviation;
    }

    public GLESShader getShader() {
        if (mShader == null)
            Log.e(TAG, "getShader() mShader is null");
        return mShader;
    }

    public float getSpaceHeight() {
        return mHeightInSpace;
    }

    public float getSpaceWidth() {
        return mWidthInSpace;
    }

    public float getSurfaceHeight() {
        return mHeight;
    }

    public float getSurfaceWidth() {
        return mWidth;
    }

    public GLESTexture getTexture() {
        return mTexture;
    }

    protected abstract void onCreate();

    protected abstract void onDraw();

    protected abstract void onReset();

    protected abstract void onUpdate();

    public void reset() {
        onReset();
    }

    public void setParticleSize(float minInDpi) {
        mMinParticleSize = GLESUtils.getPixelFromDpi(mContext, minInDpi);
        mParticleSizeDeviation = 0.0F;
    }

    public void setParticleSizeRange(float min, float max) {
        if (max < min) {
            Log.e(TAG, "setSizeRange() max is smaller than min");
            return;
        }
        float f1 = GLESUtils.getPixelFromDpi(mContext, min);
        float f2 = GLESUtils.getPixelFromDpi(mContext, max);
        mMinParticleSize = f1;
        mParticleSizeDeviation = (f2 - f1);
    }

    public void setShader(GLESShader paramGLESShader) {
        mShader = paramGLESShader;
    }

    public void setupSpace(float width, float height) {
        mWidth = width;
        mHeight = height;
    }

    public void update() {
        mShader.useProgram();
        onUpdate();
    }
}