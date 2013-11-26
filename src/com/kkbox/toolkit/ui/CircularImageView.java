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
package com.kkbox.toolkit.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kkbox.toolkit.R;

public class CircularImageView extends ImageView {

	public CircularImageView(Context context) {
		this(context, null, 0);
	}

	public CircularImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		GradientDrawable gradientDrawable;
		if (attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res-auto", "roundedCorner", false)) {
			gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_corner_image_mask);
		} else {
			gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.circular_image_mask);
		}
		gradientDrawable.setColor(Color.parseColor(attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "maskColor")));
		setImageDrawable(gradientDrawable);
		setBackgroundResource(attrs.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "maskedImage", 0));
	}

	public void setMaskedImageResource(int resid) {
		setBackgroundResource(resid);
	}

	public void setMaskedImageDrawble(Drawable background) {
		setBackgroundDrawable(background);
	}
}