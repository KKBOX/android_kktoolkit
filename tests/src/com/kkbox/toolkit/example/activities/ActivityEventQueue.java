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
 * ActivityEventQueue.java: This activity demonstrates the usage of KKEventQueue.
 */
package com.kkbox.toolkit.example.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.utils.KKEventQueue;
import com.kkbox.toolkit.utils.KKEventQueueListener;

public class ActivityEventQueue extends KKActivity {

	private KKEventQueue eventQueue;
	private Button btnStart;
	private TextView resultContent;

	private final KKEventQueueListener eventQueuelistener = new KKEventQueueListener() {
		@Override
		public void onQueueCompleted() {
			resultContent.setText("KKEventQueue Finished");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventqueue_example);
		getKKActionBar().setTitle("KKEventQueue Example");
		eventQueue = new KKEventQueue();
		eventQueue.setListener(eventQueuelistener);
		resultContent = (TextView)findViewById(R.id.resultContent);
		btnStart = (Button)findViewById(R.id.btnStart);
		btnStart.setOnClickListener(btnStartClickListener);
	};

	private final OnClickListener btnStartClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			eventQueue.add(new Runnable() {
				@Override
				public void run() {					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, KKEventQueue.ThreadType.NEW_THREAD);
			resultContent.setText("Running...");
			eventQueue.start();
		}
	};
}
