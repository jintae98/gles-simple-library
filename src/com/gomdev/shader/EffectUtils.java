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

    public static String getSavedFilePath(String effectName, String shaderType) {
        StringBuilder builder = new StringBuilder(Environment
                .getExternalStorageDirectory().getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append(EffectConfig.APP_DIRECTORY_NAME);
        builder.append(File.separatorChar);
        builder.append(effectName);
        builder.append('_');
        builder.append(shaderType);
        builder.append(".dat");

        return builder.toString();
    }

    public static String getSavedFilePath(Context context, String effectName,
            String shaderType) {
        File file = context.getExternalFilesDir(null);
        StringBuilder builder = new StringBuilder(file.getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append(effectName);
        builder.append('_');
        builder.append(shaderType);
        builder.append(".dat");

        return builder.toString();
    }

    public static String getShaderSource(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);

        String shaderSource = null;
        String shaderType = pref.getString(EffectConfig.PREF_SHADER_TYPE,
                EffectConfig.SHADER_TYPE_VS);
        if (shaderType.compareTo(EffectConfig.SHADER_TYPE_VS) == 0) {
            shaderSource = getVertexShaderSource(context);
        } else {
            shaderSource = getFragmentShaderSource(context);
        }

        return shaderSource;
    }

    public static String getVertexShaderSource(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);

        String savedFileName = pref.getString(EffectConfig.PREF_VS_FILE_NAME,
                "");
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = pref.getInt(EffectConfig.PREF_VS_RES_ID, 0);
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }
    
    public static String getFragmentShaderSource(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);

        String savedFileName = pref.getString(EffectConfig.PREF_FS_FILE_NAME,
                "");
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = pref.getInt(EffectConfig.PREF_FS_RES_ID, 0);
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }
}
