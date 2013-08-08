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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.kkbox.toolkit.internal.ui.KKListViewDelegate;
import com.kkbox.toolkit.internal.ui.KKListViewOnLoadMoreListener;
import com.kkbox.toolkit.internal.ui.KKListViewOnRefreshListener;

public class KKExpandableListView extends ExpandableListView {

	public static abstract class OnRefreshListener implements KKListViewOnRefreshListener {
		public abstract void onRefresh();
	}

	public static abstract class OnLoadMoreListener implements KKListViewOnLoadMoreListener {
		public abstract void onLoadMore();
	}

	private KKListViewDelegate delegate;

	public KKExpandableListView(Context context) {
		this(context, null);
	}

	public KKExpandableListView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public KKExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		delegate = new KKListViewDelegate(context, this);
	}

	public void setPullToRefresh(OnRefreshListener onRefreshListener) {
		delegate.setPullToRefresh(onRefreshListener);
	}

	public void setLoadMore(OnLoadMoreListener onLoadMoreListener) {
		delegate.setLoadMore(onLoadMoreListener);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		delegate.onTouchEvent(event);
		try {
			return super.onTouchEvent(event);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(adapter);
		delegate.setAdapter();
	}

	public void loadCompleted() {
		delegate.loadCompleted();
	}

	public void loadMoreFinished() {
		delegate.loadMoreFinished();
	}
}
