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
 * KKSearchViewCompat
 */
package com.kkbox.toolkit.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SearchViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.kkbox.toolkit.R;

import java.util.List;

public class KKSearchViewCompat extends LinearLayout {
	SearchView searchView;
	SearchViewCompat.OnCloseListenerCompat defaultOnCloseListener;

	EditText textSearch;
	ImageView buttonSearchClose;
	ImageView buttonVoiceSearch;

	KKActivity activity;

	public KKSearchViewCompat(KKActivity activity) {
		super(activity);
		this.activity = activity;
		if (Build.VERSION.SDK_INT >= 11) {
			searchView = new SearchView(activity);
			addView(searchView);
		} else {
			View view = LayoutInflater.from(activity).inflate(R.layout.search_view_compat, null);
			textSearch = (EditText)view.findViewById(R.id.text_search);
			buttonSearchClose = (ImageView)view.findViewById(R.id.button_search_close);
			buttonVoiceSearch = (ImageView)view.findViewById(R.id.button_voice_search);
			PackageManager packageManager = activity.getPackageManager();
			List<?> recognizedActivities = packageManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
			buttonVoiceSearch.setVisibility(recognizedActivities.size() > 0 ? View.VISIBLE : View.GONE);
			view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			addView(view);
			setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		}
	}

	public void setSearchableInfoCompat(SearchManager searchManager, ComponentName componentName) {
		if (Build.VERSION.SDK_INT >= 11) {
			searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
		} else {
			buttonVoiceSearch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "voice search");
					activity.startActivityForResult(intent, 9527);
				}
			});
		}
	}

	public void setIconified(boolean iconify) {
		if (Build.VERSION.SDK_INT >= 11) {
			searchView.setIconified(iconify);
		}
	}

	public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener listener) {
		if (Build.VERSION.SDK_INT >= 11) {
			searchView.setOnQueryTextFocusChangeListener(listener);
		} else {
			textSearch.setOnFocusChangeListener(listener);
		}
	}

	public void setOnQueryTextListener(SearchViewCompat.OnQueryTextListenerCompat listener) {
		final SearchViewCompat.OnQueryTextListenerCompat staticListener = listener;
		if (Build.VERSION.SDK_INT >= 11) {
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					staticListener.onQueryTextSubmit(query);
					return false;
				}

				@Override
				public boolean onQueryTextChange(String newText) {
					staticListener.onQueryTextChange(newText);
					return false;
				}
			});
		} else {
			textSearch.addTextChangedListener(new TextWatcher() {
				@Override
				public void afterTextChanged(Editable arg0) {}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

				@Override
				public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
					staticListener.onQueryTextChange(s.toString());
				}

			});
			textSearch.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
					if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
							&& (keycode == KeyEvent.KEYCODE_SEARCH || keycode == KeyEvent.KEYCODE_ENTER)) {
						staticListener.onQueryTextSubmit(textSearch.getText().toString());
						return true;
					}
					return false;
				}
			});
		}
	}

	public void setOnCloseListener(SearchViewCompat.OnCloseListenerCompat listener) {
		final SearchViewCompat.OnCloseListenerCompat staticListener = listener;
		if (Build.VERSION.SDK_INT >= 11) {
			searchView.setOnCloseListener(new SearchView.OnCloseListener() {
				@Override
				public boolean onClose() {
					staticListener.onClose();
					if (defaultOnCloseListener != null) {
						defaultOnCloseListener.onClose();
					}
					return false;
				}
			});
		} else {
			buttonSearchClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					staticListener.onClose();
					if (defaultOnCloseListener != null) {
						defaultOnCloseListener.onClose();
					}
				}
			});
		}
	}

	void setDefaultOnCloseListener(SearchViewCompat.OnCloseListenerCompat listener) {
		defaultOnCloseListener = listener;
		if (Build.VERSION.SDK_INT < 11) {
			buttonSearchClose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (defaultOnCloseListener != null) {
						defaultOnCloseListener.onClose();
					}
				}
			});
		}
	}
}
