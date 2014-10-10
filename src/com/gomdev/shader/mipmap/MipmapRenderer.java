package com.gomdev.shader.mipmap;

import com.gomdev.gles.*;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.shader.EffectRenderer;
import com.gomdev.shader.EffectUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

public class MipmapRenderer extends EffectRenderer {
    private static final String CLASS = "MipmapRenderer";
    private static final String TAG = MipmapConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = MipmapConfig.DEBUG;

    private GLESSceneManager mSM = null;

    private GLESObject mNonMipmap = null;
    private GLESObject mMipmap = null;

    private GLESShader mShader = null;

    private Version mVersion;

    private float mScreenRatio = 0f;

    public MipmapRenderer(Context context) {
        super(context);

        mVersion = GLESContext.getInstance().getVersion();

        mSM = GLESSceneManager.createSceneManager();
        GLESNode root = mSM.createRootNode("Root");

        {
            mNonMipmap = mSM.createObject("NonMipmap");
            mNonMipmap.setListener(mNonMipampListener);

            GLESGLState state = new GLESGLState();
            state.setCullFaceState(true);
            state.setCullFace(GLES20.GL_BACK);
            state.setDepthState(true);
            state.setDepthFunc(GLES20.GL_LEQUAL);
            mNonMipmap.setGLState(state);

            root.addChild(mNonMipmap);
        }

        {
            mMipmap = mSM.createObject("Mipmap");
            mMipmap.setListener(mNonMipampListener);

            GLESGLState state = new GLESGLState();
            state.setCullFaceState(true);
            state.setCullFace(GLES20.GL_BACK);
            state.setDepthState(true);
            state.setDepthFunc(GLES20.GL_LEQUAL);
            mMipmap.setGLState(state);

            root.addChild(mMipmap);
        }
    }

    public void destroy() {
        mNonMipmap = null;
        mMipmap = null;
    }

    @Override
    protected void onDrawFrame() {
        super.updateFPS();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderer.updateScene(mSM);
        mRenderer.drawScene(mSM);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        mRenderer.reset();

        mScreenRatio = (float) width / height;

        int imgWidth = 1024;
        int imgHeight = 1024;
        Bitmap bitmap = GLESUtils.makeCheckerboard(imgWidth, imgHeight, 32);
        {
            GLESCamera camera = setupCamera(0, height / 2, width, height / 2);

            mNonMipmap.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils
                    .createPlane(mScreenRatio * 2f, mScreenRatio * 2f, false,
                            true,
                            false, false);
            mNonMipmap.setVertexInfo(vertexInfo, true, true);

            GLESTexture texture = new GLESTexture(bitmap);
            mNonMipmap.setTexture(texture);
        }

        {
            GLESCamera camera = setupCamera(0, 0, width, height / 2);

            mMipmap.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils
                    .createPlane(mScreenRatio * 2f, mScreenRatio * 2f, false,
                            true,
                            false, false);
            mMipmap.setVertexInfo(vertexInfo, true, true);

            GLESTexture texture = new GLESTexture(bitmap);
            mMipmap.setTexture(texture);

            if (mVersion == Version.GLES_20) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                        texture.getTextureID());
                GLESBitmapInfo bitmapInfo = new GLESBitmapInfo(bitmap);
                GLESUtils.generateMipamp(bitmapInfo);
            } else {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,
                        texture.getTextureID());
                GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,
                        GLES30.GL_TEXTURE_MIN_FILTER,
                        GLES30.GL_LINEAR_MIPMAP_LINEAR);

            }
        }
    }

    private GLESCamera setupCamera(int x, int y, int width, int height) {
        GLESCamera camera = new GLESCamera();

        float fovy = 150f;
        float eyeZ = 1f / (float) Math.tan(Math.toRadians(fovy * 0.5));

        camera.setLookAt(0f, 0f, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f);

        camera.setFrustum(fovy, mScreenRatio, eyeZ * 0.1f, 400f);

        camera.setViewport(new GLESRect(x, y, width, height));

        return camera;
    }

    @Override
    protected void onSurfaceCreated() {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 0.0f);

        mNonMipmap.setShader(mShader);
        mMipmap.setShader(mShader);
    }

    @Override
    protected boolean createShader() {
        if (DEBUG) {
            Log.d(TAG, "createShader()");
        }

        mShader = new GLESShader(mContext);

        String vsSource = EffectUtils.getShaderSource(mContext, 0);
        String fsSource = EffectUtils.getShaderSource(mContext, 1);

        mShader.setShaderSource(vsSource, fsSource);
        if (mShader.load() == false) {
            mHandler.sendEmptyMessage(EffectRenderer.COMPILE_OR_LINK_ERROR);
            return false;
        }

        if (mVersion == Version.GLES_20) {
            String attribName = GLESShaderConstant.ATTRIB_POSITION;
            mShader.setVertexAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_TEXCOORD;
            mShader.setTexCoordAttribIndex(attribName);
        }

        return true;
    }

    public void touchDown(float x, float y) {
        mView.requestRender();
    }

    public void touchUp(float x, float y) {
        mView.requestRender();
    }

    public void touchMove(float x, float y) {
        mView.requestRender();
    }

    public void touchCancel(float x, float y) {
    }

    private GLESObjectListener mNonMipampListener = new GLESObjectListener() {

        @Override
        public void update(GLESObject object) {
            GLESTransform transform = object.getTransform();

            transform.setIdentity();
            transform.setPreTranslate(0f, mScreenRatio, 0f);
            transform.setRotate(-60f, 1f, 0f, 0f);
            transform.setTranslate(0f, -0.5f, 0f);
        }

        @Override
        public void apply(GLESObject object) {
        }
    };
}