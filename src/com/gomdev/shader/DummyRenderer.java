package com.gomdev.shader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;

public class DummyRenderer implements Renderer {
    static final String CLASS = "DummyRenderer";
    static final String TAG = ShaderConfig.TAG + " " + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    private Context mContext = null;
    private Handler mHandler = null;

    public DummyRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
        ShaderContext.getInstance().setExtensions(extensions);
        mHandler.sendEmptyMessage(EffectListActivity.GET_EXTENSIONS);

        saveExtensionToPreferences(extensions);
    }

    private void saveExtensionToPreferences(String extensions) {
        SharedPreferences pref = mContext.getSharedPreferences(
                ShaderConfig.PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ShaderConfig.PREF_GLES_EXTENSION, extensions);
        editor.commit();
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
