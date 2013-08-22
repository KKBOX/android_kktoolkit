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
 * KKFragment
 */
package com.kkbox.toolkit.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.kkbox.toolkit.R;
import com.kkbox.toolkit.utils.KKDebug;

public abstract class KKFragment extends Fragment {

	public static class AnimationType {
		public static final int NONE = 0;
		public static final int PUSH = 1;
		public static final int POP = 2;
	}

	public static class DataFetchStatus {
		public static final int ERROR = -1;
		public static final int NONE = 0;
		public static final int SUCCESS = 1;
	}

	private static int animationType;
	private KKActivity activity;
	private KKMessageView viewMessage;
	private View customErrorView;
	private View customLoadingView;
	private int dataFetchedStatus;
	private boolean uiLoaded = false;
	private boolean animationEnded = false;
	private boolean autoDataLoading = true;
	private KKMenuCompat menuCompat;

	public KKFragment() {}

	public static void setAnimation(int type) {
		animationType = type;
	}

	public void onLoadData() {}

	public void setCustomErrorView(View view) {
		customErrorView = view;
	}

	public void setCustomLoadingView(View view) {
		customLoadingView = view;
	}

	protected void startFetchData() {
		if (viewMessage != null) {
			if (customLoadingView != null) {
				viewMessage.setCustomView(customLoadingView);
			} else {
				viewMessage.setSingleTextView(getString(R.string.loading));
			}
			viewMessage.show();
		}
	}

	protected void onLoadUI() {
		uiLoaded = true;
		dataFetchedStatus = DataFetchStatus.SUCCESS;
		if (viewMessage != null) {
			viewMessage.hide();
		}
	}

	public void enableAutoDataLoading(boolean enabled) {
		autoDataLoading = enabled;
	}

	protected void finishFetchData() {
		dataFetchedStatus = DataFetchStatus.SUCCESS;
		if (animationEnded) {
			onLoadUI();
		}
	}

	protected void fetchDataFailed() {
		uiLoaded = true;
		if (viewMessage != null) {
			if (customErrorView != null) {
				viewMessage.setCustomView(customErrorView);
			} else {
				viewMessage.setSingleTextView(getString(R.string.loading_error));
			}
			viewMessage.show();
		}
		dataFetchedStatus = DataFetchStatus.ERROR;
	}

	public KKActivity getKKActivity() {
		return (KKActivity)getActivity();
	}

	@Override
	public void setHasOptionsMenu(boolean hasMenu) {
		if (hasMenu && Build.VERSION.SDK_INT < 11) {
			boolean foundMenu = false;
			for (int i = 0; i < menuCompat.size(); i++) {
				if (menuCompat.getItem(i).getShowAsActionFlags() == KKMenuItemCompat.SHOW_AS_ACTION_NEVER) {
					foundMenu = true;
					break;
				}
			}
			super.setHasOptionsMenu(foundMenu);
		} else {
			super.setHasOptionsMenu(hasMenu);
		}
	}

	public void onCreateCompatOptionsMenu(KKMenuCompat menu, KKMenuInflaterCompat inflater) {
		this.menuCompat = menu;
		if (activity.getKKActionBar() != null) {
			for (int i = 0; i < menuCompat.size(); i++) {
				getKKActivity().checkActionButtonCreated(this, menuCompat.getItem(i));
			}
			activity.getKKActionBar().addActionMenu(menuCompat);
		}
	}

	public void onCompatOptionsItemSelected(KKMenuItemCompat item) {
		getKKActivity().checkSearchViewSelected(item);
	}

	public void onPrepareCompatOptionsMenu(KKMenuCompat menu) {}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		KKMenuItemCompat menuItemCompat = menuCompat.findItem(item.getItemId());
		if (menuItemCompat != null) {
			onCompatOptionsItemSelected(menuItemCompat);
		} else {
			onCompatOptionsItemSelected(new KKMenuItemCompat(item));
		}
		return true;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		onPrepareCompatOptionsMenu(menuCompat);
		activity.prepareOptionsMenu(menuCompat, menu);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getKKActivity().createOptionsMenu(this.menuCompat, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (KKActivity)getActivity();
		onCreateCompatOptionsMenu(new KKMenuCompat(activity, this), activity.getMenuInflater());
	}

	public void onReceiveMessage(Bundle arguments) {}

	protected void initView(View view) {
		viewMessage = (KKMessageView)view.findViewById(R.id.view_message);
		uiLoaded = false;
		if (Build.VERSION.SDK_INT >= 11) {
			view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	public void switchToFragment(Fragment fragment, Bundle arguments) {
		if (arguments != null) {
			fragment.setArguments(arguments);
		}
		FragmentTransaction fragmentTransaction;
		if (getArguments() == null || !getArguments().getBoolean("nested_in_tab", false)) {
			fragmentTransaction = getFragmentManager().beginTransaction();
		} else {
			fragmentTransaction = getParentFragment().getFragmentManager().beginTransaction();
		}
		KKFragment.setAnimation(KKFragment.AnimationType.PUSH);
		fragmentTransaction.replace(R.id.sub_fragment, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		animationEnded = false;
		Animation animation;
		if (animationType == AnimationType.POP) {
			if (enter) {
				animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_left);
			} else {
				animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_right);
			}
		} else if (animationType == AnimationType.PUSH) {
			if (enter) {
				animation = AnimationUtils.loadAnimation(activity, R.anim.slide_in_right);
			} else {
				animation = AnimationUtils.loadAnimation(activity, R.anim.slide_out_left);
			}
		} else {
			animation = new Animation() {};
			animation.setDuration(0);
		}
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation a) {
				animationEnded = true;
				if (dataFetchedStatus == DataFetchStatus.SUCCESS && !uiLoaded) {
					onLoadUI();
				}
			}

			@Override
			public void onAnimationStart(Animation a) {}

			@Override
			public void onAnimationRepeat(Animation a) {}
		});
		return animation;
	}

	@Override
	public void onResume() {
		super.onResume();
		KKDebug.i(getClass().getSimpleName() + " onResume");
		activity.activateSubFragment(this);
		if (activity.getKKActionBar() != null) {
			activity.invalidateOptionsMenu();
		}
		if (autoDataLoading) {
			if (dataFetchedStatus == DataFetchStatus.SUCCESS) {
				onLoadUI();
			} else {
				onLoadData();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.deactivateSubFragment(this);
	}
}
