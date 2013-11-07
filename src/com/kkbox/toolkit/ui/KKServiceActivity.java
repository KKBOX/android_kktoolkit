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
 * KKServiceActivity
 */
package com.kkbox.toolkit.ui;

import android.app.ProgressDialog;
import android.content.Intent;

import com.kkbox.toolkit.KKService;
import com.kkbox.toolkit.KKServiceListener;
import com.kkbox.toolkit.R;
import com.kkbox.toolkit.dialog.KKDialog;
import com.kkbox.toolkit.internal.notification.KKDialogManagerListener;
import com.kkbox.toolkit.utils.KKDebug;

public abstract class KKServiceActivity extends KKActivity {
	private ProgressDialog serviceLoadingDialog;
	private KKDialog currentDialogFragment;
	private Intent nextActivityIntent = null;
	private int nextActivityRequestCode = -1;
	private boolean finishActivityAfterShowingNotification = false;

	protected final KKDialogManagerListener dialogNotificationListener = new KKDialogManagerListener() {
		@Override
		public void onAllNotificationEnded() {
			if (nextActivityIntent != null) {
				startActivityForResult(nextActivityIntent, nextActivityRequestCode);
				nextActivityIntent = null;
				nextActivityRequestCode = -1;
			}
			if (finishActivityAfterShowingNotification) {
				finish();
				finishActivityAfterShowingNotification = false;
			}
		}

		@Override
		public void onCancelNotification() {
			currentDialogFragment.dismiss();
		}

		@Override
		public void onNotification(final KKDialog dialog) {
			currentDialogFragment = dialog;
			currentDialogFragment.show(getSupportFragmentManager(), "alertDialog");
		}
	};

	private final KKServiceListener serviceListener = new KKServiceListener() {
		@Override
		public void onStarted() {
			if (serviceLoadingDialog != null) {
				serviceLoadingDialog.dismiss();
			}
			KKService.getDialogNotificationManager().setListener(dialogNotificationListener);
			onServiceStarted();
		}
	};

	protected void onServiceStarted() {}

	@Override
	public void startActivity(Intent intent) {
		if (!KKService.getDialogNotificationManager().isDialogOnShown()) {
			super.startActivity(intent);
		} else {
			nextActivityIntent = intent;
			nextActivityRequestCode = -1;
		}
	}

	@Override
	public void finish() {
		if (!KKService.getDialogNotificationManager().isDialogOnShown()) {
			super.finish();
		} else {
			finishActivityAfterShowingNotification = true;
		}
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (!KKService.getDialogNotificationManager().isDialogOnShown()) {
			super.startActivityForResult(intent, requestCode);
		} else {
			nextActivityIntent = intent;
			nextActivityRequestCode = requestCode;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!KKService.isRunning()) {
			serviceLoadingDialog = new ProgressDialog(this);
			serviceLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			serviceLoadingDialog.setMessage(getString(R.string.loading));
			serviceLoadingDialog.setIndeterminate(true);
			serviceLoadingDialog.setCanceledOnTouchOutside(false);
			serviceLoadingDialog.setCancelable(false);
			serviceLoadingDialog.show();
		}
		KKDebug.i(getClass().getSimpleName() + " onResume");
		KKService.attachListener(serviceListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (KKService.isRunning()) {
			KKService.getDialogNotificationManager().removeListener();
		}
	}
}
