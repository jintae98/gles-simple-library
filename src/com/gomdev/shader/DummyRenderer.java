package com.gomdev.shader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;

public class DummyRenderer implements Renderer {
    
    private Handler mHandler = null;

    public DummyRenderer() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        extensions.replace(' ', '\n');
        ShaderContext.getInstance().setExtensions(extensions);
        mHandler.sendEmptyMessage(EffectListActivity.GET_EXTENSIONS);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }
}
