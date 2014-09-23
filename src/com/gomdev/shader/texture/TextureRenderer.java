package com.gomdev.shader.texture;

import com.gomdev.gles.*;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESObject.PrimitiveMode;
import com.gomdev.gles.GLESObject.RenderType;
import com.gomdev.shader.EffectRenderer;
import com.gomdev.shader.EffectUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

public class TextureRenderer extends EffectRenderer {
    private static final String CLASS = "TextureRenderer";
    private static final String TAG = TextureConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = TextureConfig.DEBUG;

    private GLESSceneManager mSM = null;
    
    private GLESObject mTextureObject = null;
    private GLESShader mTextureShader = null;
    
    private Version mVersion;

    private boolean mIsTouchDown = false;

    private float mDownX = 0f;
    private float mDownY = 0f;

    private float mMoveX = 0f;
    private float mMoveY = 0f;

    public TextureRenderer(Context context) {
        super(context);

        mVersion = GLESContext.getInstance().getVersion();
        
        mSM = GLESSceneManager.createSceneManager();
        GLESNode root = mSM.createRootNode("Root");

        mTextureObject = mSM.createObject("TextureObject");
        mTextureObject.setPrimitiveMode(PrimitiveMode.TRIANGLES);
        mTextureObject.setRenderType(RenderType.DRAW_ELEMENTS);

        GLESGLState state = new GLESGLState();
        state.setCullFaceState(true);
        state.setCullFace(GLES20.GL_BACK);
        state.setDepthState(true);
        state.setDepthFunc(GLES20.GL_LEQUAL);
        mTextureObject.setGLState(state);

        root.addChild(mTextureObject);
    }

    public void destroy() {
        mTextureObject = null;
    }

    @Override
    protected void onDrawFrame() {
        if (DEBUG)
            Log.d(TAG, "onDrawFrame()");

        super.updateFPS();

        update();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderer.updateScene(mSM);
        mRenderer.drawScene(mSM);
    }

    private void update() {
        GLESTransform transform = mTextureObject.getTransform();

        transform.setIdentity();

        transform.rotate(mMoveX * 0.2f, 0f, 1f, 0f);
        transform.rotate(mMoveY * 0.2f, 1f, 0f, 0f);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        mRenderer.reset();

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        mTextureObject.setCamera(camera);

        GLESVertexInfo vertexInfo = GLESMeshUtils.createCube(width,
                false, true, false);
        mTextureObject.setVertexInfo(vertexInfo, true, true);
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
        int handle = mTextureShader.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false,
                camera.getProjectionMatrix(), 0);

        uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
        handle = mTextureShader.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(), 0);

        return camera;
    }

    @Override
    protected void onSurfaceCreated() {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 0.0f);

        mTextureObject.setShader(mTextureShader);

        Bitmap bitmap = GLESUtils.makeCheckerboard(512, 512, 32);
        GLESTexture texture = new GLESTexture(bitmap);
        bitmap.recycle();
        mTextureObject.setTexture(texture);
    }

    @Override
    protected boolean createShader() {
        Log.d(TAG, "createShader()");
        mTextureShader = new GLESShader(mContext);

        String vsSource = EffectUtils.getShaderSource(mContext, 0);
        String fsSource = EffectUtils.getShaderSource(mContext, 1);

        mTextureShader.setShaderSource(vsSource, fsSource);
        if (mTextureShader.load() == false) {
            mHandler.sendEmptyMessage(EffectRenderer.COMPILE_OR_LINK_ERROR);
            return false;
        }

        if (mVersion == Version.GLES_20) {
            String attribName = GLESShaderConstant.ATTRIB_POSITION;
            mTextureShader.setVertexAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_TEXCOORD;
            mTextureShader.setTexCoordAttribIndex(attribName);
        }

        return true;
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
}
