package com.gomdev.gles;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLESTexture {
	private static final String TAG = "gomdev GLESTexture";
	private static final boolean DEBUG = GLESConfig.DEBUG;
	
	private int mWidth;
	private int mHeight;
	
	private int mTextureID;
	
	public GLESTexture(Bitmap bitmap)
	{
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		
		makeTexture(bitmap);
		
	}

	public GLESTexture(int width, int height, Bitmap bitmap)
	{
		mWidth = width;
		mHeight = height;
		
		makeTexture(bitmap);
	}
	
	public int getTextureID()
	{
		if(GLES20.glIsTexture(mTextureID) == false)
		{
			Log.e(TAG, "mTextureID is invalid");
			return -1;
		}
		
		return mTextureID;
	}
	
	public void changeTexture(Bitmap bitmap)
	{
		if(DEBUG) Log.d(TAG, "changeTexture() textureID=" + mTextureID + " bitmap=" + bitmap);
		
		
		if(bitmap == null)
		{
		    throw new IllegalArgumentException("changeTexture() bitmap is null");
		}
		

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

		if(GLES20.glIsTexture(mTextureID) == true)
		{
			int[] textures = new int[1];
			textures[0] = mTextureID;
			GLES20.glDeleteTextures(1, textures, 0);
		}

		makeTexture(bitmap);
	}
	
	public int getWidth()
	{
		return mWidth;
	}
	
	public int getHeight()
	{
		return mHeight;
	}
	
	private void makeTexture(Bitmap bitmap)
	{
		if(DEBUG) Log.d(TAG, "makeTexture() bitmap=" + bitmap);
		if(bitmap == null)
		{
			throw new IllegalArgumentException("setTexture() bitmap is null");
		}
		
		int[] textureIds = new int[1];
		GLES20.glGenTextures(1, textureIds, 0);
		mTextureID = textureIds[0];
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		bitmap.recycle();
	}
}
