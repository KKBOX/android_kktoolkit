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
 * KKDialogFragment
 */
package com.kkbox.toolkit.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.kkbox.toolkit.KKService;

public class KKDialog extends DialogFragment {
	public abstract class Type {
		public static final int ALERT_DIALOG = 0;
		public static final int YES_OR_NO_DIALOG = 1;
		public static final int THREE_CHOICE_DIALOG = 2;
		public static final int PROGRESSING_DIALOG = 3;
	}

	private KKDialogPostExecutionListener listener;
	private int notificationId;
	private String title;
	private String message;
	private String positiveButtonText;
	private String negativeButtonText;
	private String neutralButtonText;
	private int dialogType;

	public int getNotificationId() {
		return notificationId;
	}

	public KKDialog() {}

	void setContent(int notificationId, String title, String message, String positiveButtonText, String negativeButtonText,
			String neutralButtonText, int dialogType, KKDialogPostExecutionListener listener) {
		this.notificationId = notificationId;
		this.title = title;
		this.message = message;
		this.positiveButtonText = positiveButtonText;
		this.negativeButtonText = negativeButtonText;
		this.neutralButtonText = neutralButtonText;
		this.dialogType = dialogType;
		this.listener = listener;
	}

	public int getDialogType() {
		return dialogType;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (listener != null) {
			listener.onCancel();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.onPositive();
				}
				KKService.getDialogNotificationManager().dismissCurrentDialog();
			}
		};

		DialogInterface.OnClickListener neutralListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.onNeutral();
				}
				KKService.getDialogNotificationManager().dismissCurrentDialog();
			}
		};

		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.onNegative();
				}
				KKService.getDialogNotificationManager().dismissCurrentDialog();
			}
		};

		switch (dialogType) {
			case Type.PROGRESSING_DIALOG:
				ProgressDialog dialog = new ProgressDialog(getActivity());
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setMessage(message);
				dialog.setIndeterminate(true);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setCancelable(listener != null);
				return dialog;
			case Type.ALERT_DIALOG:
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setPositiveButton(positiveButtonText, positiveListener);
				return builder.create();
			case Type.THREE_CHOICE_DIALOG:
				builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setPositiveButton(positiveButtonText, positiveListener);
				builder.setNeutralButton(neutralButtonText, neutralListener);
				builder.setNegativeButton(negativeButtonText, negativeListener);
				return builder.create();
			case Type.YES_OR_NO_DIALOG:
				builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setPositiveButton(positiveButtonText, positiveListener);
				builder.setNegativeButton(negativeButtonText, negativeListener);
				return builder.create();
		}
		return null;
	}
}
