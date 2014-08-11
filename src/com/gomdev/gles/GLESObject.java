package com.gomdev.gles;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class GLESObject {
    private static final String CLASS = "GLESObject";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    public enum PrimitiveMode {
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN
    }

    public enum RenderType {
        DRAW_ELEMENTS,
        DRAW_ARRAYS
    }

    protected Context mContext;

    protected Resources mRes;
    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESCamera mCamera;
    protected GLESTransform mTransform;

    protected PrimitiveMode mPrimitiveMode = PrimitiveMode.TRIANGLES;
    protected RenderType mRenderType = RenderType.DRAW_ELEMENTS;

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

    public GLESVertexInfo getVertexInfo() {
        return mVertexInfo;
    }

    public void setShader(GLESShader shader) {
        mShader = shader;
        shader.useProgram();

        getUniformLocations();
    }

    public GLESShader getShader() {
        return mShader;
    }

    public void setPrimitiveMode(PrimitiveMode mode) {
        mPrimitiveMode = mode;
    }

    public PrimitiveMode getPrimitiveMode() {
        return mPrimitiveMode;
    }

    public void setRenderType(RenderType renderType) {
        mRenderType = renderType;
    }

    public RenderType getRenderType() {
        return mRenderType;
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

    protected void update() {
    }

    protected void getUniformLocations() {
    }

}
