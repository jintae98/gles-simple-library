package com.gomdev.shader.perVertexLighting;

import java.nio.FloatBuffer;

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
import android.opengl.GLES30;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;

public class PVLRenderer extends EffectRenderer implements Renderer {
    private static final String CLASS = "PVLRenderer";
    private static final String TAG = PVLConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = PVLConfig.DEBUG;

    private final float[] LIGHT_INFO = new float[] {
            0.3f, 0.3f, 0.3f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            1.0f, 1.0f, 1.0f, 16.0f,
            16f
    };

    private final int AMBIENT_OFFSET = 0;
    private final int DIFFUSE_OFFSET = 4;
    private final int SPECULAR_OFFSET = 8;
    private final int SPECULAR_EXPONENT_OFFSET = 12;

    private GLESObject mCubeObject;
    private GLESObject mLightObject;
    private GLESShader mShader;
    private Version mVersion;

    private boolean mIsTouchDown = false;

    private float mDownX = 0f;
    private float mDownY = 0f;

    private float mMoveX = 0f;
    private float mMoveY = 0f;

    private float mScreenRatio = 0f;

    private int mNormalMatrixHandle = -1;
    private int mLightPosHandle = -1;

    private FloatBuffer mLightInfoBuffer = null;

    private GLESVector4 mCubeLight = new GLESVector4();
    private float mRadius = 0f;

    private GLESVector4 mLightLight = new GLESVector4(0f, 0f, 0f, 1f);

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
            mCubeObject.setListener(mCubeObjectListener);

            mRenderer.addObject(mCubeObject);
        }

        {
            mLightObject = GLESSceneManager.createObject();
            mLightObject.setTransform(new GLESTransform());
            mLightObject.setPrimitiveMode(PrimitiveMode.TRIANGLES);
            mLightObject.setRenderType(RenderType.DRAW_ELEMENTS);
            mLightObject.setGLState(state);
            mLightObject.setListener(mLightObjectListener);

            mRenderer.addObject(mLightObject);
        }

        mAnimator.setDuration(0, 10000);
        mAnimator.setRepeat(true);
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

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mRenderer.updateObjects();
        mRenderer.drawObjects();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        mScreenRatio = (float) width / height;
        mRadius = mScreenRatio;

        mRenderer.reset();

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        {
            mCubeObject.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils.createCube(
                    mScreenRatio * 0.5f, true, false, true);
            mCubeObject.setVertexInfo(vertexInfo, true, true);
        }

        {
            mLightObject.setCamera(camera);

            GLESVertexInfo vertexInfo = GLESMeshUtils.createSphere(0.1f, 10,
                    10, false, true, true);
            mLightObject.setVertexInfo(vertexInfo, true, false);
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
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 0.0f);

        createShader();

        mShader.useProgram();

        mCubeObject.setShader(mShader);
        mLightObject.setShader(mShader);

        int program = mShader.getProgram();
        mNormalMatrixHandle = GLES20.glGetUniformLocation(program,
                "uNormalMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(program,
                "uLightPos");

        if (mVersion == Version.GLES_20) {
            int location = GLES20
                    .glGetUniformLocation(program, "uAmbientColor");
            GLES20.glUniform4fv(location, 1, LIGHT_INFO, AMBIENT_OFFSET);

            location = GLES20.glGetUniformLocation(program, "uDiffuseColor");
            GLES20.glUniform4fv(location, 1, LIGHT_INFO, DIFFUSE_OFFSET);

            location = GLES20.glGetUniformLocation(program, "uSpecularColor");
            GLES20.glUniform4fv(location, 1, LIGHT_INFO, SPECULAR_OFFSET);

            location = GLES20
                    .glGetUniformLocation(program, "uSpecularExponent");
            GLES20.glUniform1f(location, LIGHT_INFO[SPECULAR_EXPONENT_OFFSET]);
        } else {
            int location = GLES20
                    .glGetUniformLocation(program, "uSpecularExponent");
            GLES20.glUniform1f(location, LIGHT_INFO[SPECULAR_EXPONENT_OFFSET]);

            updateUniformBuffer();
        }
    }

    private void updateUniformBuffer() {

        mLightInfoBuffer = GLESUtils.makeFloatBuffer(LIGHT_INFO);

        int bindingPoint = 1;
        int blockSize = -1;
        int uBufferID = -1;
        int program = mShader.getProgram();

        int location = GLES30.glGetUniformBlockIndex(program, "LightInfo");

        GLES30.glUniformBlockBinding(program, location, bindingPoint);

        int[] blockSizes = new int[1];
        GLES30.glGetActiveUniformBlockiv(program, location,
                GLES30.GL_UNIFORM_BLOCK_DATA_SIZE, blockSizes, 0);
        blockSize = blockSizes[0];

        String[] uniformNames = new String[] {
                "uAmbientColor",
                "uDiffuseColor",
                "uSpecularColor",
                // "uSpecularExponent"
        };
        int[] indices = new int[4];
        int[] offsets = new int[4];
        GLES30.glGetUniformIndices(program, uniformNames, indices, 0);
        GLES30.glGetActiveUniformsiv(program, 4, indices, 0,
                GLES30.GL_UNIFORM_OFFSET, offsets, 0);

        if (DEBUG) {
            Log.d(TAG, "updateUniformBuffer()");
            for (int i = 0; i < 4; i++) {
                Log.d(TAG, "\ti=" + i + " index=" + indices[i] + " offset="
                        + offsets[i]);
            }
        }

        int[] uniformBufIDs = new int[1];
        GLES30.glGenBuffers(1, uniformBufIDs, 0);
        GLES30.glBindBuffer(GLES30.GL_UNIFORM_BUFFER, uniformBufIDs[0]);
        uBufferID = uniformBufIDs[0];
        GLES30.glBufferData(GLES30.GL_UNIFORM_BUFFER,
                blockSize,
                mLightInfoBuffer,
                GLES30.GL_DYNAMIC_DRAW);
        GLES30.glBindBufferBase(GLES30.GL_UNIFORM_BUFFER, bindingPoint,
                uBufferID);
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

    private boolean createShader() {
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
                    float x = (float) (Math.cos(vector.mX) * mRadius);
                    float y = (float) (Math.sin(vector.mX) * mRadius);
                    float z = 0f;
                    float w = 0f;

                    mCubeLight.set(x, y, z, w);
                }
            });

    private GLESObjectListener mCubeObjectListener = new GLESObjectListener() {

        @Override
        public void update(GLESObject object) {
            GLESTransform transform = object.getTransform();

            transform.setIdentity();
            transform.rotate(mMoveX * 0.2f, 0f, 1f, 0f);
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

            GLES20.glUniform4f(mLightPosHandle,
                    mCubeLight.mX,
                    mCubeLight.mY,
                    mCubeLight.mZ,
                    mCubeLight.mW);

        }
    };

    GLESObjectListener mLightObjectListener = new GLESObjectListener() {

        @Override
        public void update(GLESObject object) {
            GLESTransform transform = object.getTransform();

            transform.setIdentity();
            transform.translate(mCubeLight.mX, mCubeLight.mY, mCubeLight.mZ);

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

            GLES20.glUniform4f(mLightPosHandle,
                    mLightLight.mX,
                    mLightLight.mY,
                    mLightLight.mZ,
                    mLightLight.mW);
        }
    };
}