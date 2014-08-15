package com.gomdev.shader;

import java.util.ArrayList;

import com.gomdev.shader.EffectContext.ShaderInfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ShaderListDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EffectContext context = EffectContext.getInstance();
        int numOfShaders = context.getNumOfShaders();
        String[] list = new String[numOfShaders];
        
        ArrayList<ShaderInfo> mShaderInfos = context.getShaderInfoList();

        for (int i = 0; i < numOfShaders; i++) {
            list[i] = mShaderInfos.get(i).mTitle;
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
        EffectContext context = EffectContext.getInstance();
        ArrayList<ShaderInfo> mShaderInfos = context.getShaderInfoList();
        
        ShaderInfo info = mShaderInfos.get(which);

        context.setSavedShaderInfo(info);
    }
}
