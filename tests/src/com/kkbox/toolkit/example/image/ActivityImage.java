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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.kkbox.toolkit.example.FakeData;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.ui.KKActivity;

import java.util.Random;

public class ActivityImage extends KKActivity {
	ImageView image;
	Button button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_example);
		getKKActionBar().setTitle("KKImageManager Example");
		
		image = (ImageView) findViewById(R.id.imageview);
		button = (Button) findViewById(R.id.btnChangeImage);
		KKImageManager imageManager = new KKImageManager(this, null);
		imageManager.updateViewBackground(image, FakeData.pic_url[0], null, R.drawable.ic_launcher);
		
		button.setOnClickListener(new OnClickListener() {	 
			@Override
			public void onClick(View arg0) {
				KKImageManager imageManager = new KKImageManager(ActivityImage.this, null);
		        Random ran = new Random();
				imageManager.updateViewBackground(image, FakeData.pic_url[ran.nextInt(6)], null, R.drawable.ic_launcher);				
			}
		});
	}
	

}
