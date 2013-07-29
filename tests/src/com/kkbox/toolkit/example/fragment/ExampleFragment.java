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
package com.kkbox.toolkit.example.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kkbox.toolkit.api.KKAPIListener;
import com.kkbox.toolkit.example.ExampleAPI;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.ui.KKFragment;

public class ExampleFragment extends KKFragment {
	private ExampleAPI exampleAPI;
	private EditText textTestString;
	private Button btnSubmit;
	private TextView response;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_example, container, false);
		textTestString = (EditText)v.findViewById(R.id.editText1);
		btnSubmit = (Button)v.findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(btnSubmitClickListener);
		response = (TextView)v.findViewById(R.id.responseContent);
		initView(v);

		return v;
	}

	private final KKAPIListener exampleAPIListener = new KKAPIListener() {
		@Override
		public void onAPIComplete() {
			finishFetchData();
			response.setText(exampleAPI.getData());
		}

		@Override
		public void onAPIError(int errorCode) {
			response.setText(exampleAPI.getErrorMessage());
			fetchDataFailed();
		}
	};

	private final OnClickListener btnSubmitClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startFetchData();
			exampleAPI = new ExampleAPI();
			exampleAPI.setAPIListener(exampleAPIListener);
			exampleAPI.start(textTestString.getText().toString());
		}
	};

}
