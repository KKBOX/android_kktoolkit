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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;

public class KKDialog extends DialogFragment {
	public abstract class Type {
		public static final int ALERT_DIALOG = 0;
		public static final int YES_OR_NO_DIALOG = 1;
		public static final int THREE_CHOICE_DIALOG = 2;
		public static final int PROGRESSING_DIALOG = 3;
		public static final int SELECT_DIALOG = 4;
		public static final int CUSTOMIZE_DIALOG = 5;
	}

	private KKDialogPostExecutionListener listener;
	private int notificationId;
	private CharSequence title;
	private CharSequence message;
	private CharSequence positiveButtonText;
	private CharSequence negativeButtonText;
	private CharSequence neutralButtonText;
	private int dialogType;
	private String[] entries;
	private int selectedIndex;
	private int theme = -1;
	private View customizeView;

	public int getNotificationId() {
		return notificationId;
	}

	public KKDialog() {}

	public void setContent(int notificationId, CharSequence title, CharSequence message, CharSequence positiveButtonText,
	                       CharSequence negativeButtonText, CharSequence neutralButtonText, int dialogType,
	                       KKDialogPostExecutionListener listener) {
		this.notificationId = notificationId;
		this.title = title;
		this.message = message;
		this.positiveButtonText = positiveButtonText;
		this.negativeButtonText = negativeButtonText;
		this.neutralButtonText = neutralButtonText;
		this.dialogType = dialogType;
		this.listener = listener;
	}

	public void setSelectContent(int notificationId, CharSequence title, CharSequence negativeButtonText,
	                             String[] entries, int selectedIndex, KKDialogPostExecutionListener listener) {
		this.notificationId = notificationId;
		this.title = title;
		this.positiveButtonText = "";
		this.negativeButtonText = negativeButtonText;
		this.neutralButtonText = "";
		this.dialogType = Type.SELECT_DIALOG;
		this.listener = listener;
		this.entries = entries;
		this.selectedIndex = selectedIndex;
	}

	public void setCustomizeDialog(int notificationId, CharSequence title, CharSequence positiveButtonText, CharSequence negativeButtonText,
								CharSequence neutralButtonText, KKDialogPostExecutionListener listener, View customizeView) {
		this.notificationId = notificationId;
		this.title = title;
		this.positiveButtonText = positiveButtonText;
		this.negativeButtonText = negativeButtonText;
		this.neutralButtonText = neutralButtonText;
		this.dialogType = Type.CUSTOMIZE_DIALOG;
		this.listener = listener;
		this.customizeView = customizeView;
	}

	public void setTheme(int theme) {
		this.theme = theme;
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
		onDialogFinishedByUser();
	}

	protected void onDialogFinishedByUser() {}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.onPositive();
				}
				onDialogFinishedByUser();
			}
		};

		DialogInterface.OnClickListener neutralListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.onNeutral();
				}
				onDialogFinishedByUser();
			}
		};

		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if (listener != null) {
					listener.onNegative();
				}
				onDialogFinishedByUser();
			}
		};

		switch (dialogType) {
			case Type.PROGRESSING_DIALOG:
				ProgressDialog dialog;
				if (theme != -1) {
					dialog = new ProgressDialog(getActivity(), theme);
				} else {
					dialog = new ProgressDialog(getActivity());
				}
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setMessage(message);
				dialog.setIndeterminate(true);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setCancelable(listener != null);
				return dialog;
			case Type.ALERT_DIALOG:
				AlertDialog.Builder builder;
				if (theme != -1) {
					builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), theme));
				} else {
					builder = new AlertDialog.Builder(getActivity());
				}
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setPositiveButton(positiveButtonText, positiveListener);
				return builder.create();
			case Type.THREE_CHOICE_DIALOG:
				if (theme != -1) {
					builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), theme));
				} else {
					builder = new AlertDialog.Builder(getActivity());
				}
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setPositiveButton(positiveButtonText, positiveListener);
				builder.setNeutralButton(neutralButtonText, neutralListener);
				builder.setNegativeButton(negativeButtonText, negativeListener);
				return builder.create();
			case Type.YES_OR_NO_DIALOG:
				if (theme != -1) {
					builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), theme));
				} else {
					builder = new AlertDialog.Builder(getActivity());
				}
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setPositiveButton(positiveButtonText, positiveListener);
				builder.setNegativeButton(negativeButtonText, negativeListener);
				return builder.create();
			case Type.SELECT_DIALOG:
				if (theme != -1) {
					builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), theme));
				} else {
					builder = new AlertDialog.Builder(getActivity());
				}
				builder.setMessage(message);
				builder.setTitle(title);
				builder.setSingleChoiceItems(entries, selectedIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (listener != null) {
							listener.onEvent(id);
						}
						dismiss();
						onDialogFinishedByUser();
					}
				});
				builder.setNegativeButton(negativeButtonText, negativeListener);
				return builder.create();
			case Type.CUSTOMIZE_DIALOG:
				if (theme != -1) {
					builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), theme));
				} else {
					builder = new AlertDialog.Builder(getActivity());
				}
				builder.setView(customizeView);

				if (!TextUtils.isEmpty(title)) {
					builder.setTitle(title);
				}
				if (!TextUtils.isEmpty(positiveButtonText)) {
					builder.setPositiveButton(positiveButtonText, positiveListener);
				}
				if (!TextUtils.isEmpty(neutralButtonText)) {
					builder.setNeutralButton(neutralButtonText, neutralListener);
				}
				if (!TextUtils.isEmpty(negativeButtonText)) {
					builder.setNegativeButton(negativeButtonText, negativeListener);
				}
				return builder.create();
		}
		return null;
	}
}
