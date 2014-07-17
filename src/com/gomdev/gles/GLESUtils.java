package com.gomdev.gles;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.File;
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

    public static boolean checkFileExists(String fileName) {
        File file = new File(fileName);
        if (file == null) {
            Log.e(TAG, "checkFileExists() file instance is null");
            return false;
        }
        return file.exists();
    }

    public static float convertScreenToSpace(float f) {
        return f / 4.0F;
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

    public static void deleteFile(String fileName) {
        File file = new File(new String(fileName));
        if (file.exists() == true) {
            file.delete();
        }
    }

    public static StringBuilder getDataPathName(Context context) {
        StringBuilder builder = new StringBuilder(Environment
                .getDataDirectory().getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append("data");
        builder.append(File.separatorChar);
        builder.append(context.getPackageName());
        builder.append(File.separatorChar);
        return builder;
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

    public static String makeLockScreenStringPath(Context context,
            StringBuilder builder, String path) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            String str1 = "_" + packageInfo.versionName + ".dat";
            String str2 = builder + path + str1;
            return str2;
        } catch (Exception localException) {
            Log.e(TAG, "makeLockScreenStringPath() Exception e="
                    + localException);
        }
        return null;
    }
}