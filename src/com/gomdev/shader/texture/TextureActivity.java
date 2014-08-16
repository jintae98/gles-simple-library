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

package com.gomdev.shader.texture;

import com.gomdev.shader.R;
import com.gomdev.shader.EffectActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TextureActivity extends EffectActivity {
    private GLSurfaceView mView;
    private TextureRenderer mRenderer;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        mRenderer = new TextureRenderer(this);
        mView = new GLSurfaceView(this);
        mRenderer.setSurfaceView(mView);

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
            ((TextureRenderer) mRenderer).touchDown(x, y);
            break;
        case MotionEvent.ACTION_UP:
            ((TextureRenderer) mRenderer).touchUp(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            ((TextureRenderer) mRenderer).touchMove(x, y);
            break;
        }

        return super.onTouchEvent(event);
    }
}