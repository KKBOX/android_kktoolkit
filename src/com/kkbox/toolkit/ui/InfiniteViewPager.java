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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;

public class InfiniteViewPager extends ViewPager {

	private OnInfiniteViewPagerPageChangeListener onInfiniteViewPagerPageChangeListener;
	private int currentPosition = 0;
	private boolean scrolled = false;
	private boolean isLoopEnable = false;

	public InfiniteViewPager(Context context) {
		super(context);
		setOnPageChangeListener(onPageChangeListener);
	}

	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnPageChangeListener(onPageChangeListener);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		if (isLoopEnable) {
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
		if (isLoopEnable) {
			final int index = super.getCurrentItem();
			final int count = getAdapter().getCount();
			if (index == 0) {
				return count - 3;
			} else if (index == count - 1) {
				return 0;
			} else {
				return index - 1;
			}
		} else {
			return super.getCurrentItem();
		}
	}

	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		if (adapter instanceof InfiniteViewPagerAdapter) {
			isLoopEnable = ((InfiniteViewPagerAdapter) adapter).isLoopEnabled();
			if (isLoopEnable) {
				setCurrentItem(0);
			}
		}
	}

	public void setOnPageChangeListener(OnInfiniteViewPagerPageChangeListener listener) {
		onInfiniteViewPagerPageChangeListener = listener;
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageScrolled(int i, float v, int i2) {
		}

		@Override
		public void onPageSelected(int position) {
			if (scrolled) {
				InfiniteViewPagerAdapter adapter = (InfiniteViewPagerAdapter) getAdapter();
				scrolled = false;
				if (onInfiniteViewPagerPageChangeListener != null) {
					onInfiniteViewPagerPageChangeListener.onLoopPageSelected(position);
				}
				if ((currentPosition > position) || (isLoopEnable && position == 0)) {
					if (onInfiniteViewPagerPageChangeListener != null) {
						onInfiniteViewPagerPageChangeListener.onPageScrollLeft();
					}
				} else if ((currentPosition < position) || (isLoopEnable && position == adapter.getCount() - 1)) {
					if (onInfiniteViewPagerPageChangeListener != null) {
						onInfiniteViewPagerPageChangeListener.onPageScrollRight();
					}
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
				if (isLoopEnable) {
					final int count = getAdapter().getCount();
					if (currentPosition == count - 1) {
						//last to first
						setCurrentItem(0, false);
						currentPosition = 1;
					} else if (currentPosition == 0) {
						//first to last
						setCurrentItem(count - 3, false);
						currentPosition = count - 2;
					}
				}
			}
		}
	};

}
