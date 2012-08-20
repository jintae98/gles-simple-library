package com.gomdev.gles;

import static android.opengl.GLES20.glUniformMatrix4fv;

import java.util.Vector;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLESTransform {
	private static final String TAG = "gomdev GLESTransform";
	private static final boolean DEBUG = GLESConfig.DEBUG;
	
	private GLESShader			mShader;
	
	private float[] 			mMMatrix = new float[16];
	
	
	private int 				mMMatrixHandle = -1;
	
	private Vector<float[]> 	mMatrixStack = new Vector<float[]>();
	
	public GLESTransform(GLESShader shader)
	{
		mShader = shader;
		
		mMatrixStack.clear();
		
		init();
	}
	
	public void setIdentity()
	{
		Matrix.setIdentityM(mMMatrix, 0);
	}
	
	public void translate(float x, float y, float z)
	{
		Matrix.translateM(mMMatrix, 0, x, y, z);
	}
	
	public void rotate(float angle, float x, float y, float z)
	{
		Matrix.rotateM(mMMatrix, 0, angle, x, y, z);
	}
	
	public void scale(float x, float y, float z)
	{
		Matrix.scaleM(mMMatrix, 0, x, y, z);
	}
	
	public int getTransformHandle()
	{
		return mMMatrixHandle;
	}
	
	public void enableAlphaBlending(boolean disableDepthTest)
	{
		GLES20.glEnable(GLES20.GL_BLEND);
//		GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, 
//						GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, 
				GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		if(disableDepthTest == true)
		{
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
	}
	
	public void enableAlphaBlending(int srcColor, int dstColor, int srcAlpha, int dstAlpha, boolean disableDepthTest)
	{
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
		
		if(disableDepthTest == true)
		{
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
	}
	
	public void disableAlphaBlending()
	{
		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}
	
	/*
	 * this function should be called from GL thread.
	 * send model view matrix to shader.
	 */
	public void sync()
	{
		mShader.useProgram();
		
		glUniformMatrix4fv(mMMatrixHandle, 1, false, mMMatrix, 0);
	}
	
	public void push()
	{
		float[] matrix = new float[16];
		System.arraycopy(mMMatrix, 0, matrix, 0, matrix.length);
		
		mMatrixStack.add(matrix);
	}
	
	public void pop()
	{
		int lastIndex = mMatrixStack.size() - 1;
		mMMatrix = mMatrixStack.remove(lastIndex);
	}
	
	public float[] getCurrentMatrix()
	{
		float[] matrix = new float[16];
		System.arraycopy(mMMatrix, 0, matrix, 0, matrix.length);
		
		return matrix;
	}
	
	public void setMatrix(float[] matrix)
	{
		System.arraycopy(matrix, 0, mMMatrix, 0, matrix.length);
	}
	
	private void init()
	{
		mMMatrixHandle = mShader.getUniformLocation("uMMatrix");
		Matrix.setIdentityM(mMMatrix, 0);
		GLES20.glUniformMatrix4fv(mMMatrixHandle, 1, false, mMMatrix, 0);
	}
}
