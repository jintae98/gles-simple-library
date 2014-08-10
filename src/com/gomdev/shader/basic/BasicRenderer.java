package com.gomdev.shader.basic;

import java.io.File;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gomdev.shader.R;
import com.gomdev.gles.*;
import com.gomdev.shader.EffectConfig;
import com.gomdev.shader.EffectUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BasicRenderer implements GLESRenderer {
    private static final String CLASS = "BasicRenderer";
    private static final String TAG = BasicConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = BasicConfig.DEBUG;
    private static final boolean DEBUG_PERF = BasicConfig.DEBUG_PERF;

    private static final int COMPILE_OR_LINK_ERROR = 1;

    static {
        System.loadLibrary("gomdev");
    }

    private Context mContext;
    private GLESSurfaceView mView;

    private BasicObject mBasicObject;
    private GLESTexture mBasicTexture;
    private GLESShader mBasicShader;

    private int mWidth;
    private int mHeight;

    private boolean mIsTouchDown = false;

    private float mDownX = 0f;
    private float mDownY = 0f;

    private float mMoveX = 0f;
    private float mMoveY = 0f;

    private GLESAnimatorCallback mCallback = null;

    ArrayList<GLESAnimator> mAnimatorList = new ArrayList<GLESAnimator>();
    private GLESAnimator mAnimator = null;

    private int mMMatrixHandle = -1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPILE_OR_LINK_ERROR) {
                Toast.makeText(mContext, "Compile or Link fails",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    public BasicRenderer(Context context) {
        mContext = context;

        mBasicObject = new BasicObject(context);
        mBasicObject.setTransform(new GLESTransform());
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

        int count = 0;
        boolean needToRequestRender = false;

        for (GLESAnimator animator : mAnimatorList) {
            if (animator != null) {
                needToRequestRender = animator.doAnimation();

                if (needToRequestRender == true) {
                    count++;
                }
            }
        }

        update();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mBasicObject.drawObject();

        if (count > 0) {
            mView.requestRender();
        }
    }

    private void update() {
        GLESTransform transform = mBasicObject.getTransform();

        transform.setIdentity();

        transform.rotate(mMoveX * 0.1f, 0f, 1f, 0f);
        transform.rotate(mMoveY * 0.1f, 1f, 0f, 0f);

        GLES20.glUniformMatrix4fv(mMMatrixHandle, 1, false,
                transform.getMatrix(), 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        mBasicObject.setupSpace(camera, mWidth, mHeight);
        mBasicObject.show();

        GLESVertexInfo vertexInfo = GLESMeshUtils.createCube(mWidth,
                mWidth, true, true);
        mBasicObject.setVertexInfo(vertexInfo);
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

        int handle = mBasicShader.getUniformLocation("uPMatrix");
        GLES20.glUniformMatrix4fv(handle, 1, false,
                camera.getProjectionMatrix(), 0);

        handle = mBasicShader.getUniformLocation("uVMatrix");
        GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(), 0);

        return camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.7f, 0.7f, 0.7f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        createShader();

        createAnimation();

        mBasicObject.setShader(mBasicShader);
        Bitmap bitmap = GLESUtils.makeBitmap(512, 512, Config.ARGB_8888,
                Color.RED);
        mBasicTexture = new GLESTexture(bitmap, GLES20.GL_MIRRORED_REPEAT, true);
        mBasicObject.setTexture(mBasicTexture);

        mMMatrixHandle = mBasicShader.getUniformLocation("uMMatrix");
    }

    @Override
    public void initRenderer() {

    }

    @Override
    public void setSurfaceView(GLESSurfaceView surfaceView) {
        if (surfaceView == null) {
            Log.e(TAG, "setSurfaceView() surfaceView is null");
            return;
        }
        mView = surfaceView;
    }

    @Override
    public void touchDown(float x, float y, float userData) {
        if (DEBUG)
            Log.d(TAG, "touchDown() x=" + x + " y=" + y);

        mIsTouchDown = true;

        for (GLESAnimator animator : mAnimatorList) {
            if (animator != null) {
                animator.cancel();
            }
        }

        mDownX = x;
        mDownY = y;

        mView.requestRender();
    }

    @Override
    public void touchUp(float x, float y, float userData) {
        if (mIsTouchDown == false) {
            return;
        }

        mView.requestRender();

        mIsTouchDown = false;
    }

    @Override
    public void touchMove(float x, float y, float userData) {
        if (mIsTouchDown == false) {
            return;
        }

        mMoveX = x - mDownX;
        mMoveY = y - mDownY;

        mView.requestRender();
    }

    @Override
    public void touchCancel(float x, float y) {
    }

    @Override
    public void showAll() {
        if (mBasicObject != null) {
            mBasicObject.show();
        }
    }

    @Override
    public void hideAll() {
        if (mBasicObject != null) {
            mBasicObject.hide();
        }
    }

    public void setImage(Bitmap bitmap) {
        if (mBasicObject != null) {
            mBasicObject.setImage(bitmap);
        }
    }

    private boolean createShader() {
        Log.d(TAG, "createShader()");
        mBasicShader = new GLESShader(mContext);

        String vsFilePath = EffectUtils.getSavedFilePath(mContext,
                BasicConfig.EFFECT_NAME, EffectConfig.SHADER_TYPE_VS);
        String vertexShaderSource = null;
        File file = new File(vsFilePath);
        if (file.exists() == true) {
            vertexShaderSource = GLESFileUtils.read(vsFilePath);
        } else {
            vertexShaderSource = GLESUtils.getStringFromReosurce(mContext,
                    R.raw.whitehole_vs);
        }

        String fsFilePath = EffectUtils.getSavedFilePath(mContext,
                BasicConfig.EFFECT_NAME, EffectConfig.SHADER_TYPE_FS);
        String fragmentShaderSource = null;
        file = new File(fsFilePath);
        if (file.exists() == true) {
            fragmentShaderSource = GLESFileUtils.read(fsFilePath);
        } else {
            fragmentShaderSource = GLESUtils.getStringFromReosurce(mContext,
                    R.raw.whitehole_fs);
        }

        mBasicShader.setShaderSource(vertexShaderSource, fragmentShaderSource);
        if (mBasicShader.load() == false) {
            mHandler.sendEmptyMessage(COMPILE_OR_LINK_ERROR);
            return false;
        }

        mBasicShader.setVertexAttribIndex("aPosition");
        mBasicShader.setTexCoordAttribIndex("aTexCoord");

        return true;
    }

    private void createAnimation() {
        mCallback = new GLESAnimatorCallback() {

            @Override
            public void onAnimation(GLESVector currentValue) {
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        };

        mAnimator = new GLESAnimator(mCallback);
        mAnimatorList.add(mAnimator);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }
}
