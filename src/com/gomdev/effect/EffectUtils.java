package com.gomdev.effect;

import java.io.File;

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

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
    
    public static String getSavedFilePath(Context context, String effectName, String shaderType) {
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

        int shaderResID = 0;
        String savedFileName = null;
        String shaderType = pref.getString(EffectConfig.PREF_SHADER_TYPE,
                EffectConfig.SHADER_TYPE_VS);
        if (shaderType.compareTo(EffectConfig.SHADER_TYPE_VS) == 0) {
            shaderResID = pref.getInt(EffectConfig.PREF_VS_RES_ID, 0);
            savedFileName = pref.getString(EffectConfig.PREF_VS_FILE_NAME, "");
        } else {
            shaderResID = pref.getInt(EffectConfig.PREF_FS_RES_ID, 0);
            savedFileName = pref.getString(EffectConfig.PREF_FS_FILE_NAME, "");
        }

        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            shaderSource = GLESUtils.getStringFromReosurce(context, shaderResID);
        }
        
        return shaderSource;
    }
}
