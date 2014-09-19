package com.gomdev.gles;

import com.gomdev.gles.GLESConfig.Version;

import android.content.Context;

public class GLESContext {
    private static GLESContext sContext = new GLESContext();

    private GLESRenderer mRenderer = null;
    private Context mContext = null;
    private Version mGLESVersion = GLESConfig.GLES_VERSION;
    private String mShaderErrorLog = null;

    public static GLESContext getInstance() {
        return sContext;
    }

    private GLESContext() {

    }

    public void setRenderer(GLESRenderer renderer) {
        mRenderer = renderer;
    }

    public GLESRenderer getRenderer() {
        return mRenderer;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setVersion(Version version) {
        mGLESVersion = version;
    }

    public Version getVersion() {
        return mGLESVersion;
    }

    public void setShaderErrorLog(String log) {
        mShaderErrorLog = log;
    }

    public String getShaderErrorLog() {
        return mShaderErrorLog;
    }
}
