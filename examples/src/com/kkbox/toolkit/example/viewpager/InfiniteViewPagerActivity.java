package com.kkbox.toolkit.example.viewpager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kkbox.toolkit.example.ExampleActivity;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.example.SampleUtil;
import com.kkbox.toolkit.utils.KKDebug;
import com.kkbox.toolkit.widget.InfiniteViewPager;
import com.kkbox.toolkit.widget.InfiniteViewPager.OnInfiniteViewPagerPageChangeListener;
import com.kkbox.toolkit.widget.InfiniteViewPagerAdapter;

import java.util.ArrayList;

public class InfiniteViewPagerActivity extends ExampleActivity {
	private InfiniteViewPager mViewPager;

	private ViewPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);
		mViewPager = (InfiniteViewPager) findViewById(R.id.view_pager);

		ArrayList<Integer> mColorList = new ArrayList<Integer>();
		mColorList.add(1);
		mColorList.add(2);
		mColorList.add(3);

		mAdapter = new ViewPagerAdapter(mColorList, true);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnInfiniteViewPagerPageChangeListener(new OnInfiniteViewPagerPageChangeListener() {
			@Override
			public void onLoopPageSelected(int position, boolean isManual) {
				KKDebug.i(SampleUtil.LOG_TAG, "OnInfiniteViewPagerPageChangeListener onLoopPageSelected"
						+ " isManual: " + isManual);
			}

			@Override
			public void onPageScrollLeft(boolean isManual) {
				KKDebug.i(SampleUtil.LOG_TAG, "OnInfiniteViewPagerPageChangeListener onPageScrolledLeft"
						+ " isManual: " + isManual);
			}

			@Override
			public void onPageScrolledRight(boolean isManual) {
				KKDebug.i(SampleUtil.LOG_TAG, "OnInfiniteViewPagerPageChangeListener onPageScrolledRight"
						+ " isManual: " + isManual);
			}
		});

		mViewPager.setCurrentItem(0);
	}

	private class ViewPagerAdapter extends InfiniteViewPagerAdapter {

		public ViewPagerAdapter(ArrayList<Integer> content, boolean loopEnabled) {
			super((ArrayList) content, loopEnabled);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TextView v = new TextView(getApplicationContext());
			v.setGravity(Gravity.CENTER);
			Integer index = (Integer) getItem(position);
			switch (index.intValue()) {
				case 1:
					v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
					v.setText("Page 1");
					break;
				case 2:
					v.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
					v.setText("Page 2");
					break;
				case 3:
					v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
					v.setText("Page 3");
					break;

			}
			container.addView(v);
			return v;
		}
	}
}
