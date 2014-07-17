package com.gomdev.effect.whitehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESConfig.ObjectType;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESProjection;
import com.gomdev.gles.GLESUtils;

public class WhiteholeObject extends GLESObject {
    private static final String CLASS = "WhiteholeObject";
    private static final String TAG = WhiteholeConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = WhiteholeConfig.DEBUG;

    private float mX;
    private float mY;

    private int mNumOfVertics;
    private int mNumOfVertexElement;

    private boolean mIsImageChanged;
    private Bitmap mBitmap;

    private float mObjWidth;
    private float mObjHeight;

    private int mPositionInVSHandle = -1;
    private int mPositionInFSHandle = -1;
    private int mRadiusHandle = -1;

    private float[] mDownPosInVS = new float[2];
    private float[] mDownPosInFS = new float[2];
    private float mRadius = 100.0f;

    public WhiteholeObject(Context context, boolean useTexture,
            boolean useNormal) {
        super(context, useTexture, useNormal);

        init();
    }

    public WhiteholeObject(Context context, boolean useTexture,
            boolean useNormal, ObjectType objectType) {
        super(context, useTexture, useNormal, objectType);

        init();
    }

    @Override
    public void setupSpace(GLESProjection projection, int width, int height) {
        super.setupSpace(projection, width, height);

        int handle = mShader.getUniformLocation("uSpaceInfo"); // width, height,
                                                               // width / height
        GLES20.glUniform3f(handle, GLESUtils.convertScreenToSpace(width),
                GLESUtils.convertScreenToSpace(height), (float) width / height);
    }

    @Override
    protected void update() {
        checkImageChanged();

        GLES20.glUniform2f(mPositionInVSHandle, mDownPosInVS[0],
                mDownPosInVS[1]);
        GLES20.glUniform2f(mPositionInFSHandle, mDownPosInFS[0],
                mDownPosInFS[1]);
        GLES20.glUniform1f(mRadiusHandle,
                GLESUtils.convertScreenToSpace(mRadius));
    }

    @Override
    protected void draw() {
        if (DEBUG)
            Log.d(TAG, "draw()");

        GLES20.glViewport(0, 0, (int) mWidth, (int) mHeight);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.getTextureID());

        if (GLES20.glIsTexture(mTexture.getTextureID()) == false) {
            if (DEBUG)
                Log.d(TAG, "draw() mTexture is invalid");
        }

        if (GLESConfig.USE_VBO == true) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVBOIDs[0]);

            GLES20.glEnableVertexAttribArray(mShader.getVertexAttribIndex());
            GLES20.glVertexAttribPointer(mShader.getVertexAttribIndex(),
                    GLESConfig.NUM_OF_VERTEX_ELEMENT, GLES20.GL_FLOAT, false,
                    mNumOfVertexElement * GLESConfig.FLOAT_SIZE_BYTES, 0);

            if (mUseTexture == true) {
                GLES20.glEnableVertexAttribArray(mShader
                        .getTexCoordAttribIndex());
                GLES20.glVertexAttribPointer(mShader.getTexCoordAttribIndex(),
                        GLESConfig.NUM_OF_TEXCOORD_ELEMENT, GLES20.GL_FLOAT,
                        false, mNumOfVertexElement
                                * GLESConfig.FLOAT_SIZE_BYTES,
                        GLESConfig.NUM_OF_VERTEX_ELEMENT
                                * GLESConfig.FLOAT_SIZE_BYTES);
            }

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mVBOIDs[1]);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexBuffer.capacity(),
                    GLES20.GL_UNSIGNED_SHORT, 0);

            GLES20.glDisableVertexAttribArray(mShader.getVertexAttribIndex());
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        } else {
            GLES20.glEnableVertexAttribArray(mShader.getVertexAttribIndex());
            GLES20.glVertexAttribPointer(mShader.getVertexAttribIndex(),
                    GLESConfig.NUM_OF_VERTEX_ELEMENT, GLES20.GL_FLOAT, false,
                    mNumOfVertexElement * GLESConfig.FLOAT_SIZE_BYTES,
                    mVertexBuffer.position(0));

            if (mUseTexture == true) {
                GLES20.glEnableVertexAttribArray(mShader
                        .getTexCoordAttribIndex());
                GLES20.glVertexAttribPointer(mShader.getTexCoordAttribIndex(),
                        GLESConfig.NUM_OF_TEXCOORD_ELEMENT, GLES20.GL_FLOAT,
                        false, mNumOfVertexElement
                                * GLESConfig.FLOAT_SIZE_BYTES, mVertexBuffer
                                .position(GLESConfig.NUM_OF_VERTEX_ELEMENT));
            }

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexBuffer.capacity(),
                    GLES20.GL_UNSIGNED_SHORT, mIndexBuffer);

            GLES20.glDisableVertexAttribArray(mShader.getVertexAttribIndex());
        }
    }

    @Override
    protected void getUniformLocations() {
        mPositionInVSHandle = mShader.getUniformLocation("uTouchPosInVS");
        mPositionInFSHandle = mShader.getUniformLocation("uTouchPosInFS");

        mRadiusHandle = mShader.getUniformLocation("uRadius");
        GLES20.glUniform1f(mRadiusHandle, 0.1f);

        int handle = mShader.getUniformLocation("uBandWidth");
        GLES20.glUniform1f(handle, GLESUtils.convertScreenToSpace(GLESUtils
                .getPixelFromDpi(mContext, WhiteholeConfig.BAND_WIDTH)));
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        mIsImageChanged = true;
    }

    public void setPosition(float x, float y) {
        mDownPosInVS[0] = GLESUtils.convertScreenToSpace(x - mWidth * 0.5f);
        mDownPosInVS[1] = GLESUtils.convertScreenToSpace(mHeight * 0.5f - y);

        mDownPosInFS[0] = x / mWidth;
        mDownPosInFS[1] = y / mHeight;
    }

    public void setRadius(float radius) {
        Log.d(TAG, "setRadius() radius=" + radius);
        mRadius = radius;
    }

    private void init() {
        mNumOfVertexElement = GLESConfig.NUM_OF_VERTEX_ELEMENT;

        if (mUseNormal == true) {
            mNumOfVertexElement += GLESConfig.NUM_OF_NORMAL_ELEMENT;
        }

        if (mUseTexture == true) {
            mNumOfVertexElement += GLESConfig.NUM_OF_TEXCOORD_ELEMENT;
        }

    }

    private void checkImageChanged() {
        if (mIsImageChanged == true) {
            mTexture.changeTexture(mBitmap, false);
            mBitmap = null;
            mIsImageChanged = false;
        }
    }

}
