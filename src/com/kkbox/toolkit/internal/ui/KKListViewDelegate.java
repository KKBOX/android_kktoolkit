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
package com.kkbox.toolkit.internal.ui;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkbox.toolkit.R;
import com.kkbox.toolkit.utils.StringUtils;

public class KKListViewDelegate {
	private static class State {
		public static final int NORMAL = 0;
		public static final int PULLING = 1;
		public static final int PULLING_DOWN = 2;
		public static final int UPDATING = 3;
		public static final int LOADING_MORE = 4;
	}

	private final ListView.OnScrollListener onScrollListener = new ListView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			headerViewIsFirstItem = (firstVisibleItem == 0) ? true : false;
			boolean footerViewIsLastItem = false;
			if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
				footerViewIsLastItem = true;
			}
			if (footerViewIsLastItem && currentState == State.NORMAL && footerViewAdded) {
				updateState(State.LOADING_MORE);
			}
		}
	};

	private ProgressBar progressPullToRefresh;
	private View refreshHeaderView;
	private View footerView;
	private boolean footerViewAdded = false;
	private TextView labelPullToRefresh;
	private TextView labelPullToRefreshUpdatedAt;
	private ImageView viewPullToRefresh;
	private RotateAnimation animationFlip;
	private RotateAnimation animationReverseFlip;
	private KKListViewOnRefreshListener onRefreshListener;
	private KKListViewOnLoadMoreListener onLoadMoreListener;
	private int refreshHeaderhViewOriginHeight = 0;
	private int firstItemToTopHeight = 0;
	private float actionDownY = 0;
	private int currentState = State.NORMAL;
	private boolean headerViewIsFirstItem = false;
	private Context context;
	private ListView listView;

	public KKListViewDelegate(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
	}

	public void setPullToRefresh(KKListViewOnRefreshListener onRefreshListener) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headerView = inflater.inflate(R.layout.listview_header_pull_to_refresh, null);
		refreshHeaderView = (RelativeLayout)headerView.findViewById(R.id.layout_pull_to_refresh_header);
		labelPullToRefresh = (TextView)refreshHeaderView.findViewById(R.id.label_pull_to_refresh);
		labelPullToRefreshUpdatedAt = (TextView)refreshHeaderView.findViewById(R.id.label_pull_to_refresh_updated_at);
		viewPullToRefresh = (ImageView)refreshHeaderView.findViewById(R.id.view_pull_to_refresh);
		progressPullToRefresh = (ProgressBar)refreshHeaderView.findViewById(R.id.progress_pull_to_refresh);
		animationFlip = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animationFlip.setInterpolator(new LinearInterpolator());
		animationFlip.setDuration(250);
		animationFlip.setFillAfter(true);
		animationReverseFlip = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animationReverseFlip.setInterpolator(new LinearInterpolator());
		animationReverseFlip.setDuration(250);
		animationReverseFlip.setFillAfter(true);
		refreshHeaderhViewOriginHeight = (int)(60 * context.getResources().getDisplayMetrics().density);
		listView.addHeaderView(headerView, null, false);
		String lastUpdatedTime = (String)context.getResources().getText(R.string.last_update) + " "
				+ StringUtils.timeMillisToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
		labelPullToRefreshUpdatedAt.setText(lastUpdatedTime);
		listView.setOnScrollListener(onScrollListener);
		this.onRefreshListener = onRefreshListener;
	}

	public void setLoadMore(KKListViewOnLoadMoreListener onLoadMoreListener) {
		if (!footerViewAdded) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			footerView = inflater.inflate(R.layout.listview_item_load_more, null);
			listView.addFooterView(footerView);
			footerViewAdded = true;
		}
		this.onLoadMoreListener = onLoadMoreListener;
	}

	private void updateState(int newState) {
		if (newState != currentState) {
			currentState = newState;
			switch (currentState) {
				case State.NORMAL:
					resetHeaderViewHeight(0);
					break;
				case State.PULLING:
					labelPullToRefresh.setText(R.string.pull_down_to_resort);
					progressPullToRefresh.setVisibility(View.GONE);
					viewPullToRefresh.setVisibility(View.VISIBLE);
					viewPullToRefresh.clearAnimation();
					viewPullToRefresh.startAnimation(animationReverseFlip);
					break;
				case State.PULLING_DOWN:
					labelPullToRefresh.setText(R.string.release_in_order_to_resort);
					viewPullToRefresh.clearAnimation();
					viewPullToRefresh.startAnimation(animationFlip);
					break;
				case State.UPDATING:
					resetHeaderViewHeight(refreshHeaderhViewOriginHeight);
					viewPullToRefresh.clearAnimation();
					viewPullToRefresh.setVisibility(View.GONE);
					progressPullToRefresh.setVisibility(View.VISIBLE);
					labelPullToRefresh.setText(R.string.updating);
					onRefreshListener.onRefresh();
					break;
				case State.LOADING_MORE:
					onLoadMoreListener.onLoadMore();
					break;
			}
		}
	}

	public void onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (currentState == State.PULLING) {
					resetHeaderViewHeight(0);
				}
				if (currentState == State.PULLING_DOWN) {
					updateState(State.UPDATING);
				}
				break;
			case MotionEvent.ACTION_DOWN:
				actionDownY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				if (currentState == State.NORMAL) {
					if (headerViewIsFirstItem && event.getY() - actionDownY > 10) {
						updateState(State.PULLING);
					}
				}
				if (currentState == State.PULLING) {
					setHeaderViewHeight((int)(event.getY() - actionDownY));
					if (firstItemToTopHeight <= 0) {
						updateState(State.NORMAL);
					} else if (firstItemToTopHeight > refreshHeaderhViewOriginHeight) {
						updateState(State.PULLING_DOWN);
					}
					event.setAction(MotionEvent.ACTION_CANCEL);
				}
				if (currentState == State.PULLING_DOWN) {
					setHeaderViewHeight((int)(event.getY() - actionDownY));
					if (firstItemToTopHeight < refreshHeaderhViewOriginHeight) {
						updateState(State.PULLING);
					}
					event.setAction(MotionEvent.ACTION_CANCEL);
				}
		}
	}

	public void setAdapter() {
		loadCompleted();
	}

	private void setHeaderViewHeight(int height) {
		firstItemToTopHeight = height;
		LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		if (firstItemToTopHeight <= 0) {
			firstItemToTopHeight = 0;
			containerLayoutParams.topMargin = refreshHeaderhViewOriginHeight * -1;
			containerLayoutParams.height = refreshHeaderhViewOriginHeight;
		} else if (firstItemToTopHeight <= refreshHeaderhViewOriginHeight) {
			containerLayoutParams.topMargin = firstItemToTopHeight - refreshHeaderhViewOriginHeight;
			containerLayoutParams.height = refreshHeaderhViewOriginHeight;
		} else {
			containerLayoutParams.topMargin = 0;
			containerLayoutParams.height = firstItemToTopHeight;
		}
		refreshHeaderView.setLayoutParams(containerLayoutParams);
	}

	private void resetHeaderViewHeight(final int targetHeight) {
		if (firstItemToTopHeight != targetHeight) {
			final int displacement = firstItemToTopHeight / 10;
			final Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				public void run() {
					if (firstItemToTopHeight > targetHeight) {
						setHeaderViewHeight(firstItemToTopHeight - displacement);
						handler.post(this);
					} else {
						firstItemToTopHeight = targetHeight;
						setHeaderViewHeight(targetHeight);
						handler.removeCallbacks(this);
					}
				}
			};
			handler.post(runnable);
		}
	}

	public void loadCompleted() {
		if (currentState == State.UPDATING) {
			if (labelPullToRefreshUpdatedAt != null) {
				String lastUpdatedTime = (String)context.getResources().getText(R.string.last_update) + " "
						+ StringUtils.timeMillisToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
				labelPullToRefreshUpdatedAt.setText(lastUpdatedTime);
			}
			if (!footerViewAdded && footerView != null) {
				listView.addFooterView(footerView);
				footerViewAdded = true;
			}
		}
		updateState(State.NORMAL);
	}

	public void loadMoreFinished() {
		listView.removeFooterView(footerView);
		footerViewAdded = false;
	}
}
