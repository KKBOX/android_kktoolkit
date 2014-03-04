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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.kkbox.toolkit.KKService;
import com.kkbox.toolkit.KKServiceListener;
import com.kkbox.toolkit.R;
import com.kkbox.toolkit.dialog.KKServiceDialog;
import com.kkbox.toolkit.internal.dialog.KKDialogManagerListener;

import java.util.ArrayList;

public class KKActivityDelegate {
	private static final ArrayList<FragmentActivity> activityList = new ArrayList<FragmentActivity>();
	private final ArrayList<KKFragment> activeSubFragments = new ArrayList<KKFragment>();

	private ProgressDialog serviceLoadingDialog;
	private KKServiceDialog currentDialogFragment;
	private Intent nextActivityIntent = null;
	private int nextActivityRequestCode = -1;
	private boolean finishActivityAfterShowingNotification = false;
	private FragmentActivity activity;

	protected final KKDialogManagerListener dialogNotificationListener = new KKDialogManagerListener() {
		@Override
		public void onAllNotificationEnded() {
			if (nextActivityIntent != null) {
				activity.startActivityForResult(nextActivityIntent, nextActivityRequestCode);
				nextActivityIntent = null;
				nextActivityRequestCode = -1;
			}
			if (finishActivityAfterShowingNotification) {
				activity.finish();
				finishActivityAfterShowingNotification = false;
			}
		}

		@Override
		public void onCancelNotification() {
			currentDialogFragment.dismiss();
		}

		@Override
		public void onNotification(final KKServiceDialog dialog) {
			currentDialogFragment = dialog;
			currentDialogFragment.show(activity.getSupportFragmentManager(), "alertDialog");
		}
	};

	private final KKServiceListener serviceListener = new KKServiceListener() {
		@Override
		public void onRunning(int flag) {
			if (serviceLoadingDialog != null && serviceLoadingDialog.isShowing()) {
				serviceLoadingDialog.dismiss();
			}
			KKService.getDialogNotificationManager().setListener(dialogNotificationListener);
			((KKServiceActivity) activity).onServiceStarted(flag);
		}
		
		@Override
		public void onProgress(int flag) {
			((KKServiceActivity) activity).onServiceStarting(flag);
		}
	};

	public KKActivityDelegate(FragmentActivity activity) {
		this.activity = activity;
	}

	public void startActivityIfNoDialog(Intent intent) {
		if (KKService.getDialogNotificationManager() == null || !KKService.getDialogNotificationManager().isDialogOnShown()) {
			activity.startActivity(intent);
		} else {
			nextActivityIntent = intent;
			nextActivityRequestCode = -1;
		}
	}

	public void finishIfNoDialog() {
		if (KKService.getDialogNotificationManager() == null || !KKService.getDialogNotificationManager().isDialogOnShown()) {
			activity.finish();
		} else {
			finishActivityAfterShowingNotification = true;
		}
	}

	public void startActivityForResultIfNoDialog(Intent intent, int requestCode) {
		if (KKService.getDialogNotificationManager() == null || !KKService.getDialogNotificationManager().isDialogOnShown()) {
			activity.startActivityForResult(intent, requestCode);
		} else {
			nextActivityIntent = intent;
			nextActivityRequestCode = requestCode;
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		activityList.add(activity);
	}

	public void onResume() {
		if (!KKService.isRunning()) {
			serviceLoadingDialog = new ProgressDialog(activity);
			serviceLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			serviceLoadingDialog.setMessage(activity.getString(R.string.loading));
			serviceLoadingDialog.setIndeterminate(true);
			serviceLoadingDialog.setCanceledOnTouchOutside(false);
			serviceLoadingDialog.setCancelable(false);
			serviceLoadingDialog.show();
		}
		KKService.attachListener(serviceListener);
	}

	public void onPause() {
		if (serviceLoadingDialog != null && serviceLoadingDialog.isShowing()) {
			serviceLoadingDialog.dismiss();
		}
		if (KKService.isRunning()) {
			KKService.getDialogNotificationManager().removeListener();
		}
	}

	public void onDestroy() {
		activityList.remove(activity);
	}

	public void sendMessageToActiveSubFragments(Bundle arguments) {
		for (int i = 0; i < activeSubFragments.size(); i++) {
			activeSubFragments.get(i).onReceiveMessage(arguments);
		}
	}

	public void finishAllKKActivity() {
		for (FragmentActivity activity : activityList) {
			activity.finish();
		}
		activityList.clear();
	}

	public void activateSubFragment(KKFragment fragment) {
		activeSubFragments.add(fragment);
	}

	public void deactivateSubFragment(KKFragment fragment) {
		activeSubFragments.remove(fragment);
	}
}
