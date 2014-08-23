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

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.shader.R;
import com.gomdev.shader.EffectContext.ShaderInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EffectActivity extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        boolean showFPS = EffectContext.getInstance().showFPS();

        if (showFPS == true) {
            setContentView(R.layout.effect_with_fps);
        } else {
            setContentView(R.layout.effect_without_fps);
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
}