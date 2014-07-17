package com.gomdev.gles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.gomdev.gles.GLESConfig.DepthLevel;
import com.gomdev.gles.GLESConfig.ProjectionType;

public abstract class GLESObject {
    private static final String CLASS = "GLESObject";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    protected Context mContext;

    protected Resources mRes;
    protected GLESShader mShader;

    protected GLESTexture mTexture;
    protected GLESProjection mProjection;
    protected GLESTransform mTransform;

    protected float mBitmapHeight;
    protected float mBitmapWidth;

    protected float mHeight;
    protected float mWidth;

    protected GLESVertexInfo mVertexInfo = null;

    protected boolean mIsVisible = false;

    protected DepthLevel mDepth = DepthLevel.DEFAULT_LEVEL_DEPTH;

    protected int mSpaceInfoHandle = -1;

    protected int[] mVBOIDs = null;

    public GLESObject(Context context) {
        mContext = context;
        mRes = context.getResources();

        mTexture = new GLESTexture();
    }

    public void setVertexInfo(GLESVertexInfo vertexInfo) {
        mVertexInfo = vertexInfo;
    }

    public void setShader(GLESShader shader) {
        mShader = shader;
        shader.useProgram();

        getUniformLocations();

        mTransform = new GLESTransform(shader);
    }

    public void setupSpace(GLESProjection projection, int width, int height) {
        mProjection = projection;

        mWidth = width;
        mHeight = height;
    }

    public void setupSpace(ProjectionType projectionType, int width, int height) {
        mWidth = width;
        mHeight = height;

        mProjection = new GLESProjection(mShader, projectionType, width, height);
    }

    public void setupSpace(ProjectionType projectionType, int width,
            int height, float projScale) {
        mWidth = width;
        mHeight = height;

        mProjection = new GLESProjection(mShader, projectionType, width,
                height, projScale);
    }

    public void setTexture(Bitmap bitmap, boolean needToRecycle) {
        if (bitmap == null) {
            return;
        }
        mTexture.changeTexture(bitmap, needToRecycle);
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

    public void changeTexture(Bitmap bitmap, boolean needToRecycle) {
        if (bitmap == null || mTexture == null) {
            return;
        }

        mTexture.changeTexture(bitmap, needToRecycle);
    }

    public void changeTexture(GLESTexture texture) {
        if (texture == null) {
            return;
        }

        mTexture = texture;
    }

    public void show() {
        mIsVisible = true;
    }

    public void hide() {
        mIsVisible = false;
    }

    public void setDepth(DepthLevel depth) {
        mDepth = depth;
    }

    public void drawObject() {
        if (mIsVisible == true) {
            mShader.useProgram();

            this.update();
            this.draw();
        }
    }

    public void syncAll() {
        mProjection.sync();
        mTransform.sync();
    }

    protected abstract void update();

    protected abstract void draw();

    protected abstract void getUniformLocations();

}