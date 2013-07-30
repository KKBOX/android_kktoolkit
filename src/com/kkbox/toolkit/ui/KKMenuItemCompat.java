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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ActionProvider;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageButton;

public class KKMenuItemCompat implements MenuItem {

	public static final int SHOW_AS_ACTION_ALWAYS = 2;
	public static final int SHOW_AS_ACTION_IF_ROOM = 1;
	public static final int SHOW_AS_ACTION_NEVER = 0;

	private MenuItem menuItem;
	private View compatActionView;
	private KKSearchViewCompat searchViewCompat;
	private int showAsActionFlags = 0;

	public KKSearchViewCompat getCompatSearchView() {
		return searchViewCompat;
	}

	public int getShowAsActionFlags() {
		return showAsActionFlags;
	}

	@Override
	public boolean collapseActionView() {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.collapseActionView();
		} else {
			return false;
		}
	}

	@Override
	public boolean expandActionView() {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.expandActionView();
		} else {
			return false;
		}
	}

	@Override
	public ActionProvider getActionProvider() {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.getActionProvider();
		} else {
			return null;
		}
	}

	@Override
	public View getActionView() {
		if (Build.VERSION.SDK_INT < 11) {
			return compatActionView;
		} else {
			return menuItem.getActionView();
		}
	}

	@Override
	public char getAlphabeticShortcut() {
		return menuItem.getAlphabeticShortcut();
	}

	@Override
	public int getGroupId() {
		return menuItem.getGroupId();
	}

	@Override
	public Drawable getIcon() {
		return menuItem.getIcon();
	}

	@Override
	public Intent getIntent() {
		return menuItem.getIntent();
	}

	@Override
	public int getItemId() {
		return menuItem.getItemId();
	}

	@Override
	public ContextMenuInfo getMenuInfo() {
		return menuItem.getMenuInfo();
	}

	@Override
	public char getNumericShortcut() {
		return menuItem.getNumericShortcut();
	}

	@Override
	public int getOrder() {
		return menuItem.getOrder();
	}

	@Override
	public SubMenu getSubMenu() {
		return menuItem.getSubMenu();
	}

	@Override
	public CharSequence getTitle() {
		return menuItem.getTitle();
	}

	@Override
	public CharSequence getTitleCondensed() {
		return menuItem.getTitleCondensed();
	}

	@Override
	public boolean hasSubMenu() {
		return menuItem.hasSubMenu();
	}

	@Override
	public boolean isActionViewExpanded() {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.isActionViewExpanded();
		} else {
			return false;
		}
	}

	@Override
	public boolean isCheckable() {
		return menuItem.isCheckable();
	}

	@Override
	public boolean isChecked() {
		return menuItem.isChecked();
	}

	@Override
	public boolean isEnabled() {
		return menuItem.isEnabled();
	}

	@Override
	public boolean isVisible() {
		return menuItem.isVisible();
	}

	@Override
	public MenuItem setActionProvider(ActionProvider actionProvider) {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.setActionProvider(actionProvider);
		} else {
			return menuItem;
		}
	}

	@Override
	public MenuItem setActionView(View view) {
		if (Build.VERSION.SDK_INT < 11) {
			compatActionView = view;
			setVisible(isVisible());
			setEnabled(isEnabled());
			return menuItem;
		} else {
			return menuItem.setActionView(view);
		}
	}

	@Override
	public MenuItem setActionView(int resId) {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.setActionView(resId);
		} else {
			return menuItem;
		}
	}

	@Override
	public MenuItem setAlphabeticShortcut(char alphaChar) {
		return menuItem.setAlphabeticShortcut(alphaChar);
	}

	@Override
	public MenuItem setCheckable(boolean checkable) {
		return menuItem.setCheckable(checkable);
	}

	@Override
	public MenuItem setChecked(boolean checked) {
		return menuItem.setChecked(checked);
	}

	@Override
	public MenuItem setEnabled(boolean enabled) {
		if (getActionView() != null) {
			getActionView().setEnabled(enabled);
		}
		return menuItem.setEnabled(enabled);
	}

	@Override
	public MenuItem setIcon(Drawable icon) {
		if (getActionView() != null && (getActionView() instanceof ImageButton)) {
			ImageButton button = (ImageButton)getActionView();
			button.setImageDrawable(icon);
			setActionView(button);
		}
		return menuItem.setIcon(icon);
	}

	@Override
	public MenuItem setIcon(int iconRes) {
		if (getActionView() != null && (getActionView() instanceof ImageButton)) {
			((ImageButton)getActionView()).setImageResource(iconRes);
		}
		return menuItem;
	}

	@Override
	public MenuItem setIntent(Intent intent) {
		return menuItem.setIntent(intent);
	}

	@Override
	public MenuItem setNumericShortcut(char numericChar) {
		return menuItem.setNumericShortcut(numericChar);
	}

	@Override
	public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
		if (Build.VERSION.SDK_INT >= 11) {
			return menuItem.setOnActionExpandListener(listener);
		} else {
			return menuItem;
		}
	}

	@Override
	public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
		return menuItem.setOnMenuItemClickListener(menuItemClickListener);
	}

	@Override
	public MenuItem setShortcut(char numericChar, char alphaChar) {
		return menuItem.setShortcut(numericChar, alphaChar);
	}

	@Override
	public void setShowAsAction(int actionEnum) {
		showAsActionFlags = actionEnum;
		if (Build.VERSION.SDK_INT >= 11) {
			menuItem.setShowAsAction(actionEnum);
		}
	}

	@Override
	public MenuItem setShowAsActionFlags(int actionEnum) {
		showAsActionFlags = actionEnum;
		if (Build.VERSION.SDK_INT >= 11) {
			menuItem.setShowAsAction(actionEnum);
		}
		return menuItem;
	}

	@Override
	public MenuItem setTitle(CharSequence title) {
		return menuItem.setTitle(title);
	}

	@Override
	public MenuItem setTitle(int title) {
		return menuItem.setTitle(title);
	}

	@Override
	public MenuItem setTitleCondensed(CharSequence title) {
		return menuItem.setTitleCondensed(title);
	}

	@Override
	public MenuItem setVisible(boolean visible) {
		if (Build.VERSION.SDK_INT < 11) {
			if (getActionView() != null) {
				if (visible) {
					getActionView().setVisibility(View.VISIBLE);
				} else {
					getActionView().setVisibility(View.GONE);
				}
			}
		}
		return menuItem.setVisible(visible);
	}

	KKMenuItemCompat(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	void linkToMenuItem(MenuItem menuItem) {
		menuItem.setVisible(isVisible());
		menuItem.setEnabled(isEnabled());
		menuItem.setIcon(getIcon());
		if (Build.VERSION.SDK_INT >= 11) {
			menuItem.setActionView(getActionView());
		}
		this.menuItem = menuItem;
	}

	void setCompatSearchView(KKSearchViewCompat searchViewCompat) {
		this.searchViewCompat = searchViewCompat;
	}
}
