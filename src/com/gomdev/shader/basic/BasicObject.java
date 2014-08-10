package com.gomdev.shader.basic;

import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESObject;
import com.gomdev.gles.GLESCamera;
import com.gomdev.gles.GLESUtils;

public class BasicObject extends GLESObject {
    private static final String CLASS = "BasicObject";
    private static final String TAG = BasicConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = BasicConfig.DEBUG;

    private boolean mIsImageChanged;
    private Bitmap mBitmap;

    public BasicObject(Context context) {
        super(context);
    }

    @Override
    protected void update() {
        checkImageChanged();
    }

    @Override
    protected void draw() {
        if (DEBUG)
            Log.d(TAG, "draw()");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture.getTextureID());

        if (GLES20.glIsTexture(mTexture.getTextureID()) == false) {
            if (DEBUG)
                Log.d(TAG, "draw() mTexture is invalid");
        }

        GLES20.glEnableVertexAttribArray(mShader.getVertexAttribIndex());
        GLES20.glVertexAttribPointer(mShader.getVertexAttribIndex(),
                GLESConfig.NUM_OF_VERTEX_ELEMENT, GLES20.GL_FLOAT, false,
                GLESConfig.NUM_OF_VERTEX_ELEMENT * GLESConfig.FLOAT_SIZE_BYTES,
                mVertexInfo.getVertexBuffer().position(0));

        if (mVertexInfo.isUseTexCoord() == true) {
            GLES20.glEnableVertexAttribArray(mShader.getTexCoordAttribIndex());
            GLES20.glVertexAttribPointer(mShader.getTexCoordAttribIndex(),
                    GLESConfig.NUM_OF_TEXCOORD_ELEMENT, GLES20.GL_FLOAT, false,
                    GLESConfig.NUM_OF_TEXCOORD_ELEMENT
                            * GLESConfig.FLOAT_SIZE_BYTES, mVertexInfo
                            .getTexCoordBuffer().position(0));
        }

        int numOfVertex = mVertexInfo.getVertexBuffer().limit() / 3;
        int numOfRect = numOfVertex / 4;
//        for (int i = 0; i < numOfRect; i++) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//        }

        GLES20.glDisableVertexAttribArray(mShader.getVertexAttribIndex());
    }

    @Override
    protected void getUniformLocations() {
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        mIsImageChanged = true;
    }

    private void checkImageChanged() {
        if (mIsImageChanged == true) {
            mTexture.changeTexture(mBitmap, false);
            mBitmap = null;
            mIsImageChanged = false;
        }
    }

}
