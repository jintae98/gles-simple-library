package com.gomdev.gles;

import static android.opengl.GLES20.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class GLESShader {
	private static final String TAG = "gomdev GLESShader";
	private static final boolean DEBUG = GLESConfig.DEBUG;
	
	private final Resources 	mRes;
	private final Context		mContext;
	
	private int					mProgram;
	private int 				mVertexIndex = -1;
	private int 				mTexCoordIndex = -1;
	private int 				mNormalIndex = -1;
	private int 				mColorIndex = -1;
	
	private boolean				mUseBinary = false;
	private AssetManager		mAssetManager = null;
	
	public GLESShader(Context context, boolean useBinary)
	{
		this.mContext = context;
		this.mRes = context.getResources();
		
		this.mUseBinary = useBinary;
		
		this.mProgram = glCreateProgram();
		if(mProgram == 0)
		{
			Log.e(TAG, "glCreateProgram() error=" + glGetError());
			throw new IllegalStateException("glCreateProgram() error=" + glGetError());
		}
		
		if(DEBUG)Log.d(TAG, "GLESShader() mProgram=" + mProgram);
		
		if(useBinary == true)
		{
			mAssetManager = mRes.getAssets();
		}
		
	}
	
	public boolean setShadersFromString(String vertexShader, String fragmentShader)
	{
		if(DEBUG)Log.d(TAG, "setShadersFromString()");
		
        boolean res = setShaderFromString(GLES20.GL_VERTEX_SHADER, vertexShader);
        if(res == false)
        {
        	return false;
        }
        
        res = setShaderFromString(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        if(res == false)
        {
        	return false;
        }
        
        res = linkProgram();
        if(res == false)
        {
        	return false;
        }
        
        return true;
	}
	
	public boolean setShadersFromResource(int vertexShaderID, int fragmentShaderID)
	{
		if(DEBUG)Log.d(TAG, "setShadersFromResource()");
		
        boolean res = setShaderFromResource(GLES20.GL_VERTEX_SHADER, vertexShaderID);
        if(res == false)
        {
        	return false;
        }
        
        res = setShaderFromResource(GLES20.GL_FRAGMENT_SHADER, fragmentShaderID);
        if(res == false)
        {
        	return false;
        }
        
        res = linkProgram();
        if(res == false)
        {
        	return false;
        }
        
        return true;
	}
	
	public int getProgram()
	{
		if(DEBUG)Log.d(TAG, "getProgram() mProgram=" + mProgram);
		
		return mProgram;
	}
	
	public void useProgram()
	{
		if(DEBUG)Log.d(TAG, "useProgram() mProgram=" + mProgram);
		
		glUseProgram(mProgram);
	}
	
	public void setVertexAttribIndex(String vertexAttribName)
	{
		if(DEBUG)Log.d(TAG, "setVertexAttribIndex() vertexAttribName=" + vertexAttribName);
		
		mVertexIndex = GLES20.glGetAttribLocation(mProgram, vertexAttribName);
	}
	
	public int getVertexAttribIndex()
	{
//		if(DEBUG)Log.d(TAG, "getVertexAttribIndex() mVertexIndex=" + mVertexIndex);
		
		if(mVertexIndex == -1)
		{
			Log.e(TAG, "getVertexAttribIndex() mVertexIndex is not set");
		}
		
		return mVertexIndex;
	}
	
	public void setTexCoordAttribIndex(String texCoordAttribName)
	{
		if(DEBUG)Log.d(TAG, "setTexCoordAttribIndex() texCoordAttribName=" + texCoordAttribName);
		
		mTexCoordIndex = GLES20.glGetAttribLocation(mProgram, texCoordAttribName);
	}
	
	public int getTexCoordAttribIndex()
	{
//		if(DEBUG)Log.d(TAG, "getTexCoordAttribIndex() mTexCoordIndex=" + mTexCoordIndex);
		
		if(mTexCoordIndex == -1)
		{
			Log.e(TAG, "getTexCoordAttribIndex() mTexCoordIndex is not set");
		}
		
		return mTexCoordIndex;
	}
	
	public void setNormalAttribIndex(String normalAttribName)
	{
		if(DEBUG)Log.d(TAG, "setNormalAttribIndex() normalAttribName=" + normalAttribName);
		
		mNormalIndex = GLES20.glGetAttribLocation(mProgram, normalAttribName);
	}
	
	public int getNormalAttribIndex()
	{
//		if(DEBUG)Log.d(TAG, "getNormalAttribIndex() mNormalIndex=" + mNormalIndex);
		
		if(mNormalIndex == -1)
		{
			Log.e(TAG, "getNormalAttribIndex() mNormalIndex is not set");
		}
		
		return mNormalIndex;
	}
	
	public void setColorAttribIndex(String colorAttribName)
	{
		if(DEBUG)Log.d(TAG, "setColorAttribIndex() colorAttribName=" + colorAttribName);
		
		mColorIndex = GLES20.glGetAttribLocation(mProgram, colorAttribName);
	}
	
	public int getColorAttribIndex()
	{
//		if(DEBUG)Log.d(TAG, "getColorAttribIndex() mColorIndex=" + mColorIndex);
		
		if(mColorIndex == -1)
		{
			Log.e(TAG, "getColorAttribIndex() mColorIndex is not set");
		}
		return mColorIndex;
	}
	
	public int getUniformLocation(String uniforomName)
	{
		int index = -1;
		index = GLES20.glGetUniformLocation(mProgram, uniforomName);
		
		if(DEBUG)Log.d(TAG, "getUniformLocation() uniforomName=" + uniforomName + " index=" + index);
		
		return index;
	}
	
	public int getAttribLocation(String attribName)
	{
		int index = -1;
		index = GLES20.glGetAttribLocation(mProgram, attribName);
		
		if(DEBUG)Log.d(TAG, "getAttribLocation() attribName=" + attribName + " index=" + index);
		
		return index;
	}
	
	public void retrieveProgramBinary(String fileName)
	{
		int binaryFormat = nRetrieveProgramBinary(mProgram, fileName);
//		if(DEBUG) 
			Log.d(TAG, "retrieveProgramBinary() mBinaryFormat=" + binaryFormat);
		
	}
	
    public void loadProgramBinary(String fileName, int binaryFormat)
    {
    	int result = nLoadProgramBinary(mProgram, binaryFormat, fileName, mAssetManager);
    	
    }
	
	private boolean	linkProgram()
	{
		glLinkProgram(mProgram);
		int[] linkStatus = new int[1];
		glGetProgramiv(mProgram, GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GL_TRUE) 
		{
			Log.e(TAG, "Could not link program: ");
			Log.e(TAG, glGetProgramInfoLog(mProgram));
			glDeleteProgram(mProgram);
			mProgram = 0;
			
			throw new RuntimeException("glLinkProgram() Error");
		}

		glUseProgram(mProgram);
		
		return true;
	}
	
	private boolean setShaderFromString(int shaderType, String source) {
        int shader = glCreateShader(shaderType);
        if (shader != 0) {
            glShaderSource(shader, source);
            glCompileShader(shader);
            int[] compiled = new int[1];
            glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, glGetShaderInfoLog(shader));
                glDeleteShader(shader);
                shader = 0;
                throw new RuntimeException("glCompileShader() Error");
            }
        }
        GLES20.glAttachShader(mProgram, shader);
        return true;
    }
	
	private boolean setShaderFromResource(int shaderType, int resourceID) 
	{
        int shader = glCreateShader(shaderType);
        if (shader != 0) {
        	String source = getShaderFromReosurce(resourceID);
            glShaderSource(shader, source);
            glCompileShader(shader);
            int[] compiled = new int[1];
            glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, glGetShaderInfoLog(shader));
                glDeleteShader(shader);
                shader = 0;
                throw new RuntimeException("glShaderSource() Error");
            }
        }
        GLES20.glAttachShader(mProgram, shader);
        return true;
    }
	
	private String getShaderFromReosurce(int resourceID) {
        byte[] str;
        int strLength;
        String shader = null;
        InputStream is = mRes.openRawResource(resourceID);
        try {
            try {
                str = new byte[1024];
                strLength = 0;
                while(true) {
                    int bytesLeft = str.length - strLength;                                                                                             
                    if (bytesLeft == 0) {                                                                                                               
                        byte[] buf2 = new byte[str.length * 2];                                                                                         
                        System.arraycopy(str, 0, buf2, 0, str.length);                                                                                  
                        str = buf2;                                                                                                                     
                        bytesLeft = str.length - strLength;                                                                                             
                    }                                                                                                                                   
                    int bytesRead = is.read(str, strLength, bytesLeft);                                                                                 
                    if (bytesRead <= 0) {                                                                                                               
                        break;                                                                                                                          
                    }                                                                                                                                   
                    strLength += bytesRead;                                                                                                             
                }                                                                                                                                       
            } finally {                                                                                                                                 
                is.close();                                                                                                                             
            }                                                                                                                                           
        } catch(IOException e) {                                                                                                                        
            throw new Resources.NotFoundException();                                                                                                    
        }                                                                                                                                               
                                                                                                                                                        
        try {                                                                                                                                           
        	shader = new String(str, 0, strLength, "UTF-8");                                                                                           
        } catch (UnsupportedEncodingException e) {                                                                                                      
            Log.e("Renderscript shader creation", "Could not decode shader string");                                                                    
        }                                                                                                                                               
                                                                                                                                                        
        return shader;                                                                                                                                    
    }

	private native int nRetrieveProgramBinary(int program, String fileName);
    private native int nLoadProgramBinary(int program, int binaryFormat, String fileName, AssetManager assetManager);
}
