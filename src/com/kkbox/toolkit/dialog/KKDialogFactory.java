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


public class KKDialogFactory {

	public static KKServiceDialog createAlertDialog(int notificationId, String title, String message, String positiveButtonText,
			KKDialogPostExecutionListener listener) {
		KKServiceDialog dialog = new KKServiceDialog();
		dialog.setContent(notificationId, title, message, positiveButtonText, "", "", KKServiceDialog.Type.ALERT_DIALOG, listener);
		return dialog;
	}

	public static KKServiceDialog createYesOrNoDialog(int notificationId, String title, String message, String positiveButtonText, String negativeButtonText,
			KKDialogPostExecutionListener listener) {
		KKServiceDialog dialog = new KKServiceDialog();
		dialog.setContent(notificationId, title, message, positiveButtonText, negativeButtonText, "", KKServiceDialog.Type.YES_OR_NO_DIALOG,
				listener);
		return dialog;
	}

	public static KKServiceDialog createThreeChoiceDialog(int notificationId, String title, String message, String positiveButtonText, String neutralButtonText,
			String negativeButtonText, KKDialogPostExecutionListener listener) {
		KKServiceDialog dialog = new KKServiceDialog();
		dialog.setContent(notificationId, title, message, positiveButtonText, negativeButtonText, neutralButtonText,
				KKServiceDialog.Type.THREE_CHOICE_DIALOG, listener);
		return dialog;
	}

	public static KKServiceDialog createProgressingDialog(int notificationId, String message, KKDialogPostExecutionListener listener) {
		KKServiceDialog dialog = new KKServiceDialog();
		dialog.setContent(notificationId, "", message, "", "", "", KKServiceDialog.Type.PROGRESSING_DIALOG, listener);
		return dialog;
	}
}