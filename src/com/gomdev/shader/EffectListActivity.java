package com.gomdev.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gomdev.shader.R;
import com.gomdev.shader.basic.BasicConfig;
import com.gomdev.shader.texture.TextureConfig;
import com.gomdev.shader.whitehole.WhiteholeConfig;
import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESContext;
import com.gomdev.gles.GLESConfig.Version;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EffectListActivity extends Activity implements DialogListener {
    private static final String CLASS = "EffectListActivity";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private Map<String, EffectInfo> mEffectMap = new HashMap<String, EffectInfo>();

    class EffectInfo {
        Intent mIntent = null;
        int[] mShaderResIDs;
        String[] mShaderTitle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        EffectContext.newInstance();

        optionChanged();
    }

    private void setupEffectInfos() {
        mEffectMap.clear();

        EffectInfo info = new EffectInfo();
        info.mIntent = new Intent(this,
                com.gomdev.shader.whitehole.WhiteholeActivity.class);
        Version version = GLESContext.getInstance().getVersion();
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.whitehole_20_vs,
                    R.raw.whitehole_20_fs,
                    R.raw.whitehole_20_vs,
                    R.raw.whitehole_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.whitehole_30_vs,
                    R.raw.whitehole_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Whitehole main VS",
                "Whitehole main FS",
                "Whitehole overlay VS",
                "Whitehole overlay FS"
        };

        mEffectMap.put(WhiteholeConfig.EFFECT_NAME, info);

        info = new EffectInfo();
        info.mIntent = new Intent(this,
                com.gomdev.shader.basic.BasicActivity.class);
        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.basic_20_vs,
                    R.raw.basic_20_fs,
                    R.raw.basic_20_vs,
                    R.raw.basic_20_fs
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.basic_30_vs,
                    R.raw.basic_30_fs
            };
        }
        info.mShaderTitle = new String[] {
                "Basic main VS",
                "Basic main FS",
                "Basic overlay VS",
                "Basic overlay FS"
        };

        mEffectMap.put(BasicConfig.EFFECT_NAME, info);

        info = new EffectInfo();
        info.mIntent = new Intent(this,
                com.gomdev.shader.texture.TextureActivity.class);

        if (version == Version.GLES_20) {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_20_vs,
                    R.raw.texture_20_fs,
            };
        } else {
            info.mShaderResIDs = new int[] {
                    R.raw.texture_30_vs,
                    R.raw.texture_30_fs,
            };
        }
        info.mShaderTitle = new String[] {
                "Texture VS",
                "Texture FS",
        };

        mEffectMap.put(TextureConfig.EFFECT_NAME, info);

        if (DEBUG) {
            Log.d(TAG, "onCreate() map<String, EffectInfo>");
            Set<Entry<String, EffectInfo>> entrySet = mEffectMap.entrySet();

            for (Entry<String, EffectInfo> entry : entrySet) {
                Log.d(TAG, "\t Item=" + entry.getKey());
            }
        }
    }

    private void makeEffectList() {
        ArrayList<String> effectList = new ArrayList<String>();
        Set<String> effectSet = mEffectMap.keySet();

        for (String str : effectSet) {
            effectList.add(str);
        }

        if (DEBUG) {
            Log.d(TAG, "onCreate() string list");

            for (String str : effectList) {
                Log.d(TAG, "\t Item=" + str);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, effectList);

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(mItemClickListener);
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            String effectName = parent.getItemAtPosition(position).toString();

            EffectInfo info = mEffectMap.get(effectName);
            int numOfShader = info.mShaderResIDs.length;

            EffectContext context = EffectContext.getInstance();
            context.setEffetName(effectName);
            context.setNumOfShaders(numOfShader);

            context.clearShaderInfos();

            String title = null;
            String savedFileName = null;
            for (int i = 0; i < numOfShader; i++) {
                title = info.mShaderTitle[i];
                savedFileName = EffectUtils.getSavedFilePath(
                        EffectListActivity.this, title);
                context.setShaderInfo(info.mShaderTitle[i],
                        info.mShaderResIDs[i], savedFileName);
            }

            if (DEBUG) {
                Log.d(TAG, "onItemClick() item=" + effectName);
            }
            startActivity(info.mIntent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.effect_list_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.options:
            showOptionsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOptionsDialog() {
        EffectOptionsDialog dialog = new EffectOptionsDialog();
        dialog.show(getFragmentManager(), "effect_options");
    }

    private void optionChanged() {
        setupEffectInfos();
        makeEffectList();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        optionChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
