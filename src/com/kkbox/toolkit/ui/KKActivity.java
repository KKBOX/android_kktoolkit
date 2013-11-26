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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.kkbox.toolkit.utils.KKDebug;

import java.util.ArrayList;

public abstract class KKActivity extends ActionBarActivity {
	static final private ArrayList<KKActivity> activityList = new ArrayList<KKActivity>();
	private final ArrayList<KKFragment> activeSubFragments = new ArrayList<KKFragment>();

	public void sendMessageToActiveSubFragments(Bundle arguments) {
		for (KKFragment fragment : activeSubFragments) {
			fragment.onReceiveMessage(arguments);
		}
	}

	public void finishAllKKActivity() {
		for (KKActivity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
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
}
