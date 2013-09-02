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
 * KKMessageView
 */
package com.kkbox.toolkit.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkbox.toolkit.R;

public class KKMessageView extends RelativeLayout {
	private Context context;

	public KKMessageView(Context context) {
		super(context);
		this.context = context;
	}

	public KKMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public KKMessageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public void hide() {
		setVisibility(View.GONE);
	}
	
	public void setCustomView(View view) {
		if (view != null) {
			removeAllViews();
		}
		addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void setSingleTextView(String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_empty_single_text, null);
		TextView labelText = (TextView)view.findViewById(R.id.label_text);
		labelText.setText(text);
		setCustomView(view);
	}

    public void setSingleBoldTextView(String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_empty_single_text, null);
        TextView labelText = (TextView)view.findViewById(R.id.label_text);
        labelText.setTextColor(Color.parseColor("#00aed8"));
        labelText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,22);
        labelText.setInputType(Typeface.BOLD);
        labelText.setText(text);
        setCustomView(view);
    }

	public void setMultiTextView(String text, String description) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_empty_multi_text, null);
		TextView labelText = (TextView)view.findViewById(R.id.label_text);
		labelText.setText(text);
		TextView labelDescription = (TextView)view.findViewById(R.id.label_description);
		labelDescription.setText(description);
		setCustomView(view);
	}

	public void show() {
		setVisibility(View.VISIBLE);
	}
}
