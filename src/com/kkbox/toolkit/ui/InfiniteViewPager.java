package com.kkbox.toolkit.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;

public class InfiniteViewPager extends ViewPager {

	private OnPageChangeListener onPageChangeListener;
	private OnInfiniteViewPagerPageChangeListener onInfiniteViewPagerPageChangeListener;
	private float currentPosition = 0;
	private boolean scrolled = false;

	public InfiniteViewPager(Context context) {
		super(context);
		initListener();
	}

	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		initListener();
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter) getAdapter();
		if (adapter.isLoopEnabled()) {
			super.setCurrentItem(item + 1, smoothScroll);
		} else {
			super.setCurrentItem(item, smoothScroll);
		}
	}

	@Override
	public void setCurrentItem(int item) {
		setCurrentItem(item, true);
	}

	@Override
	public int getCurrentItem() {
		InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter) getAdapter();
		if (adapter.isLoopEnabled()) {
			return super.getCurrentItem() - 1;
		} else {
			return super.getCurrentItem();
		}
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		if (adapter instanceof InfiniteViewPagerAdapter) {
			if (((InfiniteViewPagerAdapter) adapter).isLoopEnabled()) {
				setCurrentItem(0);
			}
		}
	}

	public void setOnPageChangeListener(OnInfiniteViewPagerPageChangeListener listener) {
		onInfiniteViewPagerPageChangeListener = listener;
	}

	private void initListener() {
		onPageChangeListener = new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {
			}

			@Override
			public void onPageSelected(int position) {
				InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter) getAdapter();
				if (adapter.isLoopEnabled()) {
					if (position == 0) {
						if (scrolled) {
							scrolled = false;
							onPageScrollLeft();
						}
						return;
					} else if (position == adapter.getCount() - 1) {
						if (scrolled) {
							scrolled = false;
							onPageScrollRight();
						}
						return;
					}
				}
				if (scrolled) {
					scrolled = false;
					onLoopPageSelected(position - 1);

					if (currentPosition > position) {
						onPageScrollLeft();
					} else if (currentPosition < position) {
						onPageScrollRight();
					}
				}
				currentPosition = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_SETTLING) {
					scrolled = true;
				} else if (state == ViewPager.SCROLL_STATE_IDLE) {
					scrolled = false;
					InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter) getAdapter();
					if (adapter.isLoopEnabled()) {
						int currentItem = getCurrentItem();
						if (currentItem == adapter.getCount() - 2) {
							//last to first
							setCurrentItem(0, false);
							currentPosition = 1;
						} else if (currentItem == -1) {
							//first to last
							setCurrentItem(getAdapter().getCount() - 3, false);
							currentPosition = adapter.getCount() - 2;
						}
					}
				}
			}
		};

		setOnPageChangeListener(onPageChangeListener);
	}


	private void onPageScrollRight() {
		if (onInfiniteViewPagerPageChangeListener != null) {
			onInfiniteViewPagerPageChangeListener.onPageScrollRight();
		}
	}


	private void onPageScrollLeft() {
		if (onInfiniteViewPagerPageChangeListener != null) {
			onInfiniteViewPagerPageChangeListener.onPageScrollLeft();
		}
	}

	private void onLoopPageSelected(int position) {
		if (onInfiniteViewPagerPageChangeListener != null) {
			onInfiniteViewPagerPageChangeListener.onLoopPageSelected(position);
		}
	}

}
