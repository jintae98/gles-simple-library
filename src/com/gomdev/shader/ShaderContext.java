package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.gles.GLESContext;

public class ShaderContext {
    static final String CLASS = "ShaderContext";
    static final String TAG = ShaderConfig.TAG + " " + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    private static ShaderContext sEffectContext = null;

    private ArrayList<ShaderInfo> mShaderInfos = null;
    private String mEffectName = null;
    private int mNumOfShaders = 0;
    private ShaderInfo mSavedShaderInfo = null;

    private boolean mShowInfo = true;
    private boolean mShowFPS = true;
    private boolean mUseGLES30 = (GLESConfig.GLES_VERSION == Version.GLES_30) ? true
            : false;

    private String mExtensions = null;
    private String mRenderer = null;
    private String mVendor = null;
    private String mVersion = null;

    private String mHardware = null;
    private String mArchitecture = null;
    private String mFeature = null;

    public static ShaderContext getInstance() {
        return sEffectContext;
    }

    public static ShaderContext newInstance() {
        sEffectContext = new ShaderContext();
        return sEffectContext;
    }

    private ShaderContext() {
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

    public void setShaderInfo(String effectName, String title,
            int resID, String filePath) {
        ShaderInfo shader = new ShaderInfo();
        shader.mEffectName = effectName;
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

    public void setExtensions(String extensions) {
        mExtensions = extensions;
    }

    public String getExtensions() {
        return mExtensions;
    }

    public void setRenderer(String renderer) {
        mRenderer = renderer;
    }

    public String getRenderer() {
        return mRenderer;
    }

    public void setVendor(String vendor) {
        mVendor = vendor;
    }

    public String getVendor() {
        return mVendor;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setHardware(String hardware) {
        mHardware = hardware;
    }

    public String getHardware() {
        return mHardware;
    }

    public void setArchitecture(String architecture) {
        mArchitecture = architecture;
    }

    public String getArchitecture() {
        return mArchitecture;
    }

    public void setFeature(String feature) {
        mFeature = feature;
    }

    public String getFeature() {
        return mFeature;
    }
}
