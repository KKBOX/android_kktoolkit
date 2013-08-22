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
 * KKActivity
 */
package com.kkbox.toolkit.ui;

import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SearchViewCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.kkbox.toolkit.R;
import com.kkbox.toolkit.utils.KKDebug;

import java.util.ArrayList;

public abstract class KKActivity extends FragmentActivity {
	static final private ArrayList<KKActivity> activityList = new ArrayList<KKActivity>();
	private final ArrayList<KKFragment> activeSubFragments = new ArrayList<KKFragment>();
	private KKActionBar actionBar;
	private KKMenuInflaterCompat menuInflater;
	private KKMenuCompat menuCompat;
	private Menu menu;

	public KKActionBar getKKActionBar() {
		return actionBar;
	}

	public void sendMessageToActiveSubFragments(Bundle arguments) {
		for (KKFragment fragment : activeSubFragments) {
			fragment.onReceiveMessage(arguments);
		}
	}

	@Override
	public KKMenuInflaterCompat getMenuInflater() {
		if (menuInflater == null) {
			menuInflater = new KKMenuInflaterCompat(this);
		}
		return menuInflater;
	}

	public void onCreateCompatOptionsMenu(KKMenuCompat menu) {
		this.menuCompat = menu;
		if (actionBar != null) {
			for (int i = 0; i < menuCompat.size(); i++) {
				checkActionButtonCreated(this, menuCompat.getItem(i));
			}
			actionBar.addActionMenu(menuCompat);
		}
	}

	public void onCompatOptionsItemSelected(KKMenuItemCompat item) {
		checkSearchViewSelected(item);
	}

	public void onPrepareCompatOptionsMenu(KKMenuCompat menu) {}

	public void invalidateOptionsMenu() {
		if (actionBar != null) {
			actionBar.removeAllMenu();
		}
		for (KKFragment fragment : activeSubFragments) {
			fragment.onCreateCompatOptionsMenu(new KKMenuCompat(this, fragment), menuInflater);
		}
		onCreateCompatOptionsMenu(new KKMenuCompat(this, this));
		if (Build.VERSION.SDK_INT >= 11) {
			super.invalidateOptionsMenu();
		} else if (menu != null) {
			menu.clear();
			createOptionsMenu(this.menuCompat, menu);
			for (KKFragment fragment : activeSubFragments) {
				fragment.onCreateOptionsMenu(menu, menuInflater);
			}
		}
	}

	public void finishAllKKActivity() {
		for (KKActivity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		onPrepareCompatOptionsMenu(menuCompat);
		prepareOptionsMenu(menuCompat, menu);
		if (menu.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		KKMenuItemCompat menuItemCompat = menuCompat.findItem(item.getItemId());
		if (menuItemCompat == null) {
			menuItemCompat = new KKMenuItemCompat(item);
		}
		for (KKFragment fragment : activeSubFragments) {
			fragment.onCompatOptionsItemSelected(menuItemCompat);
		}
		onCompatOptionsItemSelected(menuItemCompat);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		createOptionsMenu(this.menuCompat, menu);
		return true;
	}

	@Override
	public void setContentView(int layoutResID) {
		TypedValue typedValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.themeName, typedValue, true);
		if (typedValue.string != null && typedValue.string.equals("KKActivityTheme")) {
			if (Build.VERSION.SDK_INT < 11) {
				super.setContentView(R.layout.activity_compat);
				FrameLayout layoutContent = (FrameLayout)findViewById(R.id.layout_content);
				View view = LayoutInflater.from(this).inflate(layoutResID, null);
				layoutContent.addView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			} else {
				super.setContentView(layoutResID);
			}
			getTheme().resolveAttribute(R.attr.KKActionBarStyle, typedValue, true);
			actionBar = new KKActionBar(this, typedValue.resourceId);
		} else {
			super.setContentView(layoutResID);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		menuInflater = new KKMenuInflaterCompat(this);
		onCreateCompatOptionsMenu(new KKMenuCompat(this, this));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityList.remove(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		KKDebug.i(getClass().getSimpleName() + " onResume");
	}

	void activateSubFragment(KKFragment fragment) {
		activeSubFragments.add(fragment);
	}

	void deactivateSubFragment(KKFragment fragment) {
		activeSubFragments.remove(fragment);
	}

	void checkSearchViewSelected(KKMenuItemCompat item) {
		if (item.getCompatSearchView() != null) {
			if (Build.VERSION.SDK_INT >= 11) {
				item.setActionView(item.getCompatSearchView());
				item.getCompatSearchView().setIconified(false);
			} else {
				actionBar.showSearchView(item);
			}
		}
	}

	void checkActionButtonCreated(final Object responseUI, final KKMenuItemCompat menuItem) {
		if (menuItem.getShowAsActionFlags() != KKMenuItemCompat.SHOW_AS_ACTION_NEVER && menuItem.getActionView() == null) {
			final ImageButton button = new ImageButton(this);
			TypedValue typedValue = new TypedValue();
			getTheme().resolveAttribute(R.attr.KKActionButtonStyle, typedValue, true);
			button.setImageDrawable(menuItem.getIcon());
			TypedArray array = obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.background });
			button.setBackgroundResource(array.getResourceId(0, -1));
			array = obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.maxWidth });
			button.setMaxWidth(array.getDimensionPixelSize(0, -1));
			array = obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.paddingLeft, android.R.attr.paddingTop,
					android.R.attr.paddingRight, android.R.attr.paddingBottom });
			button.setPadding(array.getDimensionPixelSize(0, button.getPaddingLeft()),
					array.getDimensionPixelSize(1, button.getPaddingTop()), array.getDimensionPixelSize(2, button.getPaddingRight()),
					array.getDimensionPixelSize(3, button.getPaddingBottom()));
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (responseUI instanceof KKActivity) {
						onCompatOptionsItemSelected(menuItem);
					} else {
						((KKFragment)responseUI).onCompatOptionsItemSelected(menuItem);
					}

				}
			});
			if (menuItem.getCompatSearchView() != null) {
				menuItem.getCompatSearchView().setDefaultOnCloseListener(new SearchViewCompat.OnCloseListenerCompat() {
					@Override
					public boolean onClose() {
						if (Build.VERSION.SDK_INT >= 11) {
							menuItem.setActionView(button);
						} else {
							invalidateOptionsMenu();
						}
						return false;
					}
				});
			}
			menuItem.setActionView(button);
		}
	}

	void createOptionsMenu(KKMenuCompat menuCompat, Menu menu) {
		if (menuCompat.getMenuResourceId() != 0) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(menuCompat.getMenuResourceId(), menu);
			int removeCount = 0;
			int size = menu.size();
			for (int i = 0; i < size; i++) {
				MenuItem menuItem = menu.getItem(i - removeCount);
				KKMenuItemCompat menuItemCompat = menuCompat.findItem(menuItem.getItemId());
				if (menuItemCompat != null) {
					if (Build.VERSION.SDK_INT < 11 && menuItemCompat.getShowAsActionFlags() != KKMenuItemCompat.SHOW_AS_ACTION_NEVER) {
						menu.removeItem(menuItem.getItemId());
						removeCount++;
					} else {
						menuItemCompat.linkToMenuItem(menuItem);
					}
				} else if (menuCompat.isMenuItemRemoved(menuItem.getItemId())) {
					menu.removeItem(menuItem.getItemId());
					removeCount++;
				}
			}
		}
	}

	void prepareOptionsMenu(KKMenuCompat menuCompat, Menu menu) {
		for (int i = 0; i < menu.size(); i++) {
			MenuItem menuItem = menu.getItem(i);
			KKMenuItemCompat menuItemCompat = menuCompat.findItem(menuItem.getItemId());
			if (menuItemCompat != null && Build.VERSION.SDK_INT < 11
					&& menuItemCompat.getShowAsActionFlags() == KKMenuItemCompat.SHOW_AS_ACTION_NEVER) {
				menuItemCompat.linkToMenuItem(menuItem);
			}
		}
	}
}
