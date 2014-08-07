/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gomdev.effect.whitehole;

import com.gomdev.effect.EffectActivity;
import com.gomdev.effect.R;
import com.gomdev.gles.GLESRenderer;
import com.gomdev.gles.GLESSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class WhiteholeActivity extends EffectActivity {
    private static final String CLASS = "TestActivity";
    private static final String TAG = "gomdev " + CLASS;
    private static final boolean DEBUG = false;

    private GLESSurfaceView mView;
    private GLESRenderer mRenderer;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        mRenderer = new com.gomdev.effect.whitehole.WhiteholeRenderer(this);
        // mRenderer = new com.gomdev.particle.ParticleRenderer(this);
        mView = new GLESSurfaceView(this, mRenderer);

        mView.setEGLContextClientVersion(2);
        mView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mView.setRenderer(mRenderer);
        mView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS);
        mView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        FrameLayout layout = (FrameLayout) findViewById(R.id.layout);
        layout.addView(mView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mRenderer == null) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mView.touchDown(x, y);
            break;
        case MotionEvent.ACTION_UP:
            mView.touchUp(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            mView.touchMove(x, y);
            break;
        }

        return super.onTouchEvent(event);
    }
}