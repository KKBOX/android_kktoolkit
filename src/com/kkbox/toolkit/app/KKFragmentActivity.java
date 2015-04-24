/* Copyright (C) 2014 KKBOX Inc.
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
package com.kkbox.toolkit.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class KKFragmentActivity extends FragmentActivity implements KKServiceActivity {

	private KKActivityDelegate delegate;

	public KKFragmentActivity() {
		delegate = new KKActivityDelegate(this);
	}

	@Override
	public void startActivityIfNoDialog(Intent intent) {
		delegate.startActivityIfNoDialog(intent);
	}

	@Override
	public void finishIfNoDialog() {
		delegate.finishIfNoDialog();
	}

	@Override
	public void startActivityForResultIfNoDialog(Intent intent, int requestCode) {
		delegate.startActivityForResultIfNoDialog(intent, requestCode);
	}

	@Override
	public void sendMessageToActiveSubFragments(Bundle arguments) {
		delegate.sendMessageToActiveSubFragments(arguments);
	}

	@Override
	public void finishAllKKActivity() {
		delegate.finishAllKKActivity();
	}

	@Override
	public void activateSubFragment(KKFragment fragment) {
		delegate.activateSubFragment(fragment);
	}

	@Override
	public void deactivateSubFragment(KKFragment fragment) {
		delegate.deactivateSubFragment(fragment);
	}

	@Override
	public void onServiceStarted(int flag) {}

	@Override
	public void onServiceStarting(int flag) {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		delegate.onCreate(savedInstanceState);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		delegate.onPostResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		delegate.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		delegate.onDestroy();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        delegate.onActivityResult(requestCode, resultCode, data);
    }
}
