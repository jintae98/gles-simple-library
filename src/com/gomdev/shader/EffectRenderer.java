package com.gomdev.shader;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESRenderer;
import com.gomdev.gles.GLESUtils;

public abstract class EffectRenderer implements Renderer {
    static final String CLASS = "EffectRenderer";
    static final String TAG = ShaderConfig.TAG + " " + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    protected static final int COMPILE_OR_LINK_ERROR = 1;
    protected static final int UPDATE_FPS = 2;

    static {
        System.loadLibrary("gomdev");
    }

    protected Context mContext;
    protected GLSurfaceView mView;
    protected GLESRenderer mRenderer;

    protected TextView mFPS = null;

    private boolean mIsShaderCompiled = false;

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case COMPILE_OR_LINK_ERROR:
                Toast.makeText(mContext, "Compile or Link fails",
                        Toast.LENGTH_SHORT).show();

                showCompileLog();
                break;
            case UPDATE_FPS:
                ShaderContext context = ShaderContext.getInstance();
                boolean showInfo = context.showInfo();
                boolean showFPS = context.showFPS();
                if (showInfo == true) {
                    LinearLayout layout = (LinearLayout) ((Activity) mContext)
                            .findViewById(R.id.layout_fps);
                    if (showFPS == false) {
                        layout.setVisibility(View.INVISIBLE);
                    } else {
                        layout.setVisibility(View.VISIBLE);
                    }
                }

                if (showFPS == false || showInfo == false) {
                    return;
                }

                if (mFPS == null) {
                    mFPS = (TextView) ((Activity) mContext)
                            .findViewById(R.id.fps);
                }
                mFPS.setText("" + msg.arg1);
                break;
            }
        }

        private void showCompileLog() {
            TextView view = (TextView) ((Activity) mContext)
                    .findViewById(R.id.error_log);
            LinearLayout layout = (LinearLayout) ((Activity) mContext)
                    .findViewById(R.id.layout_info);

            String compileLog = GLESContext.getInstance()
                    .getShaderErrorLog();
            if (compileLog != null) {
                view.setText(compileLog);
                view.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
        }

    };

    public EffectRenderer(Context context) {
        mContext = context;

        GLESContext.getInstance().setContext(context);

        mRenderer = GLESRenderer.createRenderer();
    }

    public void setSurfaceView(GLSurfaceView surfaceView) {
        mView = surfaceView;
    }

    protected void updateFPS() {
        int fps = (int) GLESUtils.getFPS();

        Message msg = mHandler.obtainMessage(UPDATE_FPS);
        msg.arg1 = fps;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (DEBUG) {
            Log.d(TAG, "onSurfaceCreated()");
        }

        mIsShaderCompiled = createShader();

        if (mIsShaderCompiled == true) {
            onSurfaceCreated();
        } else {
            Log.e(TAG, "\t shader compiliation fails");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (DEBUG) {
            Log.d(TAG, "onSurfaceChanged()");
        }

        if (mIsShaderCompiled == true) {
            onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (DEBUG) {
            Log.d(TAG, "onDrawFrame()");
        }

        if (mIsShaderCompiled == true) {
            onDrawFrame();
        }

    }

    protected abstract void onSurfaceCreated();

    protected abstract void onSurfaceChanged(int width, int height);

    protected abstract void onDrawFrame();

    protected abstract boolean createShader();
}