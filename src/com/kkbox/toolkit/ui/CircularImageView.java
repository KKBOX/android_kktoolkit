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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
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
        setMask (attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "maskColor"),
                attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "maskColorPressed"),
                attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "strokeColor"),
                attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "strokeWidth", 1));
	}

    public void setMask (String colorNormal, String colorPressed, String colorStroke, int strokeWidth) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        if (colorNormal != null) {
            GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.circular_image_mask);
            gradientDrawable.setColor(Color.parseColor(colorNormal));
            if (colorStroke != null) {
                gradientDrawable.setStroke(strokeWidth, Color.parseColor(colorStroke));
            }
            stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, gradientDrawable);
        }
        if (colorPressed != null) {
            GradientDrawable pressedDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.circular_image_mask);
            pressedDrawable.setColor(Color.parseColor(colorPressed));
            if (colorStroke != null) {
                pressedDrawable.setStroke(strokeWidth, Color.parseColor(colorStroke));
            }
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        }
        setImageDrawable(stateListDrawable);
    }
}