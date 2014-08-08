package com.gomdev.effect.whitehole;

import java.io.File;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gomdev.effect.EffectConfig;
import com.gomdev.effect.EffectUtils;
import com.gomdev.effect.R;
import com.gomdev.gles.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class WhiteholeRenderer implements GLESRenderer {
    private static final String CLASS = "WhiteholeRenderer";
    private static final String TAG = WhiteholeConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = WhiteholeConfig.DEBUG;
    private static final boolean DEBUG_PERF = WhiteholeConfig.DEBUG_PERF;

    private static final int COMPILE_OR_LINK_ERROR = 1;

    static {
        System.loadLibrary("gomdev");
    }

    private Context mContext;
    private GLESSurfaceView mView;

    private WhiteholeObject mWhiteholeObject;
    private GLESTexture mWhiteholeTexture;

    private int mWidth;
    private int mHeight;
    private GLESShader mShaderWhitehole;

    private boolean mIsTouchDown = false;
    private float mDownX = 0f;
    private float mDownY = 0f;

    private GLESAnimatorCallback mCallback = null;

    ArrayList<GLESAnimator> mAnimatorList = new ArrayList<GLESAnimator>();
    private GLESAnimator mAnimator = null;
    private float mRadius = 0f;
    private float mMinRingSize = 0f;
    private float mMaxRingSize = 0.0f;
    private float mBoundaryRingSize = 0f;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPILE_OR_LINK_ERROR) {
                Toast.makeText(mContext, "Compile or Link fails",
                        Toast.LENGTH_SHORT).show();
            }
        }

    };

    public WhiteholeRenderer(Context context) {
        mContext = context;

        mWhiteholeObject = new WhiteholeObject(context);

    }

    public void destroy() {
        mWhiteholeObject = null;
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

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mWhiteholeObject.drawObject();

        if (count > 0) {
            mView.requestRender();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mWidth = width;
        mHeight = height;

        if (DEBUG)
            Log.d(TAG, "onSurfaceChanged()");

        GLES20.glViewport(0, 0, width, height);

        GLESCamera camera = setupCamera(width, height);

        mWhiteholeObject.setupSpace(camera, mWidth, mHeight);
        mWhiteholeObject.show();

        GLESVertexInfo vertexInfo = GLESMeshUtils.createPlaneMesh(mWidth,
                mHeight, WhiteholeConfig.MESH_RESOLUTION, true, false);
        mWhiteholeObject.setVertexInfo(vertexInfo);
    }

    private GLESCamera setupCamera(int width, int height) {
        GLESCamera camera = new GLESCamera();
        camera.setLookAt(0f, 0f, 64f, 0f, 0f, 0f, 0f, 1f, 0f);

        float right = width * 0.5f / 4f;
        float left = -right;
        float top = height * 0.5f / 4f;
        float bottom = -top;
        float near = 16f;
        float far = 256f;

        camera.setFrustum(left, right, bottom, top, near, far);

        int handle = mShaderWhitehole.getUniformLocation("uPMatrix");
        GLES20.glUniformMatrix4fv(handle, 1, false,
                camera.getProjectionMatrix(), 0);

        handle = mShaderWhitehole.getUniformLocation("uVMatrix");
        GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(), 0);

        return camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        createShader();

        createAnimation();

        mWhiteholeObject.setShader(mShaderWhitehole);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.galaxy);
        // Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
        // R.drawable.moon);
        mWhiteholeTexture = new GLESTexture(bitmap, GLES20.GL_MIRRORED_REPEAT,
                true);
        mWhiteholeObject.setTexture(mWhiteholeTexture);

        mMinRingSize = GLESUtils.getPixelFromDpi(mContext,
                WhiteholeConfig.MIN_RING_SIZE);
        mMaxRingSize = (float) Math.hypot(GLESUtils.getWidthPixels(mContext),
                GLESUtils.getHeightPixels(mContext));
        mBoundaryRingSize = GLESUtils.getPixelFromDpi(mContext,
                WhiteholeConfig.BOUNDARY_RING_SIZE);
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

        mWhiteholeObject.setPosition(x, y);
        mWhiteholeObject.setRadius(mMinRingSize);

        mView.requestRender();
    }

    @Override
    public void touchUp(float x, float y, float userData) {
        if (mIsTouchDown == false) {
            return;
        }

        mRadius = (float) Math.hypot((x - mDownX), (y - mDownY)) + mMinRingSize;

        if (mRadius > mBoundaryRingSize) {
            mAnimator.setDuration(0L, 500L);
            mAnimator.start(mRadius, mMaxRingSize);
        } else {
            mAnimator.setDuration(0L, 500L);
            mAnimator.start(mRadius, 0f);
        }

        mView.requestRender();

        mIsTouchDown = false;
    }

    @Override
    public void touchMove(float x, float y, float userData) {
        if (mIsTouchDown == false) {
            return;
        }

        mRadius = (float) Math.hypot((x - mDownX), (y - mDownY)) + mMinRingSize;
        mWhiteholeObject.setRadius(mRadius);

        mView.requestRender();
    }

    @Override
    public void touchCancel(float x, float y) {

    }

    @Override
    public void showAll() {
        if (mWhiteholeObject != null) {
            mWhiteholeObject.show();
        }
    }

    @Override
    public void hideAll() {
        if (mWhiteholeObject != null) {
            mWhiteholeObject.hide();
        }
    }

    public void setImage(Bitmap bitmap) {
        if (mWhiteholeObject != null) {
            mWhiteholeObject.setImage(bitmap);
        }
    }

    private boolean createShader() {
        Log.d(TAG, "createShader()");
        mShaderWhitehole = new GLESShader(mContext);

        SharedPreferences pref = mContext.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);
        String effectName = pref.getString(EffectConfig.PREF_EFFECT_NAME,
                "Whiltehole");
        String shaderType = pref.getString(EffectConfig.PREF_SHADER_TYPE,
                EffectConfig.SHADER_TYPE_VS);

        String vsFilePath = EffectUtils.getSavedFilePath(mContext, "Whitehole",
                EffectConfig.SHADER_TYPE_VS);
        String vertexShaderSource = null;
        File file = new File(vsFilePath);
        if (file.exists() == true) {
            vertexShaderSource = GLESFileUtils.read(vsFilePath);
        } else {
            vertexShaderSource = GLESUtils.getStringFromReosurce(mContext,
                    R.raw.whitehole_vs);
        }

        String fsFilePath = EffectUtils.getSavedFilePath(mContext, "Whitehole",
                EffectConfig.SHADER_TYPE_FS);
        String fragmentShaderSource = null;
        file = new File(fsFilePath);
        if (file.exists() == true) {
            fragmentShaderSource = GLESFileUtils.read(fsFilePath);
        } else {
            fragmentShaderSource = GLESUtils.getStringFromReosurce(mContext,
                    R.raw.whitehole_fs);
        }

        mShaderWhitehole.setShaderSource(vertexShaderSource,
                fragmentShaderSource);
        if (mShaderWhitehole.load() == false) {
            mHandler.sendEmptyMessage(COMPILE_OR_LINK_ERROR);
            return false;
        }

        mShaderWhitehole.setVertexAttribIndex("aPosition");
        mShaderWhitehole.setTexCoordAttribIndex("aTexCoord");

        return true;
    }

    private void createAnimation() {
        mCallback = new GLESAnimatorCallback() {

            @Override
            public void onAnimation(GLESVector currentValue) {
                mRadius = currentValue.mX;
                mWhiteholeObject.setRadius(mRadius);
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
