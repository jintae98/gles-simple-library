package com.gomdev.shader.multiLighting;

import com.gomdev.gles.*;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESObject.PrimitiveMode;
import com.gomdev.gles.GLESObject.RenderType;
import com.gomdev.shader.EffectRenderer;
import com.gomdev.shader.EffectUtils;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class MultiLightingRenderer extends EffectRenderer {
    private static final String CLASS = "PVLRenderer";
    private static final String TAG = MultiLightingConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = MultiLightingConfig.DEBUG;

    private Version mVersion;

    private GLESSceneManager mSM = null;
    private GLESShader mShader = null;

    private GLESObject mCube = null;
    private GLESObject mLight = null;
    private GLESObject mLight2 = null;
    private GLESNode mNode = null;

    private boolean mIsTouchDown = false;

    private float mDownX = 0f;
    private float mDownY = 0f;

    private float mMoveX = 0f;
    private float mMoveY = 0f;

    private float mScreenRatio = 0f;

    private int mNormalMatrixHandle = -1;
    private int mLightPosHandle = -1;
    private int mLightStateHandle = -1;

    private float mRadius = 0f;
    private float mRadius2 = 0f;

    private GLESVector4 mLightPos = new GLESVector4();
    private GLESVector4 mLight2Pos = new GLESVector4();

    private float mDegree = 0f;

    public MultiLightingRenderer(Context context) {
        super(context);

        mVersion = GLESContext.getInstance().getVersion();

        mSM = GLESSceneManager.createSceneManager();
        GLESNode root = mSM.createRootNode("Root");

        GLESGLState state = new GLESGLState();
        state.setCullFaceState(true);
        state.setCullFace(GLES20.GL_BACK);
        state.setDepthState(true);
        state.setDepthFunc(GLES20.GL_LEQUAL);

        {
            mCube = mSM.createObject("Cube");
            mCube.setPrimitiveMode(PrimitiveMode.TRIANGLES);
            mCube.setRenderType(RenderType.DRAW_ELEMENTS);
            mCube.setGLState(state);
            mCube.setListener(mCubeListener);

            root.addChild(mCube);
        }

        {
            mNode = mSM.createNode("Node");
            mNode.setListener(mNodeListener);

            root.addChild(mNode);

            {
                mLight = mSM.createObject("Light");
                mLight.setPrimitiveMode(PrimitiveMode.TRIANGLES);
                mLight.setRenderType(RenderType.DRAW_ELEMENTS);
                mLight.setGLState(state);
                mLight.setListener(mLightListener);

                mNode.addChild(mLight);
            }

            {
                mLight2 = mSM.createObject("Light2");
                mLight2.setPrimitiveMode(PrimitiveMode.TRIANGLES);
                mLight2.setRenderType(RenderType.DRAW_ELEMENTS);
                mLight2.setGLState(state);
                mLight2.setListener(mLight2Listener);

                mNode.addChild(mLight2);
            }
        }

        mAnimator.setDuration(0, 10000);
        mAnimator.setRepeat(true);
    }

    public void destroy() {
        mCube = null;
        mLight = null;
        mLight2 = null;
    }

    @Override
    protected void onDrawFrame() {
        if (DEBUG)
            Log.d(TAG, "onDrawFrame()");

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        super.updateFPS();

        if (mAnimator.doAnimation() == true) {
            mView.requestRender();
        }

        mRenderer.updateScene(mSM);
        mRenderer.drawScene(mSM);
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        mScreenRatio = (float) width / height;
        mRadius = mScreenRatio;
        mRadius2 = mScreenRatio * 0.3f;

        mRenderer.reset();

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        {
            mCube.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils.createCube(
                    mScreenRatio * 0.5f, true, false, true);
            mCube.setVertexInfo(vertexInfo, true, true);
        }

        {
            mLight.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils.createSphere(0.1f, 10,
                    10, false, true, true, 1f, 0f, 0f, 1f);
            mLight.setVertexInfo(vertexInfo, true, false);
        }

        {
            mLight2.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils.createSphere(0.04f, 10,
                    10, false, true, true, 0, 1f, 0f, 1f);
            mLight2.setVertexInfo(vertexInfo, true, false);
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
            mShader.useProgram();

            String uniformName = GLESShaderConstant.UNIFORM_PROJ_MATRIX;
            int handle = mShader.getUniformLocation(uniformName);
            GLES20.glUniformMatrix4fv(handle, 1, false,
                    camera.getProjectionMatrix(), 0);

            uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
            handle = mShader.getUniformLocation(uniformName);
            GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(),
                    0);
        }

        return camera;
    }

    @Override
    protected void onSurfaceCreated() {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 0.0f);

        mShader.useProgram();

        mCube.setShader(mShader);
        mLight.setShader(mShader);
        mLight2.setShader(mShader);

        int program = mShader.getProgram();
        mNormalMatrixHandle = GLES20.glGetUniformLocation(program,
                "uNormalMatrix");

        mLightPosHandle = GLES20.glGetUniformLocation(program, "uLightPos");
        mLightStateHandle = GLES20.glGetUniformLocation(program, "uLightState");
    }

    @Override
    protected boolean createShader() {
        Log.d(TAG, "createShader()");

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

            attribName = GLESShaderConstant.ATTRIB_NORMAL;
            mShader.setNormalAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_COLOR;
            mShader.setColorAttribIndex(attribName);
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

    private GLESAnimator mAnimator = new GLESAnimator(
            new GLESAnimatorCallback() {

                @Override
                public void onFinished() {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onAnimation(GLESVector3 vector) {
                    mDegree = (float) Math.toDegrees(vector.mX);

                    float x = (float) Math.cos(vector.mX) * mRadius;
                    float y = (float) Math.sin(vector.mX) * mRadius;
                    float z = 0f;
                    float w = 1f;

                    mLightPos.set(x, y, z, w);

                    x = (float) Math.cos(vector.mX * 2f) * mRadius2 + x;
                    y = (float) Math.sin(vector.mX * 2f) * mRadius2 + y;
                    z = 0f;
                    w = 1f;

                    mLight2Pos.set(x, y, z, w);
                }
            });

    private GLESObjectListener mCubeListener = new GLESObjectListener() {

        @Override
        public void update(GLESObject object) {
            GLESTransform transform = object.getTransform();

            transform.setIdentity();
            transform.setRotate(mMoveX * 0.2f, 0f, 1f, 0f);
            transform.rotate(mMoveY * 0.2f, 1f, 0f, 0f);

        }

        @Override
        public void apply(GLESObject object) {
            GLESShader shader = object.getShader();
            GLESTransform transform = object.getTransform();

            GLESCamera camera = object.getCamera();
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

            shader.useProgram();

            GLES20.glUniformMatrix3fv(mNormalMatrixHandle, 1, false,
                    normalMatrix, 0);

            float[] lightPos = new float[] {
                    mLightPos.mX,
                    mLightPos.mY,
                    mLightPos.mZ,
                    mLightPos.mW,
                    mLight2Pos.mX,
                    mLight2Pos.mY,
                    mLight2Pos.mZ,
                    mLight2Pos.mW,
            };
            GLES20.glUniform4fv(mLightPosHandle, 2, lightPos, 0);

            int[] lightState = new int[] {
                    1,
                    1,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            };
            GLES20.glUniform1iv(mLightStateHandle, 8, lightState, 0);
        }
    };

    GLESObjectListener mLightListener = new GLESObjectListener() {

        @Override
        public void update(GLESObject object) {
        }

        @Override
        public void apply(GLESObject object) {
            GLESShader shader = object.getShader();
            GLESTransform transform = object.getWorldTransform();
            GLESCamera camera = object.getCamera();

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

            shader.useProgram();

            GLES20.glUniformMatrix3fv(mNormalMatrixHandle, 1, false,
                    normalMatrix, 0);

            float[] lightPos = new float[] {
                    mLightPos.mX,
                    mLightPos.mY,
                    mLightPos.mZ,
                    mLightPos.mW,
                    mLight2Pos.mX,
                    mLight2Pos.mY,
                    mLight2Pos.mZ,
                    mLight2Pos.mW,
            };
            GLES20.glUniform4fv(mLightPosHandle, 2, lightPos, 0);

            int[] lightState = new int[] {
                    0,
                    1,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            };
            GLES20.glUniform1iv(mLightStateHandle, 8, lightState, 0);
        }
    };

    GLESObjectListener mLight2Listener = new GLESObjectListener() {

        @Override
        public void update(GLESObject object) {
            GLESTransform transform = object.getTransform();

            transform.setIdentity();
            transform.setPreTranslate(mRadius2, 0f, 0f);
            transform.setRotate(mDegree, 0f, 0f, 1f);

        }

        @Override
        public void apply(GLESObject object) {
            GLESShader shader = object.getShader();
            GLESTransform transform = object.getWorldTransform();
            GLESCamera camera = object.getCamera();

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

            shader.useProgram();

            GLES20.glUniformMatrix3fv(mNormalMatrixHandle, 1, false,
                    normalMatrix, 0);

            float[] lightPos = new float[] {
                    mLightPos.mX,
                    mLightPos.mY,
                    mLightPos.mZ,
                    mLightPos.mW,
                    mLight2Pos.mX,
                    mLight2Pos.mY,
                    mLight2Pos.mZ,
                    mLight2Pos.mW
            };
            GLES20.glUniform4fv(mLightPosHandle, 2, lightPos, 0);

            int[] lightState = new int[] {
                    1,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            };

            GLES20.glUniform1iv(mLightStateHandle, 8, lightState, 0);
        }
    };

    private GLESNodeListener mNodeListener = new GLESNodeListener() {

        @Override
        public void update(GLESNode node) {
            GLESTransform transform = node.getTransform();

            transform.setIdentity();
            transform.setRotate(mDegree, 0f, 0f, 1f);
            transform.setPreTranslate(mRadius, 0f, 0f);
        }
    };
}