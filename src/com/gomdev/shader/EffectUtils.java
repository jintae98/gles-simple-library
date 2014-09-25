package com.gomdev.shader;

import java.io.File;
import java.util.ArrayList;

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESUtils;
import com.gomdev.shader.ShaderContext.ShaderInfo;

import android.content.Context;

public class EffectUtils {
    static final String CLASS = "EffectUtils";
    static final String TAG = ShaderConfig.TAG + " " + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    public static String getSavedFilePath(Context context, String shaderTitle) {
        File file = context.getExternalFilesDir(null);
        StringBuilder builder = new StringBuilder(file.getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append(shaderTitle);
        builder.append(".dat");

        return builder.toString();
    }

    public static String getShaderSource(Context context) {
        ShaderContext effectContext = ShaderContext.getInstance();
        ShaderInfo savedShaderInfo = effectContext.getSavedShaderInfo();

        String savedFileName = savedShaderInfo.mFilePath;
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = savedShaderInfo.mResID;
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }

    public static String getShaderSource(Context context, int i) {
        ShaderContext effectContext = ShaderContext.getInstance();

        ArrayList<ShaderInfo> shaderInfos = effectContext.getShaderInfoList();
        ShaderInfo shaderInfo = shaderInfos.get(i);

        String savedFileName = shaderInfo.mFilePath;
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = shaderInfo.mResID;
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }

    public static String getFragmentShaderSource(Context context, int i) {
        ShaderContext effectContext = ShaderContext.getInstance();

        ArrayList<ShaderInfo> shaderInfos = effectContext.getShaderInfoList();
        ShaderInfo shaderInfo = shaderInfos.get(i);

        String savedFileName = shaderInfo.mFilePath;
        String shaderSource = null;
        File file = new File(savedFileName);
        if (file.exists() == true) {
            shaderSource = GLESFileUtils.read(savedFileName);
        } else {
            int shaderResID = shaderInfo.mResID;
            shaderSource = GLESUtils
                    .getStringFromReosurce(context, shaderResID);
        }

        return shaderSource;
    }
}
