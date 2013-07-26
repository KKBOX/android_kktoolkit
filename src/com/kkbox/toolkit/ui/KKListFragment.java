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
/**
 * KKListFragment
 */
package com.kkbox.toolkit.ui;

import android.view.View;

import com.kkbox.toolkit.R;

public abstract class KKListFragment extends KKFragment {
	private KKListView listView;

	private int scrollIndex = 0;
	private int scrollPositionToTop = 0;

	public KKListFragment() {}

	public KKListView getKKListView() {
		return listView;
	}

	@Override
	protected void onLoadUI() {
		super.onLoadUI();
		listView.setSelectionFromTop(scrollIndex, scrollPositionToTop);
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		listView = (KKListView)view.findViewById(R.id.listview);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveListViewPosition();
	}
	
	public void saveListViewPosition() {
		if (listView.getChildCount() > 0) {
			scrollIndex = listView.getFirstVisiblePosition();
			scrollPositionToTop = listView.getChildAt(0).getTop();
		}
	}
}