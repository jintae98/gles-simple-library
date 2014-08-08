package com.gomdev.effect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.gomdev.effect.whitehole.WhiteholeConfig;
import com.gomdev.gles.GLESConfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class EffectListActivity extends Activity {
    private static final String CLASS = "EffectListActivity";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = true;// GLESConfig.DEBUG;

    // private Map<String, Intent> mEffectMap = new HashMap<String, Intent>();
    private Map<String, EffectInfo> mEffectMap = new HashMap<String, EffectInfo>();

    class EffectInfo {
        Intent mIntent = null;
        int mVertexShaderResID = 0;
        int mFragmentShaderResID = 0;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        EffectInfo info = new EffectInfo();
        info.mIntent = new Intent(this,
                com.gomdev.effect.whitehole.WhiteholeActivity.class);
        info.mVertexShaderResID = R.raw.whitehole_vs;
        info.mFragmentShaderResID = R.raw.whitehole_fs;

        mEffectMap.put("Whitehole", info);

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
            editor.putInt(EffectConfig.PREF_VS_RES_ID, info.mVertexShaderResID);
            editor.putInt(EffectConfig.PREF_FS_RES_ID,
                    info.mFragmentShaderResID);

            String savedFileName = EffectUtils.getSavedFilePath(
                    EffectListActivity.this, effectName,
                    EffectConfig.SHADER_TYPE_VS);
            editor.putString(EffectConfig.PREF_VS_FILE_NAME, savedFileName);

            savedFileName = EffectUtils.getSavedFilePath(
                    EffectListActivity.this, effectName,
                    EffectConfig.SHADER_TYPE_FS);
            editor.putString(EffectConfig.PREF_FS_FILE_NAME, savedFileName);

            editor.commit();

            if (DEBUG) {
                Log.d(TAG, "onItemClick() item=" + effectName);
            }
            startActivity(info.mIntent);
        }
    };
}
