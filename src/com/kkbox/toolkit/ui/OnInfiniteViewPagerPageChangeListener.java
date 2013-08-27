package com.kkbox.toolkit.ui;

import android.support.v4.view.ViewPager;

import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;

public abstract class OnInfiniteViewPagerPageChangeListener implements ViewPager.OnPageChangeListener {

	private InfiniteViewPager viewPager;
	private float currentPosition = 0;
	private boolean scrolled = false;

	public OnInfiniteViewPagerPageChangeListener(InfiniteViewPager viewPager) {
		this.viewPager = viewPager;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		scrolled = true;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter)viewPager.getAdapter();
		if (adapter.isLoopEnabled()) {
			if (position == 0) {
				if (scrolled) {
					scrolled = false;
					onPageScrollLeft();
				}
				viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 3, false);
				currentPosition = viewPager.getAdapter().getCount() - 2;
				return;
			} else if (position == viewPager.getAdapter().getCount() - 1) {
				if (scrolled) {
					scrolled = false;
					onPageScrollRight();
				}
				viewPager.setCurrentItem(0, false);
				currentPosition = 1;
				return;
			}
			onLoopPageSelected(position - 1);
		} else {
			onLoopPageSelected(position);
		}
		if (scrolled) {
			if (currentPosition > position) {
				onPageScrollLeft();
			} else if (currentPosition < position) {
				onPageScrollRight();
			}
			scrolled = false;
		}
		currentPosition = position;
	}

	public abstract void onLoopPageSelected(int position);

	public abstract void onPageScrollLeft();

	public abstract void onPageScrollRight();
}
