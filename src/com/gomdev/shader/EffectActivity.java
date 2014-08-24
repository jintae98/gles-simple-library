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

package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.shader.R;
import com.gomdev.shader.EffectContext.ShaderInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EffectActivity extends Activity {
    protected GLSurfaceView mView;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.effect_main);

        boolean showInfo = EffectContext.getInstance().showInfo();
        if (showInfo == true) {
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.layout);

            LayoutInflater inflater = (LayoutInflater) getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout = (LinearLayout) inflater.inflate(
                    R.layout.effect_info, null);

            frameLayout.addView(layout);

            showGLESVersion();

        }
    }

    private void showGLESVersion() {
        TextView textView = (TextView) findViewById(R.id.layout_version);

        Version version = GLESContext.getInstance().getVersion();
        switch (version) {
        case GLES_20:
            textView.setText("OpenGL ES 2.0");
            break;
        case GLES_30:
            textView.setText("OpenGL ES 3.0");
            break;
        default:

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.effect_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.shader_list:
            showShaderListDialog();
            return true;
        case R.id.restore:
            showRestoreDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRestoreDialog() {
        if (isEmptySavedFile() == true) {
            Toast.makeText(this, "No saved shader file", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        ShaderRestoreDialog dialog = new ShaderRestoreDialog();
        dialog.show(getFragmentManager(), "restore");
    }

    private boolean isEmptySavedFile() {
        EffectContext context = EffectContext.getInstance();

        ArrayList<ShaderInfo> shaders = context.getShaderInfoList();
        for (ShaderInfo shaderInfo : shaders) {
            if (GLESFileUtils.isExist(shaderInfo.mFilePath)) {
                return false;
            }
        }

        return true;
    }

    private void showShaderListDialog() {
        ShaderListDialog dialog = new ShaderListDialog();
        dialog.show(getFragmentManager(), "shaderlist");
    }

    protected void setGLESVersion() {
        Version version = GLESContext.getInstance().getVersion();
        switch (version) {
        case GLES_20:
            mView.setEGLContextClientVersion(2);
            break;
        case GLES_30:
            mView.setEGLContextClientVersion(3);
            break;
        default:
            mView.setEGLContextClientVersion(2);
        }
    }

    protected void addSurfaceView() {
        FrameLayout layout = (FrameLayout) findViewById(R.id.layout_surface);
        layout.addView(mView);
    }
}