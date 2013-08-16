/* Copyright (C) 2013 KKBOX Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * ​http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * ActivityImage.java: This activity demonstrates the usage of KKImageManager.
 */
package com.kkbox.toolkit.example.image;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.kkbox.toolkit.example.FakeData;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.image.KKImageListener;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.ui.KKActivity;

import java.util.Random;

public class ActivityImage extends KKActivity {
	ImageView imageView;
	KKImageManager imageManager;

	private final KKImageListener imageListener = new KKImageListener() {
		@Override
		public void onReceiveBitmap(Bitmap bitmap) {
			imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
			imageManager.autoRecycleViewBackgroundBitmap(imageView);
		}
	};

	private final OnClickListener buttonAutoUpdateImageClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Random ran = new Random();
			imageManager.updateViewBackground(imageView, FakeData.pic_url[ran.nextInt(6)], null, R.drawable.ic_launcher);
		}
	};

	private final OnClickListener buttonManualUpdateImageClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Random ran = new Random();
			imageManager.loadBitmap(imageListener, FakeData.pic_url[ran.nextInt(6)], null);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_example);
		getKKActionBar().setTitle("KKImageManager Example");
		imageManager = new KKImageManager(this, null);
		imageView = (ImageView)findViewById(R.id.imageview);
		Button button = (Button)findViewById(R.id.button_auto_update_image);
		button.setOnClickListener(buttonAutoUpdateImageClickListener);

		button = (Button)findViewById(R.id.button_manual_update_image);
		button.setOnClickListener(buttonManualUpdateImageClickListener);
		imageManager.updateViewBackground(imageView, FakeData.pic_url[0], null, R.drawable.ic_launcher);
	}
}
