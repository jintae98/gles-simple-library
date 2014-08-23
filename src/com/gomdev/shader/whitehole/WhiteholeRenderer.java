package com.gomdev.shader.whitehole;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.gomdev.shader.R;
import com.gomdev.gles.*;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESObject.PrimitiveMode;
import com.gomdev.gles.GLESObject.RenderType;
import com.gomdev.shader.EffectRenderer;
import com.gomdev.shader.EffectUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class WhiteholeRenderer extends EffectRenderer implements Renderer {
    private static final String CLASS = "WhiteholeRenderer";
    private static final String TAG = WhiteholeConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = WhiteholeConfig.DEBUG;

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

    public WhiteholeRenderer(Context context) {
        super(context);

        mWhiteholeObject = new WhiteholeObject();
        mWhiteholeObject.setTransform(new GLESTransform());
        mWhiteholeObject.setPrimitiveMode(PrimitiveMode.TRIANGLES);
        mWhiteholeObject.setRenderType(RenderType.DRAW_ELEMENTS);

        GLESGLState state = new GLESGLState();
        state.setCullFaceState(true);
        state.setCullFace(GLES20.GL_BACK);
        state.setDepthState(true);
        state.setDepthFunc(GLES20.GL_LEQUAL);
        mWhiteholeObject.setGLState(state);

        mRenderer.addObject(mWhiteholeObject);

    }

    public void destroy() {
        mWhiteholeObject = null;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (DEBUG)
            Log.d(TAG, "onDrawFrame()");

        super.updateFPS();

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

        mRenderer.updateObjects();
        mRenderer.drawObjects();

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

        mWhiteholeObject.setCamera(camera);
        mWhiteholeObject.setScreenSize(width, height);
        mWhiteholeObject.show();

        GLESVertexInfo vertexInfo = GLESMeshUtils.createPlaneMesh(mWidth,
                mHeight, WhiteholeConfig.MESH_RESOLUTION, true, false);
        mWhiteholeObject.setVertexInfo(vertexInfo, true, true);
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

        String uniformName = GLESShaderConstant.UNIFORM_PROJ_MATRIX;
        int handle = mShaderWhitehole.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false,
                camera.getProjectionMatrix(), 0);

        uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
        handle = mShaderWhitehole.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(), 0);

        return camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 0.0f);

        createShader();

        createAnimation();

        mWhiteholeObject.setShader(mShaderWhitehole);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.galaxy);
        mWhiteholeTexture = new GLESTexture(bitmap, GLES20.GL_MIRRORED_REPEAT);
        bitmap.recycle();
        mWhiteholeObject.setTexture(mWhiteholeTexture);

        mMinRingSize = GLESUtils.getPixelFromDpi(mContext,
                WhiteholeConfig.MIN_RING_SIZE);
        mMaxRingSize = (float) Math.hypot(GLESUtils.getWidthPixels(mContext),
                GLESUtils.getHeightPixels(mContext));
        mBoundaryRingSize = GLESUtils.getPixelFromDpi(mContext,
                WhiteholeConfig.BOUNDARY_RING_SIZE);
    }

    public void touchDown(float x, float y) {
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

    public void touchUp(float x, float y) {
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

    public void touchMove(float x, float y) {
        if (mIsTouchDown == false) {
            return;
        }

        mRadius = (float) Math.hypot((x - mDownX), (y - mDownY)) + mMinRingSize;
        mWhiteholeObject.setRadius(mRadius);

        mView.requestRender();
    }

    public void touchCancel(float x, float y) {

    }

    public void showAll() {
        if (mWhiteholeObject != null) {
            mWhiteholeObject.show();
        }
    }

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

        String vsSource = EffectUtils.getShaderSource(mContext, 0);
        String fsSource = EffectUtils.getShaderSource(mContext, 1);

        mShaderWhitehole.setShaderSource(vsSource, fsSource);
        if (mShaderWhitehole.load() == false) {
            mHandler.sendEmptyMessage(COMPILE_OR_LINK_ERROR);
            return false;
        }

        if (GLESConfig.GLES_VERSION == Version.GLES_20) {
            String attribName = GLESShaderConstant.ATTRIB_POSITION;
            mShaderWhitehole.setVertexAttribIndex(attribName);

            attribName = GLESShaderConstant.ATTRIB_TEXCOORD;
            mShaderWhitehole.setTexCoordAttribIndex(attribName);
        }

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
}
