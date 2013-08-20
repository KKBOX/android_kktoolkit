package com.kkbox.toolkit.listview.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class InfiniteViewPagerAdapter extends PagerAdapter {

	private ArrayList<Object> objects = new ArrayList<Object>();
	private boolean loopEnabled = false;

	public InfiniteViewPagerAdapter(ArrayList<Object> content, boolean loopEnabled) {
		super();
		this.loopEnabled = loopEnabled;
		if (loopEnabled) {
			objects.add(0, content.get(content.size() - 1));
		}
		objects.addAll(content);
		if (loopEnabled) {
			objects.add(content.size() + 1, content.get(0));
		}
	}

	public Object getItem(int position) {
		return objects.get(position);
	}

	public boolean isLoopEnabled() {
		return loopEnabled;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView((View)object);
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
