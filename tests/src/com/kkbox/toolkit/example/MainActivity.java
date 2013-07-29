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
 * MainActivity.java: This is the main activity (also the entry point) of the whole example APP.
 * 
 * In the example APP, we demonstrates the usage of the elements that our toolkit provides.
 * The elements are demonstrated through combination usages in order to help you have a better understanding.
 * The following examples are provided:
 * 
 * KKFragment + API (ActivityFragment.java)
 * KKListFragment + KKListView + KKMessageView (ActivityListFragment.java)
 * KKTabFragment (ActivityTabFragment.java)
 * KKImageManager (ActivityImage.java)
 * KKNotification (ActivityNotification.java)
 * KKEventQueue (ActivityEventQueue.java)
 * 
 */
package com.kkbox.toolkit.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.kkbox.toolkit.example.eventqueue.ActivityEventQueue;
import com.kkbox.toolkit.example.fragment.ActivityFragment;
import com.kkbox.toolkit.example.image.ActivityImage;
import com.kkbox.toolkit.example.listfragment.ActivityListFragment;
import com.kkbox.toolkit.example.notification.ActivityNotification;
import com.kkbox.toolkit.example.tabfragment.ActivityTabFragment;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.KKListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends KKActivity {

	private KKListView mainListView;
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getKKActionBar().setTitle("KKToolkit Examples");
		
		mainListView = (KKListView)findViewById(R.id.listview_main);

		String[] examples = new String[] { "KKFragment + API", "KKListFragment + KKListView + KKMessageView", "KKTabFragment",
				"KKImageManager", "KKNotification", "KKEventQueue" };
		ArrayList<String> exampleList = new ArrayList<String>();
		exampleList.addAll(Arrays.asList(examples));

		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exampleList);

		mainListView.setAdapter(listAdapter);
		
		mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				Intent intent;
				switch (position) {
					case 0:
						intent = new Intent(MainActivity.this, ActivityFragment.class);
						startActivity(intent);
						break;
					case 1:
						intent = new Intent(MainActivity.this, ActivityListFragment.class);
						startActivity(intent);
						break;
					case 2:
						intent = new Intent(MainActivity.this, ActivityTabFragment.class);
						startActivity(intent);
						break;
					case 3:
						intent = new Intent(MainActivity.this, ActivityImage.class);
						startActivity(intent);
						break;
					case 4:
						intent = new Intent(MainActivity.this, ActivityNotification.class);
						startActivity(intent);
						break;
					case 5:
						intent = new Intent(MainActivity.this, ActivityEventQueue.class);
						startActivity(intent);
						break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
