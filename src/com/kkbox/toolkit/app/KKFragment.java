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

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.kkbox.toolkit.R;
import com.kkbox.toolkit.utils.KKDebug;
import com.kkbox.toolkit.widget.KKMessageView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class KKFragment extends Fragment {

	public static class AnimationType {
		public static final int NONE = 0;
		public static final int PUSH = 1;
		public static final int POP = 2;
		public static final int FADE_OUT = 3;
	}

	public static class DataFetchStatus {
		public static final int ERROR = -1;
		public static final int NONE = 0;
		public static final int SUCCESS = 1;
	}

	private static Fragment activityResultCallbackFragment;
	private static int animationType;
	private Activity activity;
	private KKMessageView viewMessage;
	private View customErrorView;
	private View customLoadingView;
	private int dataFetchedStatus;
	private boolean uiLoaded = false;
	private boolean animationEnded = false;
	private boolean autoDataLoading = true;

	public KKFragment() {}

	public static void setAnimation(int type) {
		animationType = type;
	}

	static void callbackActivityResult(int requestCode, int resultCode, Intent data) {
		Fragment fragment = activityResultCallbackFragment;
		activityResultCallbackFragment = null;
		if (fragment != null) {
			fragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void setCustomErrorView(View view) {
		customErrorView = view;
	}

	public void setCustomLoadingView(View view) {
		customLoadingView = view;
	}

	protected void resetFetchData() {
		dataFetchedStatus = DataFetchStatus.NONE;
	}

	public void onFetchData() {
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

	protected void fixStateForNestedFragment() {
		// workaround for sdk < 4.2 Orz
		if (Build.VERSION.SDK_INT < 17) {
			try {
				int CREATED = 1;          // Created.
				int ACTIVITY_CREATED = 2; // The activity has finished its creation.
				int STOPPED = 3;          // Fully created, not started.
				int STARTED = 4;          // Created and started, not resumed.
				int RESUMED = 5;          // Created started and resumed.
				Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
				Field mState = Fragment.class.getDeclaredField("mState");
				Method dispatchResumeMethod = childFragmentManager.getType().getDeclaredMethod("dispatchResume", null);
				Method dispatchStartMethod = childFragmentManager.getType().getDeclaredMethod("dispatchStart", null);
				Method dispatchActivityCreatedMethod = childFragmentManager.getType().getDeclaredMethod("dispatchActivityCreated", null);
				Method dispatchCreateMethod = childFragmentManager.getType().getDeclaredMethod("dispatchCreate", null);
				mState.setAccessible(true);
				childFragmentManager.setAccessible(true);
				int state = mState.getInt(this);
				if (state >= RESUMED) {
					dispatchResumeMethod.invoke(childFragmentManager.get(this), null);
				} else if (state >= STARTED) {
					dispatchStartMethod.invoke(childFragmentManager.get(this), null);
				} else if (state >= ACTIVITY_CREATED) {
					dispatchActivityCreatedMethod
							.invoke(childFragmentManager.get(this), null);
				} else if (state >= CREATED) {
					dispatchCreateMethod.invoke(childFragmentManager.get(this), null);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
	}

	public void onReceiveMessage(Bundle arguments) {}

	protected void initView(View view) {
		viewMessage = (KKMessageView) view.findViewById(R.id.view_message);
		uiLoaded = false;
	}

	public void switchToFragment(Fragment fragment, Bundle arguments) {
		if (arguments != null) {
			fragment.setArguments(arguments);
		}
		FragmentTransaction fragmentTransaction;
		FragmentManager fragmentManager;
		if (getArguments() == null || !getArguments().getBoolean("nested_in_tab", false)) {
			fragmentManager = getFragmentManager();
		} else {
			fragmentManager = getParentFragment().getFragmentManager();
		}
		fragmentTransaction = fragmentManager.beginTransaction();
		KKFragment.setAnimation(KKFragment.AnimationType.PUSH);
		fragmentTransaction.replace(R.id.sub_fragment, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		fragmentManager.executePendingTransactions();
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
		} else if (animationType == AnimationType.FADE_OUT) {
			if (enter) {
				animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
			} else {
				animation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
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
		KKDebug.i(((Object) this).getClass().getSimpleName() + " onResume");
		((KKServiceActivity) activity).activateSubFragment(this);
		if (autoDataLoading) {
			if (dataFetchedStatus == DataFetchStatus.SUCCESS) {
				onLoadUI();
			} else {
				onFetchData();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		((KKServiceActivity) activity).deactivateSubFragment(this);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
		activityResultCallbackFragment = this;
	}
}
