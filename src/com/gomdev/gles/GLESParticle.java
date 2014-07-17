package com.gomdev.gles;

public class GLESParticle {
    private static final String CLASS = "GLESParticle";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    public int mIndex = 0;

    public float mPosX = 0.0F;
    public float mPosY = 0.0F;
    public float mPosZ = 0.0F;

    public float mSize = 0.0F;

    public float mVecX = 0.0F;
    public float mVecY = 0.0F;

    public float mVelocityX = 0.0F;
    public float mVelocityY = 0.0F;

    public float mAlpha = 0.0F;

    public boolean mIsSetDefaultValue = false;

    public GLESParticle(int index) {
        mIndex = index;
        mIsSetDefaultValue = false;
    }

    public GLESParticle(int index, GLESParticle particle) {
        mIndex = index;
        copyParticleInfo(particle);
        mIsSetDefaultValue = true;
    }

    public void copyParticleInfo(GLESParticle particle) {
        mPosX = particle.mPosX;
        mPosY = particle.mPosY;
        mPosZ = particle.mPosZ;

        mSize = particle.mSize;

        mVecX = particle.mVecX;
        mVecY = particle.mVecY;

        mVelocityX = particle.mVelocityX;
        mVelocityY = particle.mVelocityY;

        mAlpha = particle.mAlpha;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public void setDirection(float vecX, float vecY) {
        mVecX = vecX;
        mVecY = vecY;
    }

    public void setPosition(float posX, float posY, float posZ) {
        mPosX = posX;
        mPosY = posY;
        mPosZ = posZ;
    }

    public void setSize(float size) {
        mSize = size;
    }

    public void setVelocity(float velocityX, float velocityY) {
        mVelocityX = velocityX;
        mVelocityY = velocityY;
    }
}
