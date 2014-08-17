package com.gomdev.gles;

import java.util.ArrayList;

import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESObject.RenderType;
import com.gomdev.gles.gles20.GLES20Renderer;
import com.gomdev.gles.gles30.GLES30Renderer;

import android.util.Log;

public abstract class GLESRenderer {
    private static final String CLASS = "GLESRenderer";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;

    private ArrayList<GLESObject> mObjects = new ArrayList<GLESObject>();
    protected GLESGLState mCurrentGLState = null;

    public static GLESRenderer createRenderer(Version version) {
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

    public void addObject(GLESObject object) {
        mObjects.add(object);
    }

    public void updateObjects() {
        for (GLESObject object : mObjects) {
            object.update();
            updateTransform(object.getShader(), object.getTransform());
        }
    }

    public void drawObjects() {
        for (GLESObject object : mObjects) {
            setGLState(object);
            bindTexture(object);
            drawPrimitive(object);
        }
    }

    private void drawPrimitive(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();

        shader.useProgram();

        enableVertexAttribute(object);

        RenderType renderType = object.getRenderType();
        if (renderType == RenderType.DRAW_ARRAYS) {
            drawArrays(object);
        } else if (renderType == RenderType.DRAW_ELEMENTS) {
            drawElements(object);
        }

        disableVertexAttribute(vertexInfo, shader);
    }

    public abstract void setupVBO(GLESVertexInfo vertexInfo);

    protected abstract void updateTransform(GLESShader shader,
            GLESTransform transform);

    protected abstract void setGLState(GLESObject object);

    protected abstract void bindTexture(GLESObject object);

    protected abstract void enableVertexAttribute(GLESObject object);

    protected abstract void drawArrays(GLESObject object);

    protected abstract void drawElements(GLESObject object);

    protected abstract void disableVertexAttribute(GLESVertexInfo vertexInfo,
            GLESShader shader);
}
