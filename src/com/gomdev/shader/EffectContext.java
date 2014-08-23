package com.gomdev.shader;

import java.util.ArrayList;

public class EffectContext {
    private static EffectContext sEffectContext = null;

    class ShaderInfo {
        String mTitle;
        int mResID;
        String mFilePath;
    }

    private ArrayList<ShaderInfo> mShaderInfos = null;
    private String mEffectName = null;
    private int mNumOfShaders = 0;
    private ShaderInfo mSavedShaderInfo = null;

    private boolean mShowFPS = true;

    public static EffectContext getInstance() {
        return sEffectContext;
    }

    public static EffectContext newInstance() {
        sEffectContext = new EffectContext();
        return sEffectContext;
    }

    private EffectContext() {
        mShaderInfos = new ArrayList<ShaderInfo>();
    }

    public void setEffetName(String name) {
        mEffectName = name;
    }

    public String getEffectName() {
        return mEffectName;
    }

    public void setNumOfShaders(int num) {
        mNumOfShaders = num;
    }

    public int getNumOfShaders() {
        return mNumOfShaders;
    }

    public void setShaderInfo(String title, int resID, String filePath) {
        ShaderInfo shader = new ShaderInfo();
        shader.mTitle = title;
        shader.mResID = resID;
        shader.mFilePath = filePath;

        mShaderInfos.add(shader);
    }

    public ArrayList<ShaderInfo> getShaderInfoList() {
        return mShaderInfos;
    }

    public void clearShaderInfos() {
        mShaderInfos.clear();
    }

    public void setSavedShaderInfo(ShaderInfo info) {
        mSavedShaderInfo = info;
    }

    public ShaderInfo getSavedShaderInfo() {
        return mSavedShaderInfo;
    }

    public void setShowFPS(boolean showFPS) {
        mShowFPS = showFPS;
    }

    public boolean showFPS() {
        return mShowFPS;
    }
}
