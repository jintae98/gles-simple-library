package com.gomdev.gles;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLESUtils {
    private static final String CLASS = "GLESUtils";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private static final int NUM_OF_FRAME = 4;

    private static float sDpiConvertUnit = 0f;

    private static int sFrameCount = 0;
    private static long sStartTick = -1L;
    private static long sTotalTime = 0L;

    private static float sWidthPixels = 0f;
    private static float sHeightPixels = 0f;

    public static Bitmap checkAndReplaceBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG,
                    "checkAndReplaceBitmap() Bitmap is null. Replace with transparent bitmap");
            bitmap = makeBitmap(16, 16, Bitmap.Config.ARGB_8888, 0);
        }
        return bitmap;
    }

    public static void checkFPS() {
        if (sStartTick < 0L) {
            sStartTick = System.nanoTime();
            sFrameCount = 0;
        }

        if (sFrameCount >= NUM_OF_FRAME) {
            long currentNano = System.nanoTime();
            sTotalTime = currentNano - sStartTick;

            float fps = sFrameCount * 1000000000 / (float) sTotalTime;
            Log.d(TAG, "checkFPS() fps=" + fps);

            sFrameCount = 0;
            sStartTick = currentNano;
            sTotalTime = 0L;
        } else {
            sFrameCount++;
        }
    }

    public static FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer buffer = ByteBuffer
                .allocateDirect(array.length * GLESConfig.FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    public static ShortBuffer makeShortBuffer(short[] array) {
        ShortBuffer buffer = ByteBuffer
                .allocateDirect(array.length * GLESConfig.SHORT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    public static float getWidthPixels(Context context) {
        if (Float.compare(sWidthPixels, 0.0F) == 0) {
            DisplayMetrics metric = context.getResources().getDisplayMetrics();
            sWidthPixels = metric.widthPixels;
            sHeightPixels = metric.heightPixels;
        }
        return sWidthPixels;
    }

    public static float getHeightPixels(Context context) {
        if (Float.compare(sHeightPixels, 0.0F) == 0) {
            DisplayMetrics metric = context.getResources().getDisplayMetrics();
            sWidthPixels = metric.widthPixels;
            sHeightPixels = metric.heightPixels;
        }
        return sHeightPixels;
    }

    public static float getPixelFromDpi(Context context, float dpi) {
        if (Float.compare(sDpiConvertUnit, 0.0F) == 0) {
            sDpiConvertUnit = context.getResources().getDisplayMetrics().densityDpi / 160.0F;
        }
        return dpi * sDpiConvertUnit;
    }

    public static float getPixelsFromPercentage(float paramFloat1,
            float paramFloat2) {
        return paramFloat2 * (0.01F * paramFloat1);
    }

    public static Bitmap makeBitmap(int width, int height, Config config,
            int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
        return bitmap;
    }

    public static String getAppDataPathName(Context context) {
        StringBuilder builder = new StringBuilder(Environment
                .getDataDirectory().getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append("data");
        builder.append(File.separatorChar);
        builder.append(context.getPackageName());
        builder.append(File.separatorChar);
        return builder.toString();
    }
    
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static String getShaderBinaryFilePath(Context context, String prefix) {
        String appDataPath = GLESUtils.getAppDataPathName(context);
        String versionName = GLESUtils.getAppVersionName(context);
        String path = appDataPath + prefix + "_" + versionName + ".dat";

        return path;
    }

    public static String getStringFromReosurce(Context context, int resourceID) {
        byte[] data;
        int strLength;
        String str = null;
        Resources res = context.getResources();
        InputStream is = res.openRawResource(resourceID);
        try {
            try {
                data = new byte[1024];
                strLength = 0;
                while (true) {
                    int bytesLeft = data.length - strLength;
                    if (bytesLeft == 0) {
                        byte[] buf2 = new byte[data.length * 2];
                        System.arraycopy(data, 0, buf2, 0, data.length);
                        data = buf2;
                        bytesLeft = data.length - strLength;
                    }
                    int bytesRead = is.read(data, strLength, bytesLeft);
                    if (bytesRead <= 0) {
                        break;
                    }
                    strLength += bytesRead;
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new Resources.NotFoundException();
        }

        try {
            str = new String(data, 0, strLength, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Could not decode shader string");
        }

        return str;
    }
}