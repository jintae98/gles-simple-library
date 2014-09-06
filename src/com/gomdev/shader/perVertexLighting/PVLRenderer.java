package com.gomdev.shader.perVertexLighting;

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
import android.opengl.Matrix;
import android.util.Log;

public class PVLRenderer extends EffectRenderer implements Renderer {
    private static final String CLASS = "PVLRenderer";
    private static final String TAG = PVLConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = PVLConfig.DEBUG;

    private GLESObject mCubeObject;
    private GLESObject mLightObject;
    private GLESShader mCubeShader;
    private GLESShader mLightShader;
    private Version mVersion;

    private boolean mIsTouchDown = false;

    private float mDownX = 0f;
    private float mDownY = 0f;

    private float mMoveX = 0f;
    private float mMoveY = 0f;

    private float mScreenRatio = 0f;

    private int mCubeNormalMatrixHandle = -1;
    private int mCubeLightPosHandle = -1;

    private int mLightNormalMatrixHandle = -1;
    private int mLightLightPosHandle = -1;

    private float mCubeLightX = 0f;
    private float mCubeLightY = 0f;
    private float mCubeLightZ = 0f;
    private float mCubeLightW = 0f;
    private float mRadius = 0f;

    private float mLightLightX = 0f;
    private float mLightLightY = 0f;
    private float mLightLightZ = 0f;
    private float mLightLightW = 1f;

    private GLESAnimator mAnimator;

    public PVLRenderer(Context context) {
        super(context);

        mVersion = GLESContext.getInstance().getVersion();

        GLESGLState state = new GLESGLState();
        state.setCullFaceState(true);
        state.setCullFace(GLES20.GL_BACK);
        state.setDepthState(true);
        state.setDepthFunc(GLES20.GL_LEQUAL);

        {
            mCubeObject = GLESSceneManager.createObject();
            mCubeObject.setTransform(new GLESTransform());
            mCubeObject.setPrimitiveMode(PrimitiveMode.TRIANGLES);
            mCubeObject.setRenderType(RenderType.DRAW_ELEMENTS);

            mCubeObject.setGLState(state);

            mRenderer.addObject(mCubeObject);
        }

        {
            mLightObject = GLESSceneManager.createObject();
            mLightObject.setTransform(new GLESTransform());
            mLightObject.setPrimitiveMode(PrimitiveMode.TRIANGLES);
            mLightObject.setRenderType(RenderType.DRAW_ELEMENTS);

            mLightObject.setGLState(state);

            mRenderer.addObject(mLightObject);
        }

        createAnimator();
    }

    private void createAnimator() {
        mAnimator = new GLESAnimator(new GLESAnimatorCallback() {

            @Override
            public void onFinished() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onAnimation(GLESVector vector) {
                mCubeLightX = (float) (Math.cos(vector.mX) * mRadius);
                mCubeLightY = (float) (Math.sin(vector.mX) * mRadius);
                mCubeLightZ = 0f;

                updateLight();

                mCubeShader.useProgram();

                GLES20.glUniform4f(mCubeLightPosHandle,
                        mCubeLightX,
                        mCubeLightY,
                        mCubeLightZ,
                        mCubeLightW);
            }
        });

        mAnimator.setDuration(0, 5000);
        mAnimator.setRepeat(true);
    }

    private void updateLight() {
        mLightShader.useProgram();

        GLESTransform transform = mLightObject.getTransform();

        transform.setIdentity();
        transform.translate(mCubeLightX, mCubeLightY, mCubeLightZ);

        GLESCamera camera = mLightObject.getCamera();
        float[] vMatrix = camera.getViewMatrix();
        float[] mMatrix = transform.getMatrix();

        float[] vmMatrix = new float[16];
        Matrix.multiplyMM(vmMatrix, 0, vMatrix, 0, mMatrix, 0);
        float[] normalMatrix = new float[9];

        for (int i = 0; i < 3; i++) {
            normalMatrix[i * 3 + 0] = vmMatrix[i * 4 + 0];
            normalMatrix[i * 3 + 1] = vmMatrix[i * 4 + 1];
            normalMatrix[i * 3 + 2] = vmMatrix[i * 4 + 2];
        }

        GLES20.glUniformMatrix3fv(mLightNormalMatrixHandle, 1, false,
                normalMatrix, 0);
    }

    public void destroy() {
        mCubeObject = null;
        mLightObject = null;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (DEBUG)
            Log.d(TAG, "onDrawFrame()");

        super.updateFPS();

        if (mAnimator.doAnimation() == true) {
            mView.requestRender();
        }

        updateCube();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderer.updateObjects();
        mRenderer.drawObjects();
    }

    private void updateCube() {
        mCubeShader.useProgram();

        GLESTransform transform = mCubeObject.getTransform();

        transform.setIdentity();
        transform.rotate(mMoveX * 0.2f, 0f, 1f, 0f);
        transform.rotate(mMoveY * 0.2f, 1f, 0f, 0f);

        GLESCamera camera = mCubeObject.getCamera();
        float[] vMatrix = camera.getViewMatrix();
        float[] mMatrix = transform.getMatrix();

        float[] vmMatrix = new float[16];
        Matrix.multiplyMM(vmMatrix, 0, vMatrix, 0, mMatrix, 0);
        float[] normalMatrix = new float[9];

        for (int i = 0; i < 3; i++) {
            normalMatrix[i * 3 + 0] = vmMatrix[i * 4 + 0];
            normalMatrix[i * 3 + 1] = vmMatrix[i * 4 + 1];
            normalMatrix[i * 3 + 2] = vmMatrix[i * 4 + 2];
        }

        GLES20.glUniformMatrix3fv(mCubeNormalMatrixHandle, 1, false,
                normalMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        mScreenRatio = (float) width / height;
        mRadius = mScreenRatio;

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        {
            mCubeObject.setCamera(camera);
            mCubeObject.show();

            GLESVertexInfo vertexInfo = GLESMeshUtils.createCube(
                    mScreenRatio * 0.5f, true, false, true);
            mCubeObject.setVertexInfo(vertexInfo, true, true);
        }

        {
            mLightObject.setCamera(camera);
            mLightObject.show();

            GLESVertexInfo vertexInfo = GLESMeshUtils.createSphere(0.1f, 10,
                    10, false, true, true);
            mLightObject.setVertexInfo(vertexInfo, true, false);

            mLightShader.useProgram();

            GLES20.glUniform4f(mLightLightPosHandle,
                    mLightLightX,
                    mLightLightY,
                    mLightLightZ,
                    mLightLightW);
        }

        mAnimator.start(0f, (float) (Math.PI * 2f));
    }

    private GLESCamera setupCamera(int width, int height) {
        GLESCamera camera = new GLESCamera();

        float fovy = 30f;
        float eyeZ = 1f / (float) Math.tan(Math.toRadians(fovy * 0.5));

        camera.setLookAt(0f, 0f, eyeZ, 0f, 0f, 0f, 0f, 1f, 0f);

        camera.setFrustum(fovy, mScreenRatio, 1f, 400f);

        {
            mCubeShader.useProgram();

            String uniformName = GLESShaderConstant.UNIFORM_PROJ_MATRIX;
            int handle = mCubeShader.getUniformLocation(uniformName);
            GLES20.glUniformMatrix4fv(handle, 1, false,
                    camera.getProjectionMatrix(), 0);

            uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
            handle = mCubeShader.getUniformLocation(uniformName);
            GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(),
                    0);
        }

        {
            mLightShader.useProgram();

            String uniformName = GLESShaderConstant.UNIFORM_PROJ_MATRIX;
            int handle = mLightShader.getUniformLocation(uniformName);
            GLES20.glUniformMatrix4fv(handle, 1, false,
                    camera.getProjectionMatrix(), 0);

            uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
            handle = mLightShader.getUniformLocation(uniformName);
            GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(),
                    0);
        }

        return camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 0.0f);

        createShader();

        {
            mCubeShader.useProgram();

            mCubeObject.setShader(mCubeShader);

            int program = mCubeShader.getProgram();
            mCubeNormalMatrixHandle = GLES20.glGetUniformLocation(program,
                    "uNormalMatrix");
            mCubeLightPosHandle = GLES20.glGetUniformLocation(program,
                    "uLightPos");
        }

        {
            mLightShader.useProgram();

            mLightObject.setShader(mLightShader);

            int program = mLightShader.getProgram();
            mLightNormalMatrixHandle = GLES20.glGetUniformLocation(program,
                    "uNormalMatrix");
            mLightLightPosHandle = GLES20
                    .glGetUniformLocation(program, "uLightPos");
        }
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
        if (mCubeObject != null) {
            mCubeObject.show();
        }

        if (mLightObject != null) {
            mLightObject.show();
        }
    }

    public void hideAll() {
        if (mCubeObject != null) {
            mCubeObject.hide();
        }

        if (mLightObject != null) {
            mLightObject.hide();
        }
    }

    private boolean createShader() {
        Log.d(TAG, "createShader()");

        createCubeShader();
        createLightShader();

        return true;
    }

    private void createCubeShader() {
        mCubeShader = new GLESShader(mContext);

        String vsSource = EffectUtils.getShaderSource(mContext, 0);
        String fsSource = EffectUtils.getShaderSource(mContext, 1);

        mCubeShader.setShaderSource(vsSource, fsSource);
        if (mCubeShader.load() == false) {
            mHandler.sendEmptyMessage(EffectRenderer.COMPILE_OR_LINK_ERROR);
            return;
        }

        if (mVersion == Version.GLES_20) {
            String attribName = GLESShaderConstant.ATTRIB_POSITION;
            mCubeShader.setVertexAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_NORMAL;
            mCubeShader.setNormalAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_COLOR;
            mCubeShader.setColorAttribIndex(attribName);
        }
    }

    private void createLightShader() {
        mLightShader = new GLESShader(mContext);

        String vsSource = EffectUtils.getShaderSource(mContext, 2);
        String fsSource = EffectUtils.getShaderSource(mContext, 3);

        mLightShader.setShaderSource(vsSource, fsSource);
        if (mLightShader.load() == false) {
            mHandler.sendEmptyMessage(EffectRenderer.COMPILE_OR_LINK_ERROR);
            return;
        }

        if (mVersion == Version.GLES_20) {
            String attribName = GLESShaderConstant.ATTRIB_POSITION;
            mLightShader.setVertexAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_NORMAL;
            mLightShader.setNormalAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_COLOR;
            mLightShader.setColorAttribIndex(attribName);
        }
    }
}
