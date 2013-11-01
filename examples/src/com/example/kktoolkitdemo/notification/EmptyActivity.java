package com.example.kktoolkitdemo.notification;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.KKServiceActivity;

/**
 * Created by gigichien on 13/10/25.
 */
public class EmptyActivity extends KKServiceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout mRoot = new FrameLayout(this);
        TextView mText = new TextView(this);
        mRoot.addView(mText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mText.setText("Test Activity");
        mText.setGravity(Gravity.CENTER);
        this.setContentView(mRoot);
    }
}
