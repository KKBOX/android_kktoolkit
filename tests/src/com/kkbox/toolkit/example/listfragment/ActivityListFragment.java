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
 * ActivityListFragment.java: This activity demonstrates the usage of KKListFragment, KKListView, and KKMessageView.
 */
package com.kkbox.toolkit.example.listfragment;

import android.os.Bundle;

import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.ui.KKActivity;

public class ActivityListFragment extends KKActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfragment_example);
		getKKActionBar().setTitle("KKListFragment Example");

		// Create an instance of ExampleFragment
		ExampleListFragment firstFragment = new ExampleListFragment();

		// In case this activity was started with special instructions from an Intent,
		// pass the Intent's extras to the fragment as arguments
		firstFragment.setArguments(getIntent().getExtras());

		// Add the fragment to the 'fragment_container' FrameLayout
		getSupportFragmentManager().beginTransaction().add(R.id.sub_fragment, firstFragment).commit();
	}
}