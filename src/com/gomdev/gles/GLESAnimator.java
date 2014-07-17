package com.gomdev.gles;

import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class GLESAnimator {
    private static final String CLASS = "GLESAnimator";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESAnimatorCallback mCallback;
    private Interpolator mInterpolator = null;

    private GLESVector mCurrent;
    private GLESVector mDistance;

    private long mDuration = 1000L;

    private GLESVector mFrom;
    private GLESVector mTo;

    private float mFromValue;
    private float mToValue;

    private boolean mIsFinished = false;
    private boolean mIsSetValue = false;
    private boolean mIsStarted = false;

    private long mStartOffset = 0L;
    private long mStartTick = 0L;

    private boolean mUseVector = true;

    public GLESAnimator(float fromValue, float toValue,
            GLESAnimatorCallback callback) {
        mFromValue = fromValue;
        mToValue = toValue;
        mDistance.set(mToValue - mFromValue, 0.0F, 0.0F);
        mIsSetValue = true;
        mUseVector = false;
        mCallback = callback;
    }

    public GLESAnimator(GLESAnimatorCallback callback) {
        mCurrent = new GLESVector(0f, 0f, 0f);
        mDistance = new GLESVector(0f, 0f, 0f);
        mIsSetValue = false;
        mCallback = callback;
    }

    public GLESAnimator(GLESVector from, GLESVector to,
            GLESAnimatorCallback callback) {
        mFrom = from;
        mTo = to;
        mDistance.set(mTo.mX - mFrom.mX, mTo.mY - mFrom.mY, mTo.mZ - mFrom.mZ);
        mIsSetValue = true;
        mUseVector = true;
        mCallback = callback;
    }

    public void cancel() {
        mIsFinished = true;
        mIsStarted = false;
        mInterpolator = null;
        if (mCallback != null)
            mCallback.onCancel();
    }

    public void destroy() {
        mInterpolator = null;
    }

    public boolean doAnimation() {
        if (mIsStarted == false) {
            return false;
        }

        if ((mIsFinished == true) && (mCallback != null)) {
            mCallback.onFinished();
            mIsStarted = false;
            return false;
        }

        long currentTick = System.currentTimeMillis();
        if (currentTick - mStartTick < mStartOffset) {
            return true;
        }

        long startTick = mStartTick + mStartOffset;
        float normalizedDuration = (float) (currentTick - startTick)
                / mDuration;

        if (normalizedDuration >= 1.0F) {
            normalizedDuration = 1.0F;
            mIsFinished = true;
        }

        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }

        normalizedDuration = mInterpolator.getInterpolation(normalizedDuration);
        if (mUseVector == true) {
            mCurrent.mX = mFrom.mX + mDistance.mX * normalizedDuration;
            mCurrent.mY = mFrom.mY + mDistance.mY * normalizedDuration;
            mCurrent.mZ = mFrom.mZ + mDistance.mZ * normalizedDuration;
        } else {
            mCurrent.mX = mFromValue + mDistance.mX * normalizedDuration;
            mCurrent.mY = 0f;
            mCurrent.mZ = 0f;
        }

        if (mCallback != null) {
            mCallback.onAnimation(mCurrent);
        }

        return true;
    }

    public GLESVector getCurrentValue() {
        if (mIsFinished == true) {
            return null;
        }

        long currentTick = System.currentTimeMillis();

        if ((currentTick - mStartTick) < mStartOffset) {
            return null;
        }

        long startTick = mStartTick + mStartOffset;

        float normalizedDuration = (float) (currentTick - startTick)
                / mDuration;

        if (normalizedDuration >= 1.0f) {
            normalizedDuration = 1.0f;
            mIsFinished = true;
        }

        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }

        normalizedDuration = mInterpolator.getInterpolation(normalizedDuration);

        if (mUseVector == true) {
            mCurrent.mX = mFrom.mX + mDistance.mX * normalizedDuration;
            mCurrent.mY = mFrom.mY + mDistance.mY * normalizedDuration;
            mCurrent.mZ = mFrom.mZ + mDistance.mZ * normalizedDuration;
        } else {
            mCurrent.mX = mFromValue + mDistance.mX * normalizedDuration;
            mCurrent.mY = 0f;
            mCurrent.mZ = 0f;
        }

        return mCurrent;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public void setDuration(long start, long end) {
        if (start < 0L) {
            Log.e(TAG, "setDuration() start=" + start + " is invalid");
            return;
        }

        mStartOffset = start;
        mDuration = (end - start);
    }

    public void setInterpolator(Interpolator paramInterpolator) {
        mInterpolator = paramInterpolator;
    }

    public void start() {
        if (mIsSetValue == false) {
            throw new IllegalStateException(
                    "quilt GLESAnimatorstart() should use start(from, to)");
        }

        mIsFinished = false;
        mIsStarted = true;
        mStartTick = System.currentTimeMillis();
    }

    public void start(float fromValue, float toValue) {
        mUseVector = false;

        mFromValue = fromValue;
        mToValue = toValue;

        mDistance.set(mToValue - mFromValue, 0.0F, 0.0F);

        mIsFinished = false;
        mIsStarted = true;

        mStartTick = System.currentTimeMillis();
    }

    public void start(GLESVector from, GLESVector to) {
        mUseVector = true;

        mFrom = from;
        mTo = to;

        mDistance.set(mTo.mX - mFrom.mX, mTo.mY - mFrom.mY, mTo.mZ - mFrom.mZ);

        mIsFinished = false;
        mIsStarted = true;

        mStartTick = System.currentTimeMillis();
    }
}
