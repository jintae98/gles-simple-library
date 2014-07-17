package com.gomdev.gles;

import android.opengl.GLSurfaceView.Renderer;

public abstract interface GLESRenderer extends Renderer {
    public abstract void hideAll();

    public abstract void initRenderer();

    public abstract void pause();

    public abstract void resume();

    public abstract void setSurfaceView(GLESSurfaceView surfaceView);

    public abstract void showAll();

    public abstract void touchCancel(float x, float y);

    public abstract void touchDown(float x, float y, float user);

    public abstract void touchMove(float x, float y, float user);

    public abstract void touchUp(float x, float y, float user);
}