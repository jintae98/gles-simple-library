package com.gomdev.shader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeviceInfoDialog extends DialogFragment {
    enum GLES_VERSION {
        GLES_10,
        GLES_20,
        GLES_30,
        GLES_31
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = makeInfoView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.effect_list_device_info)
                .setView(view)
                .setPositiveButton("OK", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    @SuppressLint("InflateParams")
    private View makeInfoView() {
        Activity activity = getActivity();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.effect_device_info, null);

        // set GLESVersion
        TextView versionView = (TextView) layout.findViewById(R.id.version);
        GLES_VERSION version = getGLESVersion();
        switch (version) {
        case GLES_31:
            versionView.setText("OpenGL ES 3.1");
            break;
        case GLES_30:
            versionView.setText("OpenGL ES 3.0");
            break;
        case GLES_20:
            versionView.setText("OpenGL ES 2.0");
            break;
        case GLES_10:
            versionView.setText("OpenGL ES 1.0");
            break;
        default:
        }

        TextView extensionView = (TextView) layout
                .findViewById(R.id.extensions);
        String extensions = ShaderContext.getInstance().getExtensions();
        extensionView.setText(extensions);

        return layout;
    }

    private GLES_VERSION getGLESVersion() {
        Activity activity = getActivity();
        ActivityManager am = (ActivityManager) activity
                .getSystemService(Context.ACTIVITY_SERVICE);

        ConfigurationInfo info = am.getDeviceConfigurationInfo();

        if (info.reqGlEsVersion >= 0x31000) {
            return GLES_VERSION.GLES_31;
        } else if (info.reqGlEsVersion >= 0x30000) {
            return GLES_VERSION.GLES_30;
        } else if (info.reqGlEsVersion >= 0x20000) {
            return GLES_VERSION.GLES_20;
        } else if (info.reqGlEsVersion >= 0x10000) {
            return GLES_VERSION.GLES_10;
        }
        return GLES_VERSION.GLES_10;
    }
}
