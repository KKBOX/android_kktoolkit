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
 * KKDragAndDropListView
 */
package com.kkbox.toolkit.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.listview.adapter.ReorderListAdapter;
import com.kkbox.toolkit.utils.KKDebug;

public class KKDragAndDropListView extends ListView {
	private Object movingObject;
	private ImageView viewDrag;
	private LinearLayout layoutExpanded;
	private WindowManager windowManager;
	private WindowManager.LayoutParams dragViewParams;
	private int expandedViewIndex = -1;
	private int upperBound;
	private int lowerBound;
	private int height;
	private int dragPoint;
	private int listViewItemHeight;
	private boolean isLastItem;
	private int grabberId;

	public KKDragAndDropListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public KKDragAndDropListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		dragViewParams = new WindowManager.LayoutParams();
		dragViewParams.gravity = Gravity.TOP;
		dragViewParams.x = 0;
		dragViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dragViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		dragViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		dragViewParams.format = PixelFormat.TRANSLUCENT;
		dragViewParams.windowAnimations = 0;
		windowManager = (WindowManager)getContext().getSystemService("window");
	}

	public void setGrabberId(int resourceId) {
		grabberId = resourceId;
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		height = getHeight();
		upperBound = height / 3;
		lowerBound = height * 2 / 3;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		int x = (int)ev.getX();
		int y = (int)ev.getY();
		switch (action) {
			case MotionEvent.ACTION_UP:
				if (viewDrag != null) {
					windowManager.removeViewImmediate(viewDrag);
					viewDrag = null;
					ListAdapter adapter = getAdapter();
					if (isLastItem) {
						expandedViewIndex++;
					}
					((ReorderListAdapter)adapter).addAtPosition(expandedViewIndex, movingObject);
					unexpandView();
					return true;
				}
				break;
			case MotionEvent.ACTION_DOWN:
				if (viewDrag == null) {
					int itemIndex = pointToPosition(x, y);
					if (itemIndex != -1) {
						ViewGroup item = (ViewGroup)getChildAt(itemIndex - getFirstVisiblePosition());
						listViewItemHeight = item.getHeight();
						dragPoint = (int)ev.getRawY() - y + item.getTop() - y;
						View dragger = item.findViewById(grabberId);
						if ((dragger.getLeft() < x) && (x < dragger.getRight())) {
							item.destroyDrawingCache();
							item.buildDrawingCache();
							Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
							dragViewParams.y = y + dragPoint;
							viewDrag = new ImageView(getContext());
							viewDrag.setImageBitmap(bitmap);
							KKImageManager.autoRecycleViewSourceBitmap(viewDrag);
							windowManager.addView(viewDrag, dragViewParams);
							ListAdapter adapter = getAdapter();
							movingObject = ((ReorderListAdapter)adapter).removeAtPosition(itemIndex);
							upperBound = Math.min(y, height / 3);
							lowerBound = Math.max(y, height * 2 / 3);

							if (itemIndex == ((ReorderListAdapter)adapter).getCount()) {
								itemIndex--;
								layoutExpanded = (LinearLayout)getChildAt(itemIndex - getFirstVisiblePosition());
								layoutExpanded.setGravity(Gravity.TOP);
								isLastItem = true;
							} else {
								layoutExpanded = (LinearLayout)getChildAt(itemIndex - getFirstVisiblePosition());
								layoutExpanded.setGravity(Gravity.BOTTOM);
								isLastItem = false;
							}
							ViewGroup.LayoutParams viewParams = layoutExpanded.getLayoutParams();
							viewParams.height = listViewItemHeight * 2;
							layoutExpanded.setLayoutParams(viewParams);
							expandedViewIndex = itemIndex;
							return true;
						} else {
							break;
						}
					} else {
						break;
					}
				} else {
					break;
				}
			case MotionEvent.ACTION_MOVE:
				if (viewDrag != null) {
					int itemIndex = pointToPosition(x, y);
					if (itemIndex != -1) {
						if (y >= height / 3) {
							upperBound = height / 3;
						}
						if (y <= height * 2 / 3) {
							lowerBound = height * 2 / 3;
						}
						int speed = 0;
						if (y > lowerBound) {
							speed = y > (height + lowerBound) / 2 ? 16 : 4;
						} else if ((y < upperBound) && (getFirstVisiblePosition() != 0)) {
							speed = y < upperBound / 2 ? -16 : -4;
						}
						if (speed != 0) {
							int ref = pointToPosition(x, height / 2);
							if (ref == -1) {
								ref = pointToPosition(x, height / 2 + getDividerHeight());
							}
							View v = getChildAt(ref - getFirstVisiblePosition());
							setSelectionFromTop(ref, v.getTop() - speed);
						}
						if (expandedViewIndex != -1) {
							if (expandedViewIndex != itemIndex) {
								unexpandView();
							} else {
								if (pointToPosition(x, y + listViewItemHeight) != itemIndex) {
									itemIndex++;
									unexpandView();
								}
							}
						}
						layoutExpanded = (LinearLayout)getChildAt(itemIndex - getFirstVisiblePosition());
						if (layoutExpanded == null) {
							itemIndex--;
							layoutExpanded = (LinearLayout)getChildAt(itemIndex - getFirstVisiblePosition());
							layoutExpanded.setGravity(Gravity.TOP);
							isLastItem = true;
						} else {
							layoutExpanded.setGravity(Gravity.BOTTOM);
							isLastItem = false;
						}
						ViewGroup.LayoutParams viewParams = layoutExpanded.getLayoutParams();
						viewParams.height = listViewItemHeight * 2;
						layoutExpanded.setLayoutParams(viewParams);
						expandedViewIndex = itemIndex;
					}
					dragViewParams.y = y + dragPoint;
					windowManager.updateViewLayout(viewDrag, dragViewParams);
					return true;
				}
		}
		return super.onTouchEvent(ev);
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
}