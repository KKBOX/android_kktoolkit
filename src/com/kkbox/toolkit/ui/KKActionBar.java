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
 * KKActionBar
 */
package com.kkbox.toolkit.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkbox.toolkit.R;

import java.util.ArrayList;

public class KKActionBar {
	private Activity activity;
	private ActionBar actionBar;
	private LinearLayout actionBarCompat;
	private RelativeLayout buttonBack;
	private ImageView viewHomeUp;
	private TextView labelTitle;
	private TextView labelSubTitle;
	private LinearLayout viewTitle;
	private ImageView viewIcon;
	private ArrayList<KKMenuCompat> menuCompatList = new ArrayList<KKMenuCompat>();
	private ArrayList<KKMenuItemCompat> actionMenuItemList = new ArrayList<KKMenuItemCompat>();
	private int actionBarCompatSubViewCount = 0;

	private final OnClickListener buttonBackClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO: call onOptionsMenuSelected instead
			activity.onBackPressed();
		}
	};

	public KKActionBar(Activity activity, int styleResourceId) {
		this.activity = activity;
		if (styleResourceId != 0) {
			TypedArray array;
			if (Build.VERSION.SDK_INT >= 11) {
				actionBar = activity.getActionBar();
				if (actionBar == null) { return; }
				labelTitle = (TextView)activity.findViewById(Resources.getSystem().getIdentifier("action_bar_title", "id", "android"));
				labelSubTitle = (TextView)activity
						.findViewById(Resources.getSystem().getIdentifier("action_bar_subtitle", "id", "android"));
				if (Build.VERSION.SDK_INT < 14) {
					viewIcon = (ImageView)activity
							.findViewById(Resources.getSystem().getIdentifier("home", "id", "android"));
				}
			} else {
				actionBarCompat = (LinearLayout)activity.findViewById(R.id.action_bar);
				viewIcon = (ImageView)actionBarCompat.findViewById(R.id.view_icon);
				labelTitle = (TextView)actionBarCompat.findViewById(R.id.label_title);
				labelSubTitle = (TextView)actionBarCompat.findViewById(R.id.label_subtitle);
				viewHomeUp = (ImageView)actionBarCompat.findViewById(R.id.view_homeup);
				buttonBack = (RelativeLayout)actionBarCompat.findViewById(R.id.button_back);
				buttonBack.setOnClickListener(buttonBackClickListener);
				viewTitle = (LinearLayout)actionBarCompat.findViewById(R.id.view_title);
				FrameLayout frameLayout = (FrameLayout)activity.getWindow().getDecorView().findViewById(android.R.id.content);
				frameLayout.setForeground(null); // remove original windowContentOverlay, make it under actionbar
				actionBarCompatSubViewCount = actionBarCompat.getChildCount();
			}
			array = activity.obtainStyledAttributes(styleResourceId, new int[] { android.R.attr.icon });
			setIcon(array.getDrawable(0));
			array.recycle();
			array = activity.obtainStyledAttributes(styleResourceId, new int[] { android.R.attr.background });
			setBackgroundDrawable(array.getDrawable(0));
			array.recycle();
			array = activity.obtainStyledAttributes(styleResourceId, new int[] { R.attr.KKTitleTextStyle });
			labelTitle.setTextAppearance(activity, array.getResourceId(0, -1));
			array.recycle();
			array = activity.obtainStyledAttributes(styleResourceId, new int[] { R.attr.KKSubtitleTextStyle });
			labelSubTitle.setTextAppearance(activity, array.getResourceId(0, -1));
			array.recycle();
		}
	}

	public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
		if (Build.VERSION.SDK_INT >= 11) {
			actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
			if (Build.VERSION.SDK_INT >= 14) {
				actionBar.setHomeButtonEnabled(showHomeAsUp);
			}
		} else {
			if (showHomeAsUp) {
				viewHomeUp.setVisibility(View.VISIBLE);
			} else {
				viewHomeUp.setVisibility(View.INVISIBLE);
			}
			buttonBack.setClickable(showHomeAsUp);
		}
	}

	public void setSubtitle(String subTitle) {
		if (Build.VERSION.SDK_INT >= 11) {
			actionBar.setSubtitle(subTitle);
		} else {
			if (subTitle.equals("") || subTitle == null) {
				labelSubTitle.setVisibility(View.GONE);
			} else {
				labelSubTitle.setVisibility(View.VISIBLE);
			}
			labelSubTitle.setText(subTitle);
		}
	}

	public void setTitle(String title) {
		if (Build.VERSION.SDK_INT >= 11) {
			actionBar.setTitle(title);
		} else {
			labelTitle.setText(title);
		}
	}

	public void hide() {
		if (Build.VERSION.SDK_INT >= 11) {
			actionBar.hide();
		} else {
			actionBarCompat.setVisibility(View.GONE);
		}
	}

	public void setLogo(Drawable logo) {
		if (Build.VERSION.SDK_INT >= 14) {
			actionBar.setLogo(logo);
		} else {
			viewIcon.setImageDrawable(logo);
		}
	}

	public void setIcon(Drawable icon) {
		if (Build.VERSION.SDK_INT >= 14) {
			actionBar.setIcon(icon);
		} else {
			viewIcon.setImageDrawable(icon);
		}
	}

	@SuppressWarnings("deprecation")
	public void setBackgroundDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT >= 11) {
			actionBar.setBackgroundDrawable(drawable);
		} else {
			actionBarCompat.setBackgroundDrawable(drawable);
		}
	}

	void removeAllMenu() {
		if (Build.VERSION.SDK_INT < 11) {
			for (KKMenuCompat menuCompat : menuCompatList) {
				removeActionButtons(menuCompat);
			}
			menuCompatList.clear();
			viewTitle.setVisibility(View.VISIBLE);
		}
	}

	void removeActionButtons(KKMenuCompat menuCompat) {
		if (Build.VERSION.SDK_INT < 11) {
			for (int i = 0; i < menuCompatList.size(); i++) {
				if (menuCompatList.get(i).getUITarget().equals(menuCompat.getUITarget())) {
					for (int j = 0; j < menuCompatList.get(i).size(); j++) {
						KKMenuItemCompat menuItem = menuCompatList.get(i).getItem(j);
						if (menuItem.getShowAsActionFlags() != KKMenuItemCompat.SHOW_AS_ACTION_NEVER) {
							actionBarCompat.removeView(menuItem.getActionView());
							actionMenuItemList.remove(menuItem);
						}
					}
					return;
				}
			}
		}
	}

	void showSearchView(KKMenuItemCompat menuItemCompat) {
		if (Build.VERSION.SDK_INT < 11) {
			actionBarCompat.removeView(menuItemCompat.getActionView());
			menuItemCompat.setActionView(menuItemCompat.getCompatSearchView());
			actionBarCompat.addView(menuItemCompat.getCompatSearchView());
			for (int i = 0; i < actionMenuItemList.size(); i++) {
				if (!actionMenuItemList.get(i).equals(menuItemCompat)) {
					actionMenuItemList.get(i).getActionView().setVisibility(View.GONE);
				}
			}
			viewTitle.setVisibility(View.GONE);
		}
	}

	void addActionMenu(KKMenuCompat menuCompat) {
		if (Build.VERSION.SDK_INT < 11) {
			removeActionButtons(menuCompat);
			menuCompatList.remove(menuCompat);
			for (int i = 0; i < menuCompat.size(); i++) {
				KKMenuItemCompat menuItem = menuCompat.getItem(i);
				if (menuItem.getShowAsActionFlags() != KKMenuItemCompat.SHOW_AS_ACTION_NEVER) {
					int actionIndex = actionBarCompatSubViewCount;
					for (int j = 0; j < actionMenuItemList.size(); j++) {
						if (actionMenuItemList.get(j).getOrder() < menuItem.getOrder()) {
							actionIndex++;
						}
					}
					actionMenuItemList.add(menuItem);
					actionBarCompat.addView(menuItem.getActionView(), actionIndex);
				}
			}
			menuCompatList.add(menuCompat);
		}
	}
}
