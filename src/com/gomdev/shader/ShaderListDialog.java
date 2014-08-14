package com.gomdev.shader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ShaderListDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences pref = getActivity()
                .getSharedPreferences(EffectConfig.PREF_NAME,
                        Context.MODE_PRIVATE);
        int numOfShaders = pref.getInt(EffectConfig.PREF_SHADER_COUNT, 1);
        String[] list = new String[numOfShaders];

        for (int i = 0; i < numOfShaders; i++) {
            list[i] = pref.getString(EffectConfig.PREF_SHADER_TITLE + i, "");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.shader_list_title)
                .setItems(list, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveSelectedShaderInfo(which);

                        Intent intent = new Intent(getActivity(),
                                com.gomdev.shader.ShaderViewActivity.class);
                        startActivity(intent);
                    }
                });
        return builder.create();
    }

    private void saveSelectedShaderInfo(int which) {
        Activity activity = getActivity();
        
        SharedPreferences pref = activity.getSharedPreferences(
                EffectConfig.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String shaderTitle = pref.getString(EffectConfig.PREF_SHADER_TITLE
                + which, "");
        int resID = pref.getInt(EffectConfig.PREF_SHADER_RES_ID + which, 0);

        String savedFileName = EffectUtils.getSavedFilePath(activity,
                shaderTitle);
        editor.putString(EffectConfig.PREF_SAVED_FILE_NAME, savedFileName);
        editor.putInt(EffectConfig.PREF_SAVED_RES_ID, resID);

        editor.commit();
    }
}
