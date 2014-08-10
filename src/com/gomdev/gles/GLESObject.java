package com.gomdev.gles;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public abstract class GLESObject {
    private static final String CLASS = "GLESObject";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    protected Context mContext;

    protected Resources mRes;
    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESCamera mCamera;
    protected GLESTransform mTransform;

    protected float mWidth;
    protected float mHeight;

    protected GLESVertexInfo mVertexInfo = null;

    protected boolean mIsVisible = false;

    public GLESObject(Context context) {
        mContext = context;
        mRes = context.getResources();
    }

    public void setVertexInfo(GLESVertexInfo vertexInfo) {
        mVertexInfo = vertexInfo;
    }

    public void setShader(GLESShader shader) {
        mShader = shader;
        shader.useProgram();

        getUniformLocations();
    }

    public void setupSpace(GLESCamera camera, int width, int height) {
        mCamera = camera;

        mWidth = width;
        mHeight = height;
    }

    public void setTexture(GLESTexture texture) {
        if (texture == null) {
            return;
        }

        mTexture = texture;
    }

    public GLESTexture getTexture() {
        return mTexture;
    }

    public void changeTexture(GLESTexture texture) {
        if (texture == null) {
            return;
        }

        mTexture = texture;
    }

    public void setTransform(GLESTransform transform) {
        mTransform = transform;
    }

    public GLESTransform getTransform() {
        return mTransform;
    }

    public void show() {
        mIsVisible = true;
    }

    public void hide() {
        mIsVisible = false;
    }

    public void drawObject() {
        if (mIsVisible == true) {
            mShader.useProgram();

            this.update();
            this.draw();
        }
    }

    protected abstract void update();

    protected abstract void draw();

    protected abstract void getUniformLocations();

}