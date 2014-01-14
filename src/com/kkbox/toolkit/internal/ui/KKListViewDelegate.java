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
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkbox.toolkit.R;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.listview.adapter.ReorderExpendableListAdapter;
import com.kkbox.toolkit.listview.adapter.ReorderListAdapter;
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
			if (onRefreshListener != null) {
				headerViewIsFirstItem = (firstVisibleItem == 0) ? true : false;
			}
			if (onLoadMoreListener != null) {
				boolean footerViewIsLastItem = false;
				if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
					footerViewIsLastItem = true;
				}
				if (footerViewIsLastItem && currentState == State.NORMAL && footerViewAdded) {
					updateState(State.LOADING_MORE);
				}
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
	private KKListViewOnRefreshListener onRefreshListener;
	private KKListViewOnLoadMoreListener onLoadMoreListener;
	private int refreshHeaderhViewOriginHeight = 0;
	private int firstItemToTopHeight = 0;
	private float actionDownY = 0;
	private int currentState = State.NORMAL;
	private boolean headerViewIsFirstItem = false;
	private Context context;
	private ListView listView;

	private Object movingObject;
	private Object movingChildObject;
	private ImageView viewDrag;
	private View layoutExpanded;
	private WindowManager windowManager;
	private WindowManager.LayoutParams dragViewParams;
	private int expandedViewIndex = -1;
	private int upperBound;
	private int lowerBound;
	private int dragPoint;
	private int listViewItemHeight;
	private boolean isLastItem;
	private Integer dragAndDropResourceId;

	public KKListViewDelegate(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;
		listView.setOnScrollListener(onScrollListener);
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
		refreshHeaderhViewOriginHeight = (int)(60 * context.getResources().getDisplayMetrics().density);
		listView.addHeaderView(headerView, null, false);
		String lastUpdatedTime = (String)context.getResources().getText(R.string.last_update) + " "
				+ StringUtils.timeMillisToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
		labelPullToRefreshUpdatedAt.setText(lastUpdatedTime);
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

	public void onInterceptTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			actionDownY = event.getY();
		}
	}

	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (currentState == State.PULLING) {
					resetHeaderViewHeight(0);
				}
				if (currentState == State.PULLING_DOWN) {
					updateState(State.UPDATING);
				}
				if(canDragAndDrop()) {
					return onDragAndDropActionUp();
				}
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
				if(canDragAndDrop()) {
					return onDragAndDropActionMove(event);
				}
				break;
			case MotionEvent.ACTION_DOWN:
				if(canDragAndDrop()) {
					return onDragAndDropActionDown(event);
				}
				break;
		}
		return false;
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

	public void setDragAndDropResourceId(Integer resourceId) {
		dragAndDropResourceId = resourceId;
		onSizeChanged();
		setDragView();
	}

	private void setDragView() {
		dragViewParams = new WindowManager.LayoutParams();
		dragViewParams.gravity = Gravity.TOP;
		dragViewParams.x = 0;
		dragViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dragViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		dragViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		dragViewParams.format = PixelFormat.TRANSLUCENT;
		dragViewParams.windowAnimations = 0;
		windowManager = (WindowManager)this.context.getSystemService("window");
	}

	public void onSizeChanged() {
		if(dragAndDropResourceId != null) {
			final int height = this.listView.getHeight();
			upperBound = height / 3;
			lowerBound = height * 2 / 3;
		}
	}

	private void unexpandView() {
		if (layoutExpanded != null) {
			ViewGroup.LayoutParams viewParams = layoutExpanded.getLayoutParams();
			viewParams.height = listViewItemHeight;
			layoutExpanded.setLayoutParams(viewParams);
			layoutExpanded = null;
		}
		expandedViewIndex = -1;
	}

	public boolean onDragAndDropActionUp() {
		if (viewDrag != null) {
			windowManager.removeViewImmediate(viewDrag);
			viewDrag = null;

			Object adapter;
			int counter;
			if(listView instanceof ExpandableListView) {
				final ExpandableListAdapter tempAdapter = ((ExpandableListView) listView).getExpandableListAdapter();
				counter = tempAdapter.getGroupCount();
				adapter = tempAdapter;
			} else {
				final ListAdapter tempAdapter = listView.getAdapter();
				counter = tempAdapter.getCount();
				adapter = tempAdapter;
			}

			if (isLastItem) {
				expandedViewIndex++;
			}

			if (counter > 0) {
				((ReorderListAdapter) adapter).addAtPosition(expandedViewIndex, movingObject);
				if(isExpandable() && movingChildObject != null) {
					((ReorderExpendableListAdapter) adapter).addExpendChildAtGroupPosition(expandedViewIndex, movingChildObject);
				}
			} else {
				((ReorderListAdapter) adapter).addAtPosition(0, movingObject);
				if(isExpandable() && movingChildObject != null) {
					((ReorderExpendableListAdapter) adapter).addExpendChildAtGroupPosition(0, movingChildObject);
				}
			}
			unexpandView();
			return true;
		}
		return false;
	}

	public boolean onDragAndDropActionDown(MotionEvent ev) {
		int x = (int)ev.getX();
		int y = (int)ev.getY();
		if (viewDrag == null) {
			int itemIndex = this.listView.pointToPosition(x, y);
			if (itemIndex != -1) {
				ViewGroup item = (ViewGroup)this.listView.getChildAt(itemIndex - this.listView.getFirstVisiblePosition());
				if(item == null) {
					return false;
				}
				final int height = this.listView.getHeight();
				listViewItemHeight = item.getHeight();
				dragPoint = (int)ev.getRawY() - y + item.getTop() - y;
				View dragger = item.findViewById(dragAndDropResourceId);
				if ((dragger.getLeft() < x) && (x < dragger.getRight())) {
					item.destroyDrawingCache();
					item.buildDrawingCache();
					Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
					dragViewParams.y = y + dragPoint;
					viewDrag = new ImageView(this.context);
					viewDrag.setImageBitmap(bitmap);
					KKImageManager.autoRecycleViewSourceBitmap(viewDrag);
					windowManager.addView(viewDrag, dragViewParams);

					Object adapter;
					int counter;
					if(listView instanceof ExpandableListView) {
						final ExpandableListAdapter tempAdapter = ((ExpandableListView) listView).getExpandableListAdapter();
						counter = tempAdapter.getGroupCount();
						adapter = tempAdapter;
					} else {
						final ListAdapter tempAdapter = listView.getAdapter();
						counter = tempAdapter.getCount();
						adapter = tempAdapter;

					}

					movingObject = ((ReorderListAdapter)adapter).removeAtPosition(itemIndex);
					if(isExpandable()) {
						movingChildObject = ((ReorderExpendableListAdapter)adapter).removeExpendChildAtGroupPosition(itemIndex);
					}

					upperBound = Math.min(y, height / 3);
					lowerBound = Math.max(y, height * 2 / 3);
					if (counter > 0) {
						if (itemIndex == counter) {
							itemIndex--;
							View childView = this.listView.getChildAt(itemIndex - this.listView.getFirstVisiblePosition());
							if(childView instanceof RelativeLayout) {
								((RelativeLayout) childView).setGravity(Gravity.TOP);
							} else {
								((LinearLayout) childView).setGravity(Gravity.TOP);
							}
							layoutExpanded = childView;
							isLastItem = true;
						} else {
							View childView = this.listView.getChildAt(itemIndex - this.listView.getFirstVisiblePosition());
							if(childView instanceof RelativeLayout) {
								((RelativeLayout) childView).setGravity(Gravity.BOTTOM);
							} else {
								((LinearLayout) childView).setGravity(Gravity.BOTTOM);
							}
							layoutExpanded = childView;
							isLastItem = false;
						}
						ViewGroup.LayoutParams viewParams = layoutExpanded.getLayoutParams();
						viewParams.height = listViewItemHeight * 2;
						layoutExpanded.setLayoutParams(viewParams);
						expandedViewIndex = itemIndex;
					}
					return true;
				}
			}
		}
		return false;
	}

	public boolean onDragAndDropActionMove(MotionEvent ev) {
		int x = (int)ev.getX();
		int y = (int)ev.getY();
		if (viewDrag != null) {
			int itemIndex = this.listView.pointToPosition(x, y);
			if (itemIndex != -1) {
				final int height = this.listView.getHeight();
				if (y >= height / 3) {
					upperBound = height / 3;
				}
				if (y <= height * 2 / 3) {
					lowerBound = height * 2 / 3;
				}
				int speed = 0;
				if (y > lowerBound) {
					speed = y > (height + lowerBound) / 2 ? 16 : 4;
				} else if ((y < upperBound) && (this.listView.getFirstVisiblePosition() != 0)) {
					speed = y < upperBound / 2 ? -16 : -4;
				}
				if (speed != 0) {
					int ref = this.listView.pointToPosition(x, height / 2);
					if (ref == -1) {
						ref = this.listView.pointToPosition(x, height / 2 + this.listView.getDividerHeight());
					}
					View v = this.listView.getChildAt(ref - this.listView.getFirstVisiblePosition());
					this.listView.setSelectionFromTop(ref, v.getTop() - speed);
				}
				if (expandedViewIndex != -1) {
					if (expandedViewIndex != itemIndex) {
						unexpandView();
					} else {
						if (this.listView.pointToPosition(x, y + listViewItemHeight) != itemIndex) {
							itemIndex++;
							unexpandView();
						}
					}
				}
				if (this.listView.getAdapter().getCount() > 0) {
					View childView = this.listView.getChildAt(itemIndex - this.listView.getFirstVisiblePosition());
					if (childView == null) {
						itemIndex--;
						childView = this.listView.getChildAt(itemIndex - this.listView.getFirstVisiblePosition());
						if(childView instanceof RelativeLayout) {
							((RelativeLayout) childView).setGravity(Gravity.TOP);
						} else {
							((LinearLayout) childView).setGravity(Gravity.TOP);
						}
						layoutExpanded = childView;
						isLastItem = true;
					} else {
						if(childView instanceof RelativeLayout) {
							((RelativeLayout) childView).setGravity(Gravity.BOTTOM);
						} else {
							((LinearLayout) childView).setGravity(Gravity.BOTTOM);
						}
						layoutExpanded = childView;
						isLastItem = false;
					}
					ViewGroup.LayoutParams viewParams = layoutExpanded.getLayoutParams();
					viewParams.height = listViewItemHeight * 2;
					layoutExpanded.setLayoutParams(viewParams);
					expandedViewIndex = itemIndex;
				}
			}
			dragViewParams.y = y + dragPoint;
			windowManager.updateViewLayout(viewDrag, dragViewParams);
			return true;
		}
		return false;
	}

	private boolean canDragAndDrop() {
		return dragAndDropResourceId != null && !haveExpanded();
	}

	private boolean isExpandable() {
		return listView instanceof ExpandableListView;
	}

	private boolean haveExpanded() {
		if(isExpandable()) {
			for(int i = 0; i < listView.getCount(); i++) {
				if(((ExpandableListView) listView).isGroupExpanded(i)) {
					return true;
				}
			}
		}
		return false;
	}
}
