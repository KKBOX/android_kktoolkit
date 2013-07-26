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
 * ExampleListFragment.java: 
 */
package com.kkbox.toolkit.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.kkbox.toolkit.example.FakeData;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.ui.KKListFragment;

public class ExampleListFragment extends KKListFragment {

	@Override
	public void onLoadData() {
		startFetchData();
		onLoadUI();
	}

	@Override
	protected void onLoadUI() {
		super.onLoadUI();
	}
	
	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			getKKListView().setItemChecked(position, true);
			Bundle bundle = new Bundle();
			bundle.putInt(ExampleMessageFragment.ARG_POSITION, position);
			switchToFragment(new ExampleMessageFragment(), bundle);
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    // We need to use a different list item layout for devices older than Honeycomb
	    View view = inflater.inflate(R.layout.fragment_listview, container, false);
		initView(view);
		
		getKKListView().setAdapter(new ArrayAdapter<String>(getKKActivity(),android.R.layout.simple_list_item_1,FakeData.Headlines));
	    getKKListView().setOnItemClickListener(onItemClickListener);
	    
	    return view;
	}

}
