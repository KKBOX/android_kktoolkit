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
import android.view.Menu;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class KKMenuCompat {
	private ArrayList<KKMenuItemCompat> menuItems = new ArrayList<KKMenuItemCompat>();
	private Menu menu;
	private int menuResourceId = 0;
	// TODO: refactor this
	private Object uiTarget;
	private ArrayList<KKMenuItemCompat> removedMenuItems = new ArrayList<KKMenuItemCompat>();

	public KKMenuItemCompat getItem(int index) {
		return menuItems.get(index);
	}

	public KKMenuItemCompat findItem(int id) {
		for (int i = 0; i < menuItems.size(); i++) {
			if (menuItems.get(i).getItemId() == id) {
				return menuItems.get(i);
			}
		}
		return null;
	}

	public void removeItem(int id) {
		menu.removeItem(id);
		for (int i = 0; i < menuItems.size(); i++) {
			if (menuItems.get(i).getItemId() == id) {
				removedMenuItems.add(menuItems.get(i));
				menuItems.remove(i);
				return;
			}
		}
	}

	public int size() {
		if (menuItems != null) {
			return menuItems.size();
		} else {
			return 0;
		}
	}

	KKMenuCompat(Context context, Object uiTarget) {
		try {
			Class<?> menuBuilderClass = Class.forName("com.android.internal.view.menu.MenuBuilder");
			Constructor<?> constructor = menuBuilderClass.getDeclaredConstructor(Context.class);
			menu = (Menu)constructor.newInstance(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.uiTarget = uiTarget;
	}

	boolean isMenuItemRemoved(int id) {
		for (int i = 0; i < removedMenuItems.size(); i++) {
			if (removedMenuItems.get(i).getItemId() == id) { return true; }
		}
		return false;
	}

	void initMenu(int resourceId) {
		menuResourceId = resourceId;
		menuItems = new ArrayList<KKMenuItemCompat>();
		for (int i = 0; i < menu.size(); i++) {
			menuItems.add(new KKMenuItemCompat(menu.getItem(i)));
		}
	}

	int getMenuResourceId() {
		return menuResourceId;
	}

	Object getUITarget() {
		return uiTarget;
	}

	Menu getRawMenu() {
		return menu;
	}
}
