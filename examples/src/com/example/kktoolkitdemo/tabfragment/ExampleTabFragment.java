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
 * ExampleTabFragment.java: 
 */
package com.example.kktoolkitdemo.tabfragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kktoolkitdemo.R;
import com.example.kktoolkitdemo.fragment.ExampleFragment;
import com.kkbox.toolkit.ui.KKFragment;
import com.kkbox.toolkit.ui.KKTabFragment;
import com.kkbox.toolkit.utils.KKDebug;

public class ExampleTabFragment extends KKTabFragment{
	
	private int[] buttonTextResourcetId = new int[3];
	private int currentIndex = 0;
	
	protected KKFragment onRequestTabFragment(int index, Bundle arguments) {
		KKFragment fragment = null;
		KKDebug.i("current index: " + index);
		switch (index) {
			case 0:
				fragment = new ExampleFragment(ExampleFragment.TAB_STYLE.GRAY);
				break;
			case 1:
				fragment = new ExampleFragment(ExampleFragment.TAB_STYLE.YELLOW);
				break;
			case 2:
				fragment = new ExampleFragment(ExampleFragment.TAB_STYLE.BLUE);
                break;
		}
		return fragment;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tabs, container, false);
		buttonTextResourcetId[0] = R.string.tab_1;
		buttonTextResourcetId[1] = R.string.tab_2;
		buttonTextResourcetId[2] = R.string.tab_3;
		initView(view, buttonTextResourcetId, false, currentIndex);
        view.setBackgroundColor(Color.GRAY);
		return view;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
