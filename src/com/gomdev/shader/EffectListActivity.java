package com.gomdev.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gomdev.shader.R;
import com.gomdev.shader.basic.BasicConfig;
import com.gomdev.shader.whitehole.WhiteholeConfig;
import com.gomdev.gles.GLESConfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EffectListActivity extends Activity {
    private static final String CLASS = "EffectListActivity";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = true;// GLESConfig.DEBUG;

    // private Map<String, Intent> mEffectMap = new HashMap<String, Intent>();
    private Map<String, EffectInfo> mEffectMap = new HashMap<String, EffectInfo>();

    class EffectInfo {
        Intent mIntent = null;
        int[] mShaderResIDs;
        String[] mShaderTitle;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        EffectInfo info = new EffectInfo();
        info.mIntent = new Intent(this,
                com.gomdev.shader.whitehole.WhiteholeActivity.class);
        info.mShaderResIDs = new int[] {
                R.raw.whitehole_vs,
                R.raw.whitehole_fs,
                R.raw.whitehole_vs,
                R.raw.whitehole_fs
        };
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
        
        info.mShaderResIDs = new int[] {
                R.raw.basic_vs,
                R.raw.basic_fs,
                R.raw.basic_vs,
                R.raw.basic_fs
        };
        info.mShaderTitle = new String[] {
                "Basic main VS",
                "Basic main FS",
                "Basic overlay VS",
                "Basic overlay FS"
        };

        mEffectMap.put(BasicConfig.EFFECT_NAME, info);

        if (DEBUG) {
            Log.d(TAG, "onCreate() map<String, EffectInfo>");
            Set<Entry<String, EffectInfo>> entrySet = mEffectMap.entrySet();

            for (Entry entry : entrySet) {
                Log.d(TAG, "\t Item=" + entry.getKey());
            }
        }

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

            SharedPreferences pref = EffectListActivity.this
                    .getSharedPreferences(EffectConfig.PREF_NAME,
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString(EffectConfig.PREF_EFFECT_NAME, effectName);
            editor.putInt(EffectConfig.PREF_SHADER_COUNT, info.mShaderResIDs.length);
            
            for (int i = 0; i < info.mShaderResIDs.length; i++) {
                editor.putInt(EffectConfig.PREF_SHADER_RES_ID + i, info.mShaderResIDs[i]);
                editor.putString(EffectConfig.PREF_SHADER_TITLE + i, info.mShaderTitle[i]);
            }

            editor.commit();

            if (DEBUG) {
                Log.d(TAG, "onItemClick() item=" + effectName);
            }
            startActivity(info.mIntent);
        }
    };
}
