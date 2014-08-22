package com.gomdev.shader.basic;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gomdev.gles.*;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESObject.PrimitiveMode;
import com.gomdev.gles.GLESObject.RenderType;
import com.gomdev.shader.EffectRenderer;
import com.gomdev.shader.EffectUtils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class BasicRenderer extends EffectRenderer implements Renderer {
    private static final String CLASS = "BasicRenderer";
    private static final String TAG = BasicConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = BasicConfig.DEBUG;
    private static final boolean DEBUG_PERF = BasicConfig.DEBUG_PERF;

    private GLESObject mBasicObject;
    private GLESShader mBasicShader;

    private boolean mIsTouchDown = false;

    private float mDownX = 0f;
    private float mDownY = 0f;

    private float mMoveX = 0f;
    private float mMoveY = 0f;

    public BasicRenderer(Context context) {
        super(context);

        mBasicObject = new GLESObject();
        mBasicObject.setTransform(new GLESTransform());
        mBasicObject.setPrimitiveMode(PrimitiveMode.TRIANGLES);
        mBasicObject.setRenderType(RenderType.DRAW_ELEMENTS);

        GLESGLState state = new GLESGLState();
        state.setCullFaceState(true);
        state.setCullFace(GLES20.GL_BACK);
        state.setDepthState(true);
        state.setDepthFunc(GLES20.GL_LEQUAL);
        mBasicObject.setGLState(state);

        mRenderer.addObject(mBasicObject);
    }

    public void destroy() {
        mBasicObject = null;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (DEBUG)
            Log.d(TAG, "onDrawFrame()");

        if (DEBUG_PERF)
            GLESUtils.checkFPS();

        update();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderer.updateObjects();
        mRenderer.drawObjects();
    }

    private void update() {
        GLESTransform transform = mBasicObject.getTransform();

        transform.setIdentity();

        transform.rotate(mMoveX * 0.2f, 0f, 1f, 0f);
        transform.rotate(mMoveY * 0.2f, 1f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        mBasicObject.setCamera(camera);
        mBasicObject.show();

        GLESVertexInfo vertexInfo = GLESMeshUtils.createCube(width,
                false, false, true);
        mBasicObject.setVertexInfo(vertexInfo, false, true);
    }

    private GLESCamera setupCamera(int width, int height) {
        GLESCamera camera = new GLESCamera();
        camera.setLookAt(0f, 0f, 2048f, 0f, 0f, 0f, 0f, 1f, 0f);

        float right = width * 0.5f / 4f;
        float left = -right;
        float top = height * 0.5f / 4f;
        float bottom = -top;
        float near = 128f;
        float far = 2048f * 2f;

        camera.setFrustum(left, right, bottom, top, near, far);

        String uniformName = GLESShaderConstant.UNIFORM_PROJ_MATRIX;
        int handle = mBasicShader.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false,
                camera.getProjectionMatrix(), 0);

        uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
        handle = mBasicShader.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(), 0);

        return camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 1.0f);

        createShader();

        mBasicObject.setShader(mBasicShader);
    }

    public void touchDown(float x, float y) {
        if (DEBUG)
            Log.d(TAG, "touchDown() x=" + x + " y=" + y);

        mIsTouchDown = true;

        mDownX = x;
        mDownY = y;

        mView.requestRender();
    }

    public void touchUp(float x, float y) {
        if (mIsTouchDown == false) {
            return;
        }

        mView.requestRender();

        mIsTouchDown = false;
    }

    public void touchMove(float x, float y) {
        if (mIsTouchDown == false) {
            return;
        }

        mMoveX = x - mDownX;
        mMoveY = y - mDownY;

        mView.requestRender();
    }

    public void touchCancel(float x, float y) {
    }

    public void showAll() {
        if (mBasicObject != null) {
            mBasicObject.show();
        }
    }

    public void hideAll() {
        if (mBasicObject != null) {
            mBasicObject.hide();
        }
    }

    private boolean createShader() {
        Log.d(TAG, "createShader()");
        mBasicShader = new GLESShader(mContext);

        String vsSource = EffectUtils.getVertexShaderSource(mContext, 0);
        String fsSource = EffectUtils.getFragmentShaderSource(mContext, 1);

        mBasicShader.setShaderSource(vsSource, fsSource);
        if (mBasicShader.load() == false) {
            mHandler.sendEmptyMessage(EffectRenderer.COMPILE_OR_LINK_ERROR);
            return false;
        }

        if (GLESConfig.GLES_VERSION == Version.GLES_20) {
            String attribName = GLESShaderConstant.ATTRIB_POSITION;
            mBasicShader.setVertexAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_COLOR;
            mBasicShader.setColorAttribIndex(attribName);
        }

        return true;
    }
}
