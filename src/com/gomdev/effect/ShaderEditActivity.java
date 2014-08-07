package com.gomdev.effect;

import java.io.File;

import com.gomdev.gles.GLESFileUtils;
import com.gomdev.gles.GLESUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ShaderEditActivity extends Activity {
    private static final String CLASS = "ShaderEditActivity";
    private static final String TAG = "gomdev " + CLASS;
    private static final boolean DEBUG = false;

    private EditText mEditView = null;

    private String mShaderSource = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.shader_edit);

        mEditView = (EditText) findViewById(R.id.shader_edit);

        mShaderSource = EffectUtils.getShaderSource(this);

        mEditView.setText(mShaderSource);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shader_edit_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.save:
            SharedPreferences pref = this.getSharedPreferences(
                    EffectConfig.PREF_NAME, MODE_PRIVATE);
            String effect = pref.getString(EffectConfig.PREF_EFFECT_NAME,
                    "");
            String shaderType = pref.getString(EffectConfig.PREF_SHADER_TYPE,
                    EffectConfig.SHADER_TYPE_VS);

            if (checkSDCardState() == false) {
                Toast.makeText(this, "SDCard is not available",
                        Toast.LENGTH_SHORT).show();

                return false;
            }

            String path = EffectUtils.getSavedFilePath(effect, shaderType);

            GLESFileUtils.write(path, mEditView.getText().toString());

            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            this.finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkSDCardState() {
        GLESFileUtils.checkExternalStorageState();
        if (GLESFileUtils.isExternalStorageAvaiable() == false
                || GLESFileUtils.isExternalStorageWriable() == false) {
            return false;
        }

        return true;
    }
}
