package com.gomdev.gles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLESSurfaceView extends GLSurfaceView {
    private static final String CLASS = "GLESSurfaceView";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    protected Context mContext;
    protected GLESRenderer mRenderer = null;

    public GLESSurfaceView(Context context, AttributeSet attributeSet,
            GLESRenderer renderer) {
        super(context, attributeSet);
        init(context, renderer);
    }

    public GLESSurfaceView(Context context, GLESRenderer renderer) {
        super(context);
        init(context, renderer);
    }

    private void init(Context context, GLESRenderer renderer) {
        mContext = context;
        mRenderer = renderer;
        mRenderer.setSurfaceView(this);
    }

    public void onPause() {
        if (mRenderer != null) {
            mRenderer.initRenderer();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();

        if (mRenderer != null) {
            mRenderer.initRenderer();
        }

    }

    public void setRenderer(GLSurfaceView.Renderer renderer) {
        mRenderer = ((GLESRenderer) renderer);
        mRenderer.setSurfaceView(this);
        super.setRenderer(renderer);
    }

    public void touchCancel(final float x, final float y) {
        if (mRenderer == null) {
            return;
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.touchCancel(x, y);
            }
        });
    }

    public boolean touchDown(float x, float y) {
        return touchDown(x, y, 0.0F);
    }

    public boolean touchDown(final float x, final float y, final float userData) {
        if (mRenderer == null) {
            return false;
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.touchDown(x, y, userData);
            }
        });

        return true;
    }

    public boolean touchMove(float x, float y) {
        return touchMove(x, y, 0.0F);
    }

    public boolean touchMove(final float x, final float y, final float userData) {
        if (mRenderer == null) {
            return false;
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.touchMove(x, y, userData);
            }
        });
        return true;
    }

    public void touchUp(float x, float y) {
        touchUp(x, y, 0.0F);
    }

    public void touchUp(final float x, final float y, final float userData) {
        if (mRenderer == null) {
            return;
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.touchUp(x, y, userData);
            }
        });
    }
}
