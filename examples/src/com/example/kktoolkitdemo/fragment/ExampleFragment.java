package com.example.kktoolkitdemo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kkbox.toolkit.ui.KKFragment;

/**
 * Created by gigichien on 13/10/22.
 */
public class ExampleFragment extends KKFragment{

    public enum TAB_STYLE{GRAY, YELLOW, BLUE};
    TAB_STYLE mStyle = TAB_STYLE.GRAY;

    public ExampleFragment() {
        super();
    }
    public ExampleFragment(TAB_STYLE style) {
        super();
        mStyle = style;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView v = new TextView(this.getActivity());
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        v.setGravity(Gravity.CENTER);
        v.setTextColor(Color.WHITE);
        switch (mStyle) {
            case GRAY:
                v.setText("Fragment 1");
                v.setBackgroundColor(Color.DKGRAY);
                break;
            case YELLOW:
                v.setText("Fragment 2");
                v.setBackgroundColor(Color.YELLOW);
                break;
            case BLUE:
                v.setText("Fragment 3");
                v.setBackgroundColor(Color.BLUE);
                break;
        }
        initView(v);
        return v;
    }
}
