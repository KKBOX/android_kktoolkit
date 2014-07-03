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
		//This is maximum of viewPager can access
		return objects.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
}
