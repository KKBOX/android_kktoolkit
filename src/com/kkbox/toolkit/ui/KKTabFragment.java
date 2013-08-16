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
 * KKTabFragment
 */
package com.kkbox.toolkit.ui;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.kkbox.toolkit.R;

public abstract class KKTabFragment extends KKFragment {
	private int currentIndex = -1;
	private RadioGroup radioGroup;
	private KKFragment currentFragment;
	private boolean showSubFragmentAnimation = false;

	private final OnClickListener buttonClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int radioButtonId = radioGroup.getCheckedRadioButtonId();
			View radioButton = radioGroup.findViewById(radioButtonId);
			currentIndex = radioGroup.indexOfChild(radioButton);
			KKFragment fragment = (KKFragment)getChildFragmentManager().findFragmentByTag("" + currentIndex);
			FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
			if (currentFragment != null) {
				fragmentTransaction.detach(currentFragment);
			}
			if (fragment == null) {
				Bundle arguments = new Bundle();
				arguments.putBoolean("nested_in_tab", true);
				fragment = onRequestTabFragment(currentIndex, arguments);
				if (fragment == null) { return; }
				fragment.setArguments(arguments);
				fragmentTransaction.replace(R.id.sub_fragment, fragment, String.valueOf(currentIndex));
			} else {
				fragmentTransaction.attach(fragment);
			}
			if (currentFragment != null) {
				KKFragment.setAnimation(KKFragment.AnimationType.NONE);
			} else if (showSubFragmentAnimation) {
				KKFragment.setAnimation(KKFragment.AnimationType.PUSH);
			}
			fragmentTransaction.commit();
			currentFragment = fragment;
		}
	};

	public KKTabFragment() {}

	public void invalidateCurrentTabFragment() {
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		if (currentFragment != null) {
			fragmentTransaction.detach(currentFragment);
		} else {
			return;
		}
		Bundle arguments = new Bundle();
		arguments.putBoolean("nested_in_tab", true);
		KKFragment fragment = onRequestTabFragment(currentIndex, arguments);
		if (fragment == null) { return; }
		fragment.setArguments(arguments);
		fragmentTransaction.replace(R.id.sub_fragment, fragment, String.valueOf(currentIndex));
		KKFragment.setAnimation(KKFragment.AnimationType.NONE);
		fragmentTransaction.commit();
		currentFragment = fragment;
	}

	protected void initView(View view, int[] buttonTextResourcetId, boolean showSubFragmentAnimation, int currentIndex) {
		initView(view);
		if (this.currentIndex == -1) {
			this.currentIndex = currentIndex;
		}
		this.showSubFragmentAnimation = showSubFragmentAnimation;
		radioGroup = (RadioGroup)view.findViewById(R.id.button_radiogroup);

		TypedValue typedValue = new TypedValue();
		getKKActivity().getTheme().resolveAttribute(R.attr.KKTabFragmentStyle, typedValue, true);
		TypedArray array = getKKActivity().obtainStyledAttributes(typedValue.resourceId,
				new int[] { R.attr.KKTabButtonBackgroundLeft, R.attr.KKTabButtonBackgroundMiddle, R.attr.KKTabButtonBackgroundRight });
		int tabButtonBackgroundLeftResourceId = array.getResourceId(0, -1);
		int tabButtonBackgroundMiddleResourceId = array.getResourceId(1, -1);
		int tabButtonBackgroundRightResourceId = array.getResourceId(2, -1);
		array.recycle();
		array = getKKActivity().obtainStyledAttributes(typedValue.resourceId, new int[] { R.attr.KKTabBackground });
		int backgroundResourceId = array.getResourceId(0, -1);
		FrameLayout layoutRadioBar = (FrameLayout)view.findViewById(R.id.layout_radio_bar);
		if (backgroundResourceId != -1) {
			layoutRadioBar.setBackgroundResource(backgroundResourceId);
		}
		array.recycle();

		array = getKKActivity().obtainStyledAttributes(typedValue.resourceId, new int[] { R.attr.KKTabOverlay });
		int overlayResourceId = array.getResourceId(0, -1);
		ImageView viewOverlay = (ImageView)view.findViewById(R.id.view_overlay);
		if (overlayResourceId != -1) {
			viewOverlay.setBackgroundResource(overlayResourceId);
		}
		array.recycle();

		array = getKKActivity().obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.textSize });
		int textSize = array.getDimensionPixelSize(0, -1);
		array.recycle();
		array = getKKActivity().obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.textColor });
		ColorStateList textColor = array.getColorStateList(0);
		array.recycle();
		array = getKKActivity().obtainStyledAttributes(typedValue.resourceId, new int[] { android.R.attr.height });
		int height = array.getDimensionPixelSize(0, -1);
		array.recycle();
		for (int i = 0; i < buttonTextResourcetId.length; i++) {
			RadioButton radioButton = new RadioButton(getActivity());
			if (i == 0 && tabButtonBackgroundLeftResourceId != -1) {
				radioButton.setBackgroundResource(tabButtonBackgroundLeftResourceId);
			} else if (i == buttonTextResourcetId.length - 1 && tabButtonBackgroundRightResourceId != -1) {
				radioButton.setBackgroundResource(tabButtonBackgroundRightResourceId);
			} else if (tabButtonBackgroundMiddleResourceId != -1) {
				radioButton.setBackgroundResource(tabButtonBackgroundMiddleResourceId);
			}
			radioButton.setText(buttonTextResourcetId[i]);
			radioButton.setButtonDrawable(new StateListDrawable());
			radioButton.setGravity(Gravity.CENTER);
			radioButton.setTextColor(textColor);
			radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			radioButton.setLayoutParams(new RadioGroup.LayoutParams(0, height, 1));
			radioButton.setOnClickListener(buttonClickListener);
			radioGroup.addView(radioButton);
		}
		currentFragment = null;
		radioGroup.getChildAt(this.currentIndex).performClick();

	}

	protected abstract KKFragment onRequestTabFragment(int index, Bundle arguments);
}
