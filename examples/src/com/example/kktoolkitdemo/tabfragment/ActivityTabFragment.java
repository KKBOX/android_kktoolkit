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
 * ActivityTabFragment.java: This activity demonstrates the usage of KKTabFragment.
 */
package com.example.kktoolkitdemo.tabfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.kktoolkitdemo.R;
import com.kkbox.toolkit.ui.KKActivity;

public class ActivityTabFragment extends KKActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listfragment_example);
		getKKActionBar().setTitle("KKTabFragment Example");
		
		ExampleTabFragment firstFragment = new ExampleTabFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		firstFragment.setArguments(getIntent().getExtras());
		fragmentTransaction.add(R.id.sub_fragment, firstFragment);
		fragmentTransaction.commit();
	}
}
