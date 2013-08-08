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
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		} else {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = width * getBackground().getIntrinsicHeight() / getBackground().getIntrinsicWidth();

			setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
		}
	}

}
