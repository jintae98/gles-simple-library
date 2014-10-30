package com.gomdev.gles;

public class GLESParticle {
    public float mX = 0f;
    public float mY = 0f;
    public float mZ = 0f;

    private float mVelocity = 1f;
    private float mNormalizedDuration = 1f;

    public GLESParticle(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public void setVelocity(float vel) {
        mVelocity = vel;
        
        if (mVelocity != 1.0f) {
            mNormalizedDuration = 1f / mVelocity;
        }
    }

    public float getVelocity() {
        return mVelocity;
    }

    public float getNormalizedDuration() {
        return mNormalizedDuration;
    }
}
