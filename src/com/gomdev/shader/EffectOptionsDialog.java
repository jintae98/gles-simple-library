package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.shader.EffectConfig.Options;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EffectOptionsDialog extends DialogFragment {
    private boolean[] mCheckedItem = null;
    private ArrayList<String> mOptions = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter = null;
    private DialogListener mListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EffectContext context = EffectContext.getInstance();

        final int numOfOptions = EffectConfig.EFFECT_OPTIONS.length;
        if (context.showInfo() == true) {
            for (int i = 0; i < numOfOptions; i++) {
                mOptions.add(EffectConfig.EFFECT_OPTIONS[i].getOption());
            }
        } else {
            mOptions.add(EffectConfig.EFFECT_OPTIONS[Options.SHOW_INFO
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
            option = EffectConfig.EFFECT_OPTIONS[i];
            switch (option) {
            case SHOW_INFO:
                mCheckedItem[i] = context.showInfo();
                listView.setItemChecked(i, context.showInfo());
                break;
            case SHOW_FPS:
                mCheckedItem[i] = context.showFPS();
                listView.setItemChecked(i, context.showFPS());
                break;
            case USE_GLES30:
                mCheckedItem[i] = context.useGLES30();
                listView.setItemChecked(i, context.useGLES30());
                break;

            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.effect_shader_list)
                .setView(listView)
                .setPositiveButton("OK", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SparseBooleanArray sb = listView
                                .getCheckedItemPositions();

                        if (sb.size() != 0) {
                            for (int i = 0; i < mCheckedItem.length; i++) {
                                mCheckedItem[i] = sb.get(i, false);
                            }
                        }

                        Options option = null;
                        for (int i = 0; i < numOfOptions; i++) {
                            option = EffectConfig.EFFECT_OPTIONS[i];
                            switch (option) {
                            case SHOW_INFO:
                                context.setShowInfo(mCheckedItem[i]);
                                break;
                            case SHOW_FPS:
                                context.setShowFPS(mCheckedItem[i]);
                                break;
                            case USE_GLES30:
                                context.setUseGLES30(mCheckedItem[i]);
                                break;
                            default:
                                break;
                            }
                        }
                        mListener.onDialogPositiveClick(EffectOptionsDialog.this);
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        mListener.onDialogPositiveClick(EffectOptionsDialog.this);
                    }
                });
        return builder.create();
    }

    private ListView makeMultipleChoiceList(final int numOfOptions) {
        final EffectContext context = EffectContext.getInstance();

        Activity activity = getActivity();
        final ListView listView = new ListView(activity);
        listView.setAdapter(mAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                setupOptionsMenu(context, numOfOptions, listView);
            }

        });
        return listView;
    }

    private void setupOptionsMenu(final EffectContext context,
            final int numOfOptions, final ListView listView) {
        SparseBooleanArray sb = listView.getCheckedItemPositions();
        boolean showInfo = context.showInfo();
        if (sb.size() != 0) {
            if (sb.get(0) == false) {
                if (showInfo == true) {
                    for (int i = listView.getCount() - 1; i >= 1; i--) {
                        mOptions.remove(i);
                    }
                    context.setShowInfo(false);

                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (showInfo == false) {
                    for (int i = 1; i < numOfOptions; i++) {
                        mOptions.add(EffectConfig.EFFECT_OPTIONS[i]
                                .getOption());
                    }
                    context.setShowInfo(true);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implment DiallogListener");
        }
    }

}
