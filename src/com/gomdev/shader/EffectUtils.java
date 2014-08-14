package com.gomdev.shader;

import java.io.File;

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class EffectUtils {
    private static final String CLASS = "EffectUtils";
    private static final String TAG = "gomdev " + CLASS;
    private static final boolean DEBUG = false;

    public static String getSavedFilePath(Context context, String shaderTitle) {
        File file = context.getExternalFilesDir(null);
        StringBuilder builder = new StringBuilder(file.getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append(shaderTitle);
        builder.append(".dat");

        return builder.toString();
    }

    public static String getShaderSource(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);

        String savedFileName = pref.getString(EffectConfig.PREF_SAVED_FILE_NAME,
                "");
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = pref.getInt(EffectConfig.PREF_SAVED_RES_ID, 0);
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }

    public static String getVertexShaderSource(Context context, int i) {
        SharedPreferences pref = context.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);

        int index = i * 2;
        String shaderTitle = pref.getString(EffectConfig.PREF_SHADER_TITLE + index,
                "");
        String savedFileName = getSavedFilePath(context, shaderTitle);
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = pref.getInt(EffectConfig.PREF_SHADER_RES_ID + index, 0);
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }

    public static String getFragmentShaderSource(Context context, int i) {
        SharedPreferences pref = context.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);

        int index = i * 2 + 1;
        String shaderTitle = pref.getString(EffectConfig.PREF_SHADER_TITLE + index,
                "");
        String savedFileName = getSavedFilePath(context, shaderTitle);
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = pref.getInt(EffectConfig.PREF_SHADER_RES_ID + index, 0);
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }
}
