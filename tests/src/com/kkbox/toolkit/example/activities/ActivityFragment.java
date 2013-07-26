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
 * ActivityFragment.java: This activity demonstrates the usage of KKFragment and KKAPIs.
 */
package com.kkbox.toolkit.example.activities;

import android.os.Bundle;

import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.example.fragments.ExampleFragment;
import com.kkbox.toolkit.ui.KKActivity;


public class ActivityFragment extends KKActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfragment_example);
		getKKActionBar().setTitle("KKFragment Example");
		
		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first fragment
		if (findViewById(R.id.sub_fragment) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) { return; }

			// Create an instance of ExampleFragment
			ExampleFragment firstFragment = new ExampleFragment();

			// In case this activity was started with special instructions from an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction().add(R.id.sub_fragment, firstFragment).commit();
		}
	}
	
}
