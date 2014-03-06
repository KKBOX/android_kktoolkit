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
package com.kkbox.toolkit.dialog;

import android.support.v4.app.FragmentActivity;

import com.kkbox.toolkit.internal.dialog.KKDialogManagerListener;

import java.util.ArrayList;

public class KKDialogManager {

	private ArrayList<KKServiceDialog> dialogs = new ArrayList<KKServiceDialog>();
	private KKDialogManagerListener listener;
	private boolean isDialogOnShown = false;

	public void addDialog(KKServiceDialog newDialog) {
		for (KKServiceDialog dialog : dialogs) {
			if (dialog.getNotificationId() == newDialog.getNotificationId()) { return; }
		}
		dialogs.add(newDialog);
		showDialog();
	}

	public void cancelDialog(int notificationId) {
		if (isDialogOnShown && listener != null && dialogs.get(0).getNotificationId() == notificationId) {
			listener.onCancelNotification();
			dismissCurrentDialog();
		} else {
			for (int i = 0; i < dialogs.size(); i++) {
				if (dialogs.get(i).getNotificationId() == notificationId) {
					dialogs.remove(i);
					return;
				}
			}
		}
	}

	public void setListener(KKDialogManagerListener listener) {
		this.listener = listener;
		showDialog();
		if (!isDialogOnShown && dialogs.size() == 0) {
			listener.onAllNotificationEnded();
		}
	}

	public void removeListener() {
		if (isDialogOnShown) {
			listener.onCancelNotification();
			isDialogOnShown = false;
		}
		listener = null;
	}

	public boolean isDialogOnShown() {
		return isDialogOnShown;
	}
	
	public void dismissCurrentDialog() {
		isDialogOnShown = false;
		if (!dialogs.isEmpty()) {
			dialogs.remove(0);
		}
		showDialog();
		if (!isDialogOnShown && listener != null) {
			listener.onAllNotificationEnded();
		}
	}

	private void showDialog() {
		if (!dialogs.isEmpty() && !isDialogOnShown && listener != null) {
			isDialogOnShown = true;
			listener.onNotification(dialogs.get(0));
		}
	}

	public FragmentActivity getCurrentDialogActivity() {
		return dialogs.get(0).getActivity();
	}

	public boolean isNotificationExists(int dialogID) {
		for (KKDialog dialog : dialogs) {
			if (dialog.getNotificationId() == dialogID) {
				return true;
			}
		}
		return false;
	}
}
