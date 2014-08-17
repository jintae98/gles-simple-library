package com.gomdev.shader;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESRenderer;
import com.gomdev.gles.GLESConfig;

public class EffectRenderer {
    protected static final int COMPILE_OR_LINK_ERROR = 1;

    static {
        System.loadLibrary("gomdev");
    }

    protected Context mContext;
    protected GLSurfaceView mView;
    protected GLESRenderer mRenderer;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPILE_OR_LINK_ERROR) {
                Toast.makeText(mContext, "Compile or Link fails",
                        Toast.LENGTH_SHORT).show();
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
}