package com.gomdev.shader;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESRenderer;

public class EffectRenderer {
    protected static final int COMPILE_OR_LINK_ERROR = 1;

    static {
        System.loadLibrary("gomdev");
    }

    public Context mContext;
    public GLSurfaceView mView;
    public GLESRenderer mRenderer;
    public Handler mHandler = new Handler(Looper.getMainLooper()) {

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

        mRenderer = new GLESRenderer();

        GLESContext.getInstance().setContext(context);
    }

    public void setSurfaceView(GLSurfaceView surfaceView) {
        mView = surfaceView;
    }
}