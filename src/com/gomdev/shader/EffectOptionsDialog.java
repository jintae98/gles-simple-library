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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EffectContext context = EffectContext.getInstance();

        final int numOfOptions = EffectConfig.EFFECT_OPTIONS.length;
        String[] options = new String[numOfOptions];
        
        if (mCheckedItem == null) {
            mCheckedItem = new boolean[numOfOptions];
        }

        for (int i = 0; i < numOfOptions; i++) {
            options[i] = EffectConfig.EFFECT_OPTIONS[i].getOption();
        }

        Options option = null;
        for (int i = 0; i < numOfOptions; i++) {
            option = EffectConfig.EFFECT_OPTIONS[i];
            switch (option) {
            case SHOW_FPS:
                mCheckedItem[i] = context.showFPS();
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.effect_shader_list)
                .setMultiChoiceItems(options, mCheckedItem,
                        new OnMultiChoiceClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which, boolean isChecked) {
                                mCheckedItem[which] = isChecked;
                            }
                        })
                .setPositiveButton("OK", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Options option = null;
                        for (int i = 0; i < numOfOptions; i++) {
                            option = EffectConfig.EFFECT_OPTIONS[i];
                            switch (option) {
                            case SHOW_FPS:
                                context.setShowFPS(mCheckedItem[i]);
                                break;
                            }
                        }
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
