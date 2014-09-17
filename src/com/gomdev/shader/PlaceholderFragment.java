package com.gomdev.shader;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaceholderFragment extends Fragment {
    private int mFragmentLayoutID = -1;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(mFragmentLayoutID, container,
                false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        mFragmentLayoutID = ((ShaderActivity) activity).getLayoutID();
        super.onAttach(activity);
    }

}
