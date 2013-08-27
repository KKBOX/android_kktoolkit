package com.kkbox.toolkit.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;

public class InfiniteViewPager extends ViewPager {

	public InfiniteViewPager(Context context) {
		super(context);
	}
	
	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setCurrentItem (int item, boolean smoothScroll) {
		InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter)getAdapter();
		if (adapter.isLoopEnabled()) {
			super.setCurrentItem(item + 1, smoothScroll);
		} else {
			super.setCurrentItem(item, smoothScroll);
		}
	}
	
	@Override
	public void setCurrentItem (int item) {
		setCurrentItem(item, true);
	}
}
