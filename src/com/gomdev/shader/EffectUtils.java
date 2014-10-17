package com.gomdev.shader;

import java.io.File;
import java.util.ArrayList;

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESUtils;

import android.content.Context;
import android.os.Bundle;

public class EffectUtils {
    static final String CLASS = "EffectUtils";
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
                .getParcelableArrayList("shaderInfo");
        for (ShaderInfo info : savedShaderInfoList) {
            shaderInfoList.add(info);
        }
        context.setEffetName(icicle.getString("EffectName"));
        context.setNumOfShaders(icicle.getInt("numOfShader"));
        context.setSavedShaderInfo((ShaderInfo) icicle
                .getParcelable("saveShaderInfo"));
        context.setShowInfo(icicle.getBoolean("showInfo"));
        context.setShowFPS(icicle.getBoolean("showFPS"));
        context.setUseGLES30(icicle.getBoolean("useGLES30"));
        context.setExtensions(icicle.getString("extension"));
    }

    public static void saveShaderContext(Bundle outState) {
        ShaderContext context = ShaderContext.getInstance();
        outState.putParcelableArrayList("shaderInfo",
                context.getShaderInfoList());
        outState.putString("EffectName", context.getEffectName());
        outState.putInt("numOfShader", context.getNumOfShaders());
        outState.putParcelable("saveShaderInfo", context.getSavedShaderInfo());
        outState.putBoolean("showInfo", context.showInfo());
        outState.putBoolean("showFPS", context.showFPS());
        outState.putBoolean("useGLES30", context.useGLES30());
        outState.putString("extension", context.getExtensions());
    }
}
