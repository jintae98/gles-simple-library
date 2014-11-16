package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.gles.GLESConfig;
import com.gomdev.gles.GLESConfig.Version;
import com.gomdev.shader.ShaderConfig.Options;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class OptionsFragment extends MainFragment {
    static final String CLASS = "ShaderOptionsDialog";
    static final String TAG = ShaderConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = ShaderConfig.DEBUG;

    private boolean[] mCheckedItem = null;
    private ArrayList<String> mOptions = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter = null;

    private SharedPreferences mPref = null;
    private SharedPreferences.Editor mPrefEditor = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onCreateView() " + this);
        }

        mPref = getActivity().getSharedPreferences(ShaderConfig.PREF_NAME, 0);
        mPrefEditor = mPref.edit();

        final ShaderContext context = ShaderContext.getInstance();

        boolean showInfo = mPref.getBoolean(ShaderConfig.PREF_SHOW_INFO, true);
        context.setShowInfo(showInfo);
        boolean showFPS = mPref.getBoolean(ShaderConfig.PREF_SHOW_FPS, true);
        context.setShowFPS(showFPS);
        boolean useGLES30 = mPref.getBoolean(ShaderConfig.PREF_USE_GLES_30,
                GLESConfig.GLES_VERSION == Version.GLES_30);
        context.setUseGLES30(useGLES30);

        final int numOfOptions = ShaderConfig.EFFECT_OPTIONS.length;
        if (showInfo == true) {
            for (int i = 0; i < numOfOptions; i++) {
                mOptions.add(ShaderConfig.EFFECT_OPTIONS[i].getOption());
            }
        } else {
            mOptions.add(ShaderConfig.EFFECT_OPTIONS[Options.SHOW_INFO
                    .getIndex()].getOption());
        }

        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice, mOptions);

        final ListView listView = makeMultipleChoiceList(numOfOptions);

        if (mCheckedItem == null) {
            mCheckedItem = new boolean[numOfOptions];
        }

        Options option = null;
        for (int i = 0; i < numOfOptions; i++) {
            option = ShaderConfig.EFFECT_OPTIONS[i];
            switch (option) {
            case SHOW_INFO:
                mCheckedItem[i] = context.showInfo();
                listView.setItemChecked(i, context.showInfo());
                break;
            case SHOW_FPS:
                mCheckedItem[i] = context.showFPS();
                ;
                listView.setItemChecked(i, context.showFPS());
                break;
            case USE_GLES30:
                mCheckedItem[i] = context.useGLES30();
                listView.setItemChecked(i, context.useGLES30());
                break;

            }
        }

        return listView;
    }

    private ListView makeMultipleChoiceList(final int numOfOptions) {
        final ShaderContext context = ShaderContext.getInstance();

        Activity activity = getActivity();
        final ListView listView = new ListView(activity);
        listView.setAdapter(mAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                setupOptionsMenu(context, numOfOptions, listView);

                Options option = ShaderConfig.EFFECT_OPTIONS[position];
                switch (option) {
                case SHOW_INFO:
                    context.setShowInfo(mCheckedItem[position]);
                    mPrefEditor.putBoolean(
                            ShaderConfig.PREF_SHOW_INFO,
                            mCheckedItem[position]);
                    break;
                case SHOW_FPS:
                    context.setShowFPS(mCheckedItem[position]);
                    mPrefEditor.putBoolean(
                            ShaderConfig.PREF_SHOW_FPS,
                            mCheckedItem[position]);
                    break;
                case USE_GLES30:
                    context.setUseGLES30(mCheckedItem[position]);
                    mPrefEditor.putBoolean(
                            ShaderConfig.PREF_USE_GLES_30,
                            mCheckedItem[position]);
                    break;
                default:
                    break;
                }

                mPrefEditor.commit();
            }

        });
        return listView;
    }

    private void setupOptionsMenu(final ShaderContext context,
            final int numOfOptions, final ListView listView) {
        SparseBooleanArray sb = listView.getCheckedItemPositions();

        if (sb.size() != 0) {
            for (int i = 0; i < mCheckedItem.length; i++) {
                mCheckedItem[i] = sb.get(i, false);
            }
        }

        boolean showInfo = context.showInfo();
        if (sb.size() != 0) {
            int startIndex = ShaderConfig.Options.SHOW_INFO.getIndex() + 1;
            if (sb.get(0) == false) {
                if (showInfo == true) {

                    for (int i = listView.getCount() - 1; i >= startIndex; i--) {
                        mOptions.remove(i);
                    }
                    context.setShowInfo(false);

                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (showInfo == false) {
                    for (int i = startIndex; i < numOfOptions; i++) {
                        mOptions.add(ShaderConfig.EFFECT_OPTIONS[i]
                                .getOption());
                    }
                    context.setShowInfo(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    int getFragmentPosition() {
        return MainActivity.TAB_OPTIONS_POSITION;
    }
}
