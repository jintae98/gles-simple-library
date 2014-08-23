package com.gomdev.shader;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESRenderer;
import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESUtils;

public class EffectRenderer {
    protected static final int COMPILE_OR_LINK_ERROR = 1;
    protected static final int UPDATE_FPS = 2;

    static {
        System.loadLibrary("gomdev");
    }

    protected Context mContext;
    protected GLSurfaceView mView;
    protected GLESRenderer mRenderer;
    protected TextView mFPS = null;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case COMPILE_OR_LINK_ERROR:
                Toast.makeText(mContext, "Compile or Link fails",
                        Toast.LENGTH_SHORT).show();
                break;
            case UPDATE_FPS:
                if (mFPS == null) {
                    mFPS = (TextView) ((Activity) mContext)
                            .findViewById(R.id.fps);
                }
                mFPS.setText("" + msg.arg1);
                break;
            }
        }

    };

    public EffectRenderer(Context context) {
        mContext = context;

        GLESContext.getInstance().setContext(context);

        mRenderer = GLESRenderer.createRenderer(GLESConfig.GLES_VERSION);
        GLESContext.getInstance().setVersion(GLESConfig.GLES_VERSION);
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
}