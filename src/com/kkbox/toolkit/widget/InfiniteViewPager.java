/* Copyright (C) 2014 KKBOX Inc.
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
package com.kkbox.toolkit.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;


public class InfiniteViewPager extends ViewPager {

	public static abstract class OnInfiniteViewPagerPageChangeListener {

		public abstract void onLoopPageSelected(int position, boolean isManual);

		public abstract void onPageScrolledLeft(boolean isManual);

		public abstract void onPageScrolledRight(boolean isManual);
	}

	private OnInfiniteViewPagerPageChangeListener onInfiniteViewPagerPageChangeListener;
	private OnPageChangeListener listener;
	private int currentPosition = 0;
	private boolean isManual = false;
	private boolean isLoopEnable = false;

	public InfiniteViewPager(Context context) {
		super(context);
		super.setOnPageChangeListener(onPageChangeListener);
	}

	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setOnPageChangeListener(onPageChangeListener);
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

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.listener = listener;
	}

	public void setOnInfiniteViewPagerPageChangeListener(OnInfiniteViewPagerPageChangeListener listener) {
		onInfiniteViewPagerPageChangeListener = listener;
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageScrolled(int i, float v, int i2) {
			if (listener != null) {
				listener.onPageScrolled(i, v, i2);
			}
		}

		@Override
		public void onPageSelected(int position) {
			int index = getCurrentItem();

			if (onInfiniteViewPagerPageChangeListener != null) {
				onInfiniteViewPagerPageChangeListener.onLoopPageSelected(index, isManual);
			}
			if ((currentPosition > position) || (isLoopEnable && position == 0)) {
				if (onInfiniteViewPagerPageChangeListener != null) {
					onInfiniteViewPagerPageChangeListener.onPageScrolledLeft(isManual);
				}
			} else if ((currentPosition < position) || (isLoopEnable && position == getAdapter().getCount() - 1)) {
				if (onInfiniteViewPagerPageChangeListener != null) {
					onInfiniteViewPagerPageChangeListener.onPageScrolledRight(isManual);
				}
			}
			isManual = false;
			currentPosition = position;
			if (listener != null) {
				listener.onPageSelected(index);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_SETTLING) {
				isManual = true;
			} else if (state == ViewPager.SCROLL_STATE_IDLE) {
				isManual = false;
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
			if (listener != null) {
				listener.onPageScrollStateChanged(state);
			}
		}
	};

}
