package com.gomdev.gles;

public class GLESContext {
    private static GLESContext sContext = new GLESContext();

    private GLESRenderer mRenderer = null;

    public static GLESContext getInstance() {
        return sContext;
    }

    public void setRenderer(GLESRenderer renderer) {
        mRenderer = renderer;
    }

    public GLESRenderer getRenderer() {
        return mRenderer;
    }
}
