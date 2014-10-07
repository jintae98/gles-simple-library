package com.gomdev.gles;

import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESVertexInfo.RenderType;
import com.gomdev.gles.gles20.GLES20Renderer;
import com.gomdev.gles.gles30.GLES30Renderer;

import android.util.Log;

public abstract class GLESRenderer {
    static final String CLASS = "GLESRenderer";
    static final String TAG = GLESConfig.TAG + " " + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    protected GLESGLState mCurrentGLState = null;

    protected GLESRendererListener mListener = null;

    public static GLESRenderer createRenderer() {
        Version version = GLESContext.getInstance().getVersion();

        switch (version) {
        case GLES_20:
            return new GLES20Renderer();
        case GLES_30:
            return new GLES30Renderer();
        }

        Log.e(TAG, "createRenderer() you should select version!!!");
        return null;
    }

    protected GLESRenderer() {
        GLESContext.getInstance().setRenderer(this);
    }

    public void reset() {
        mCurrentGLState = null;
    }

    public void setListener(GLESRendererListener listener) {
        mListener = listener;
    }

    public void updateScene(GLESSceneManager sm) {
        GLESNode root = sm.getRootNode();
        root.update(0, true);
    }

    public void drawScene(GLESSceneManager sm) {
        GLESNode root = sm.getRootNode();
        root.draw(this);
    }

    void draw(GLESObject object) {
        applyCamera(object);
        applyTransform(object);
        setGLState(object);
        bindTexture(object);
        drawPrimitive(object);
    }

    private void drawPrimitive(GLESObject object) {
        GLESShader shader = object.getShader();

        shader.useProgram();

        enableVertexAttribute(object);

        RenderType renderType = object.getVertexInfo().getRenderType();
        switch (renderType) {
        case DRAW_ARRAYS:
            drawArrays(object);
            break;
        case DRAW_ELEMENTS:
            drawElements(object);
            break;
        case DRAW_ARRAYS_INSTANCED:
            drawArraysInstanced(object);
            break;
        case DRAW_ELEMENTS_INSTANCED:
            drawElementsInstanced(object);
            break;
        }

        disableVertexAttribute(object);
    }

    public abstract void setupVBO(GLESVertexInfo vertexInfo);

    protected abstract void applyCamera(GLESObject object);

    protected abstract void applyTransform(GLESObject object);

    protected abstract void setGLState(GLESObject object);

    protected abstract void bindTexture(GLESObject object);

    protected abstract void enableVertexAttribute(GLESObject object);

    protected abstract void drawArrays(GLESObject object);

    protected abstract void drawElements(GLESObject object);

    protected abstract void drawArraysInstanced(GLESObject object);

    protected abstract void drawElementsInstanced(GLESObject object);

    protected abstract void disableVertexAttribute(GLESObject object);
}
