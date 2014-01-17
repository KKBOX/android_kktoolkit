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
package com.kkbox.toolkit.app;

import android.view.View;

import com.kkbox.toolkit.internal.app.KKListFragmentDelegate;
import com.kkbox.toolkit.widget.KKDragAndDropListView;
import com.kkbox.toolkit.widget.KKListView;

public abstract class KKListFragment extends KKFragment {
	private KKListFragmentDelegate delegate = new KKListFragmentDelegate();

	public KKListFragment() {}

	public KKDragAndDropListView getKKDragAndDropListView() {
		return (KKDragAndDropListView)delegate.getListView();
	}

	public KKListView getKKListView() {
		return (KKListView)delegate.getListView();
	}

	@Override
	protected void onLoadUI() {
		super.onLoadUI();
		delegate.onLoadUI();
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		delegate.initView(view);
	}

	@Override
	public void onPause() {
		super.onPause();
		delegate.onPause();
	}
	
	public void saveListViewPosition() {
		delegate.saveListViewPosition();
	}
}