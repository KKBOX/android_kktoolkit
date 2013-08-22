package com.kkbox.toolkit.ui;

import android.support.v4.view.ViewPager;

import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;

public abstract class OnInfiniteViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

	private ViewPager viewPager;

	public OnInfiniteViewPagerPageChangeListener(ViewPager viewPager) {
		this.viewPager = viewPager;
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter)viewPager.getAdapter();
		if (adapter.isLoopEnabled()) {
			if (position == 0) {
				viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 2, false);
				return;
			}
			if (position == viewPager.getAdapter().getCount() - 1) {
				viewPager.setCurrentItem(1, false);
				return;
			}
			onLoopPageSelected(position - 1);
		} else {
			onLoopPageSelected(position);
		}
	}

	public abstract void onLoopPageSelected(int position);
}
