package com.gomdev.shader;

import java.io.File;
import java.util.ArrayList;

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESUtils;

import android.content.Context;
import android.os.Bundle;

public class ShaderUtils {
    static final String CLASS = "ShaderUtils";
    static final String TAG = ShaderConfig.TAG + " " + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    public static String getSavedFilePath(Context context, String prefix,
            String shaderTitle) {
        File file = context.getExternalFilesDir(null);
        StringBuilder builder = new StringBuilder(file.getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append(prefix);
        builder.append("_");
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

    public static void restoreShaderContext(Bundle icicle) {
        ShaderContext context = ShaderContext.getInstance();
        if (context == null) {
            context = ShaderContext.newInstance();
        }

        context.clearShaderInfos();
        ArrayList<ShaderInfo> shaderInfoList = context.getShaderInfoList();

        ArrayList<ShaderInfo> savedShaderInfoList = icicle
                .getParcelableArrayList(ShaderConfig.STATE_SHADER_INFO);
        for (ShaderInfo info : savedShaderInfoList) {
            shaderInfoList.add(info);
        }
        context.setEffetName(icicle.getString(ShaderConfig.STATE_EFFECT_NAME));
        context.setNumOfShaders(icicle.getInt(ShaderConfig.STATE_NUM_OF_SHADER));
        context.setSavedShaderInfo((ShaderInfo) icicle
                .getParcelable(ShaderConfig.STATE_SAVED_SHADER_INFO));
        context.setShowInfo(icicle.getBoolean(ShaderConfig.STATE_SHOW_INFO));
        context.setShowFPS(icicle.getBoolean(ShaderConfig.STATE_SHOW_FPS));
        context.setUseGLES30(icicle.getBoolean(ShaderConfig.STATE_USE_GLES30));
        context.setExtensions(icicle.getString(ShaderConfig.STATE_EXTENSIONS));
    }

    public static void saveShaderContext(Bundle outState) {
        ShaderContext context = ShaderContext.getInstance();
        outState.putParcelableArrayList(ShaderConfig.STATE_SHADER_INFO,
                context.getShaderInfoList());
        outState.putString(ShaderConfig.STATE_EFFECT_NAME,
                context.getEffectName());
        outState.putInt(ShaderConfig.STATE_NUM_OF_SHADER,
                context.getNumOfShaders());
        outState.putParcelable(ShaderConfig.STATE_SAVED_SHADER_INFO,
                context.getSavedShaderInfo());
        outState.putBoolean(ShaderConfig.STATE_SHOW_INFO, context.showInfo());
        outState.putBoolean(ShaderConfig.STATE_SHOW_FPS, context.showFPS());
        outState.putBoolean(ShaderConfig.STATE_USE_GLES30, context.useGLES30());
        outState.putString(ShaderConfig.STATE_EXTENSIONS,
                context.getExtensions());
    }
}
