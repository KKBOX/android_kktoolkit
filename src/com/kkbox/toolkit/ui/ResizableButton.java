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
import android.util.AttributeSet;
import android.widget.Button;

public class ResizableButton extends Button {

	public ResizableButton(Context context) {
		this(context, null);
	}

	public ResizableButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ResizableButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getBackground() == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = width * getBackground().getIntrinsicHeight() / getBackground().getIntrinsicWidth();

			super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		}
	}

}
