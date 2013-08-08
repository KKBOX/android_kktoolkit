package com.kkbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ResizableView extends View {

	public ResizableView(Context context) {
		this(context, null);
	}

	public ResizableView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ResizableView(Context context, AttributeSet attrs, int defStyle) {
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
