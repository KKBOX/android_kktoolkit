package com.kkbox.toolkit.ui;

import android.support.v4.view.ViewPager;

import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;

public abstract class OnInfiniteViewPagerPageChangeListener {

	public OnInfiniteViewPagerPageChangeListener(InfiniteViewPager viewPager) {
	}

	public OnInfiniteViewPagerPageChangeListener() {
	}

	public abstract void onLoopPageSelected(int position);

	public abstract void onPageScrollLeft();

	public abstract void onPageScrollRight();
}
