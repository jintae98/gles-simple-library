package com.gomdev.gles;

import android.content.Context;

public class GLESContext {
    private static GLESContext sContext = new GLESContext();

    private GLESRenderer mRenderer = null;
    private Context mContext = null;

    public static GLESContext getInstance() {
        return sContext;
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
}
