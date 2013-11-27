package com.example.kktoolkitdemo.circleImageView;

import android.os.Bundle;

import com.example.kktoolkitdemo.ExampleActivity;
import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.ui.CircularImageView;

public class CircleImageViewActivity extends ExampleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_circular_imageview);
		CircularImageView imageView = (CircularImageView)findViewById(R.id.imageview2);
		imageView.setMaskedImageResource(R.drawable.ic_launcher);
	}
}
