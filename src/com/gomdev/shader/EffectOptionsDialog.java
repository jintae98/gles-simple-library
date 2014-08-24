package com.gomdev.shader;

import com.gomdev.shader.EffectConfig.Options;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

public class EffectOptionsDialog extends DialogFragment {
    private boolean[] mCheckedItem = null;
    private String[] mOptions = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EffectContext context = EffectContext.getInstance();

        final int numOfShowOptions = EffectConfig.EFFECT_SHOW_OPTIONS.length;
        mOptions = new String[numOfShowOptions];
        for(int i = 0; i < numOfShowOptions; i++) {
            mOptions[i] = EffectConfig.EFFECT_SHOW_OPTIONS[i].getOption();
        }

        if (mCheckedItem == null) {
            mCheckedItem = new boolean[numOfShowOptions];
        }

        Options option = null;
        for (int i = 0; i < numOfShowOptions; i++) {
            option = EffectConfig.EFFECT_SHOW_OPTIONS[i];
            switch (option) {
            case SHOW_INFO:
                mCheckedItem[i] = context.showInfo();
                break;
            case SHOW_FPS:
                mCheckedItem[i] = context.showFPS();
                break;
            case USE_GLES30:
                mCheckedItem[i] = context.useGLES30();
                break;
                
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.effect_shader_list)
                .setMultiChoiceItems(mOptions, mCheckedItem,
                        new OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which, boolean isChecked) {
                                mCheckedItem[which] = isChecked;
                                
                                Options option = EffectConfig.EFFECT_SHOW_OPTIONS[which];
                                if (option == Options.SHOW_INFO) {
                                    for (int i = which + 1; i < numOfShowOptions; i++) {
                                    }
                                }
                            }
                        })
                .setPositiveButton("OK", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Options option = null;
                        for (int i = 0; i < numOfShowOptions; i++) {
                            option = EffectConfig.EFFECT_SHOW_OPTIONS[i];
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
                            }
                        }
                        ((EffectListActivity)getActivity()).optionChanged();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                    }
                });
        return builder.create();
    }
}
