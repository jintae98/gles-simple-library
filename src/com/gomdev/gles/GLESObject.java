package com.gomdev.gles;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.gomdev.gles.GLESConfig.DepthLevel;
import com.gomdev.gles.GLESConfig.ProjectionType;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;


public abstract class GLESObject {
	private static final boolean DEBUG = GLESConfig.DEBUG;
	private static final String TAG = "gomdev GLESObject";
	
	protected Context			mContext;
	protected Resources			mRes;
	protected GLESShader		mShader;
	protected GLESTexture 		mTexture;
	protected GLESProjection	mProjection;
	protected GLESTransform		mTransform;
	
	protected float 			mWidth;
	protected float 			mHeight;
	
	protected int				mSpaceInfoHandle = -1;
	
	protected boolean 			mUseTexture = false;
	protected boolean 			mUseNormal = false;
	
	protected boolean			mIsVisible = false;
	
	protected DepthLevel 		mDepth = GLESConfig.DepthLevel.DEFAULT_LEVEL_DEPTH;
	
	public GLESObject(Context context, boolean useTexture, boolean useNormal)
	{
		mContext = context;
		mRes = context.getResources();
		
		mUseTexture = useTexture;
		mUseNormal = useNormal;
	}
	
	
	public void init(GLESShader shader)
	{
		if(DEBUG) Log.d(TAG, "init() shader=" + shader);
		
		mShader = shader;
		shader.useProgram();
		
		shader.setVertexAttribIndex("aPosition");
		
		if(mUseTexture == true)
		{
			shader.setTexCoordAttribIndex("aTexCoord");
		}
		
		if(mUseNormal == true)
		{
			shader.setNormalAttribIndex("aNormal");
		}
		
		getUniformLocations();
		
		mTransform = new GLESTransform(shader);
	}
	
	public void setupSpace(GLESProjection projection, int width, int height)
	{
		mProjection = projection;
		
		mWidth = width;
		mHeight = height;
	}
	
	public void setupSpace(ProjectionType projectionType, int width, int height)
	{
		mWidth = width;
		mHeight = height;
		
		mProjection = new GLESProjection(mShader, projectionType, width, height);
	}
	
	public void setTexture(GLESTexture texture)
	{
		mTexture = texture;
	}
	
	public void setTexture(Bitmap bitmap)
	{
		mTexture = new GLESTexture(bitmap);
	}
	
	public GLESTexture getTexture()
	{
		return mTexture;
	}
	
	public void changeTexture(GLESTexture texture)
	{
		mTexture = texture;
	}
	
	public void changeTexture(Bitmap bitmap)
	{
		mTexture.changeTexture(bitmap);
	}
	
	public void show()
	{
		mIsVisible = true;
	}
	
	public void hide()
	{
		mIsVisible = false;
	}
	
	/*
	 * top
	 * 
	 * HIGH_LEVEL_DEPTH
	 * DEFAULT_LEVEL_DEPTH
	 * FUSTUM object
	 * LOW_LEVEL_DEPTH
	 * 
	 * bottom
	 */
	public void setDepth(DepthLevel depth)
	{
		mDepth = depth;
	}
	
	public abstract void update();
	public abstract void draw();
	public abstract boolean createMesh(float x, float y, float width, float height, int resolution);
	
	protected abstract void getUniformLocations();
	
}
