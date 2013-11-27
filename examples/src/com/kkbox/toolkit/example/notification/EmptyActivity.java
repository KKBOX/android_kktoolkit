package com.kkbox.toolkit.example.notification;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kkbox.toolkit.example.ExampleActivity;

public class EmptyActivity extends ExampleActivity {
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
