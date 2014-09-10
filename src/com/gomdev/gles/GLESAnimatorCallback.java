package com.gomdev.gles;

public abstract interface GLESAnimatorCallback {
    public abstract void onAnimation(GLESVector3 paramGLESVector);

    public abstract void onCancel();

    public abstract void onFinished();
}