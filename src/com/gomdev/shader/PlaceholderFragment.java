package com.gomdev.shader;

import com.gomdev.gles.GLESConfig;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaceholderFragment extends Fragment {
    private static final String CLASS = "PlaceholderFragment";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    
    private int mFragmentLayoutID = -1;
    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() mFragmentLayoutID=" + mFragmentLayoutID);
        View rootView = inflater.inflate(mFragmentLayoutID, container,
                false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        mFragmentLayoutID = ((ShaderActivity)activity).getLayoutID();
        Log.d(TAG, "onAttach() mFragmentLayoutID=" + mFragmentLayoutID);
        super.onAttach(activity);
    }
    
    
}
