package com.gomdev.gles;

import static android.opengl.GLES20.glUniformMatrix4fv;
import android.opengl.Matrix;
import android.util.Log;

import com.gomdev.gles.GLESConfig.ProjectionType;

public class GLESProjection {
	private static final String TAG = "gomdev GLESProjection";
	private static final boolean DEBUG = GLESConfig.DEBUG;
	
	private GLESShader 			mShader;
	private ProjectionType 		mProjectionType;
	
	private int 				mWidth;
	private int 				mHeight;
	
	private float				mProjectScale;
	
	private int 				mPMatrixHandle = -1;
	private int 				mVMatrixHandle = -1;
	
	private float[] 			mPMatrix = new float[16];
	private float[] 			mVMatrix = new float[16];
	
	public GLESProjection(GLESShader shader, ProjectionType projectionType, int width, int height)
	{
		this(shader, projectionType, width, height, 4.0f);
	}
	
	public GLESProjection(GLESShader shader, ProjectionType projectionType, int width, int height, float projScale)
	{
		mShader = shader;
		mProjectionType = projectionType;
		
		mWidth = width;
		mHeight = height;
		
		mProjectScale = projScale;
		
		buildProjection();
	}
	
	public ProjectionType getProjectionType()
	{
		return mProjectionType;
	}
	
	public float getProjectionScale()
	{
		return mProjectScale;
	}
	
	public void setFrustum(ProjectionType projType, float left, float right, float bottom, float top, float near, float far)
	{
		Matrix.setIdentityM(mPMatrix, 0);
		
		if(projType == ProjectionType.FRUSTUM)
		{
			Matrix.frustumM(mPMatrix, 0, left, right, bottom, top, near, far);
		}
		else
		{
			Matrix.orthoM(mPMatrix, 0, left, right, bottom, top, near, far);
		}
	}
	
	public void setProjectionMatrix(float[] matrix)
	{
		System.arraycopy(matrix, 0, mPMatrix, 0, matrix.length);
	}
	
	public float[] getProjectionMatrix()
	{
		float[] matrix = new float[16];
		System.arraycopy(mPMatrix, 0, matrix, 0, matrix.length);
		return matrix;
	}

	public void setViewMatrix(float x, float y, float z)
	{
		Matrix.setIdentityM(mVMatrix, 0);
		Matrix.translateM(mVMatrix, 0, x, y, z);
	}
	
	public void setViewMatrix(float[] matrix)
	{
		System.arraycopy(matrix, 0, mVMatrix, 0, matrix.length);
	}
	
	public float[] getViewMatrix()
	{
		float[] matrix = new float[16];
		System.arraycopy(mVMatrix, 0, matrix, 0, matrix.length);
		return matrix;
	}
	
	public void sync()
	{
		mShader.useProgram();
		
		glUniformMatrix4fv(mPMatrixHandle, 1, false, mPMatrix, 0);
    	glUniformMatrix4fv(mVMatrixHandle, 1, false, mVMatrix, 0);
	}
	
	private void buildProjection()
	{
		float right;
		float left;
		float bottom;
		float top;
		
		mShader.useProgram();
		
		mPMatrixHandle = mShader.getUniformLocation("uPMatrix");
		mVMatrixHandle = mShader.getUniformLocation("uVMatrix");
		
		if(mProjectionType == ProjectionType.ORTHO)
		{
			right = GLESUtils.convertScreenToSpace(mWidth * 0.5f);
			left = -right;
			top = GLESUtils.convertScreenToSpace(mHeight * 0.5f);
	    	bottom = -top;
	    	if(DEBUG) Log.d(TAG, "buildProjection() left=" + (left) + " right=" + (right) + " bottom=" + (bottom) + " top=" + (top));
	    	
			Matrix.orthoM(mPMatrix, 0, left, right, bottom, top, -100f, 100f);
	    	glUniformMatrix4fv(mPMatrixHandle, 1, false, mPMatrix, 0);
	    	
	    	Matrix.setIdentityM(mVMatrix, 0);
	    	glUniformMatrix4fv(mVMatrixHandle, 1, false, mVMatrix, 0);
		}
		else if(mProjectionType == ProjectionType.FRUSTUM)
		{
			right = GLESUtils.convertScreenToSpace(mWidth * 0.5f / mProjectScale);
			left = -right;
			top = GLESUtils.convertScreenToSpace(mHeight * 0.5f / mProjectScale);
	    	bottom = -top;
	    	if(DEBUG) Log.d(TAG, "buildProjection() left=" + (left) + " right=" + (right) + " bottom=" + (bottom) + " top=" + (top));
	    	
			Matrix.frustumM(mPMatrix, 0, left, right, bottom, top, 4f, 64f);
	    	glUniformMatrix4fv(mPMatrixHandle, 1, false, mPMatrix, 0);
	    	
	    	Matrix.setIdentityM(mVMatrix, 0);
	    	Matrix.translateM(mVMatrix, 0, 0f, 0f, -4.0f * mProjectScale);
	    	glUniformMatrix4fv(mVMatrixHandle, 1, false, mVMatrix, 0);
	    	
		}
		else
		{
			throw new IllegalArgumentException("buildProjection() invalid projection type");
		}
	}
}
