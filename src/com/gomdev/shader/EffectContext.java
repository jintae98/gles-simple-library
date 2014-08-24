package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESContext;

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

    private boolean mShowInfo = true;
    private boolean mShowFPS = true;
    private boolean mUseGLES30 = (GLESConfig.GLES_VERSION == Version.GLES_30) ? true
            : false;

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

    public void setShowInfo(boolean showInfo) {
        mShowInfo = showInfo;
    }

    public boolean showInfo() {
        return mShowInfo;
    }

    public void setShowFPS(boolean showFPS) {
        mShowFPS = showFPS;
    }

    public boolean showFPS() {
        return mShowFPS;
    }

    public void setUseGLES30(boolean useGLES30) {
        mUseGLES30 = useGLES30;

        if (useGLES30 == true) {
            GLESContext.getInstance().setVersion(Version.GLES_30);
        } else {
            GLESContext.getInstance().setVersion(Version.GLES_20);
        }
    }

    public boolean useGLES30() {
        return mUseGLES30;
    }
}
