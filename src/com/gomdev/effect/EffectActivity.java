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

package com.gomdev.effect;

import java.io.File;

import com.gomdev.effect.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EffectActivity extends Activity {
    private static final String CLASS = "EffectActivity";
    private static final String TAG = "gomdev " + CLASS;
    private static final boolean DEBUG = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.effect_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        SharedPreferences pref = this.getSharedPreferences(
                EffectConfig.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String effectName = pref.getString(EffectConfig.PREF_EFFECT_NAME,
                "Whitehole");

        String savedFileName = null;

        switch (item.getItemId()) {
        case R.id.vertex_shader:
            editor.putString(EffectConfig.PREF_SHADER_TYPE,
                    EffectConfig.SHADER_TYPE_VS);
            savedFileName = EffectUtils.getSavedFilePath(effectName,
                    EffectConfig.SHADER_TYPE_VS);
            editor.putString(EffectConfig.PREF_VS_FILE_NAME, savedFileName);
            editor.commit();

            intent = new Intent(this,
                    com.gomdev.effect.ShaderViewActivity.class);
            startActivity(intent);
            return true;
        case R.id.fragment_shader:
            editor.putString(EffectConfig.PREF_SHADER_TYPE,
                    EffectConfig.SHADER_TYPE_FS);
            savedFileName = EffectUtils.getSavedFilePath(effectName,
                    EffectConfig.SHADER_TYPE_FS);
            editor.putString(EffectConfig.PREF_FS_FILE_NAME, savedFileName);
            editor.commit();

            intent = new Intent(this,
                    com.gomdev.effect.ShaderViewActivity.class);
            startActivity(intent);
            return true;
        case R.id.restore_vs:
            savedFileName = EffectUtils.getSavedFilePath(effectName,
                    EffectConfig.SHADER_TYPE_VS);
            File file = new File(savedFileName);
            file.delete();
            this.finish();
            return true;
        case R.id.restore_fs:
            savedFileName = EffectUtils.getSavedFilePath(effectName,
                    EffectConfig.SHADER_TYPE_FS);
            file = new File(savedFileName);
            file.delete();
            this.finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}