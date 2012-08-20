package com.gomdev.gles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class GLESUtils {
	private static final boolean 	DEBUG = GLESConfig.DEBUG;
	private static final String 	TAG = "gomdev GLESUtils";
	
	public static float convertScreenToSpace(float value)
	{
		return value / GLESConfig.SPACE_SCALE_FACTOR;
	}
	
	
	private static final int NUM_OF_FRAME = 4;
	private static int sFrameCount = 0;
	private static long sStartTick = -1L;
	private static long sTotalTime = 0L;
	
	public static void checkFPS()
	{
		float fps = 0.0f;
		long currentTick = 0L;
		
		if(sStartTick < 0L)
		{
			sStartTick = System.nanoTime();
			sFrameCount = 0;
		}
		else
		{
			++sFrameCount;
			
			if(sFrameCount >= NUM_OF_FRAME)
			{
				currentTick = System.nanoTime();
				sTotalTime = currentTick - sStartTick;
				fps = (float) sFrameCount * 1000000000 / sTotalTime;
				
				Log.d(TAG, "checkFPS() fps=" + fps);
				
				sFrameCount = 0;
				sStartTick = currentTick;
				sTotalTime = 0L;
			}
		}
	}
	
	public static Bitmap makeBitmap(int width, int height, Bitmap.Config config, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        
        return bitmap;
	}
}
