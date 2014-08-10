package com.gomdev.shader;

import com.gomdev.shader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShaderViewActivity extends Activity {
    private static final String CLASS = "ShaderEditActivity";
    private static final String TAG = "gomdev " + CLASS;
    private static final boolean DEBUG = false;

    private TextView mTextView = null;
    private String mShaderSource = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.shader_view);

        mTextView = (TextView) findViewById(R.id.shader_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mShaderSource = EffectUtils.getShaderSource(this);

        mTextView.setText(mShaderSource);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shader_view_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this,
                com.gomdev.shader.ShaderEditActivity.class);
        switch (item.getItemId()) {
        case R.id.edit:
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
