package com.kkbox.toolkit.example.listview;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.kkbox.toolkit.api.KKAPIListener;
import com.kkbox.toolkit.example.ExampleActivity;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.example.SampleUtil;
import com.kkbox.toolkit.example.api.ExampleWeatherAPI;
import com.kkbox.toolkit.utils.KKDebug;
import com.kkbox.toolkit.widget.KKListView;

public class KKListViewActivity extends ExampleActivity {
	private static final String TAG = "KKListViewActivity";
	private KKListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout root = new FrameLayout(this);
		setContentView(root);
		mListView = new KKListView(this);
		mListView.setPullToRefresh(pullToRefreshListener);
		mListView.setLoadMore(loadMoreListener);
		root.addView(mListView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		String[] mTestItem = getResources().getStringArray(R.array.city);
		ArrayAdapter<String> mAdapter = new ArrayAdapter(KKListViewActivity.this, android.R.layout.simple_list_item_1, mTestItem);
		mListView.setAdapter(mAdapter);

	}

	private KKListView.OnRefreshListener pullToRefreshListener = new KKListView.OnRefreshListener() {
		@Override
		public void onRefresh() {
			KKDebug.i(TAG, "onRefresh");
			loadForecastData(false);
		}
	};

	private KKListView.OnLoadMoreListener loadMoreListener = new KKListView.OnLoadMoreListener() {
		@Override
		public void onLoadMore() {
			KKDebug.i(TAG, "onLoadMore");
			loadForecastData(true);
		}
	};

	private void loadForecastData(final boolean more) {
		ExampleWeatherAPI mAPI = new ExampleWeatherAPI();
		mAPI.setAPIListener(new KKAPIListener() {
			@Override
			public void onAPIComplete() {
				if (more) {
					mListView.loadMoreFinished();
				} else {
					mListView.loadCompleted();
				}
			}

			@Override
			public void onAPIError(int errorCode) {
				KKDebug.e("KKListViewActivity", "onAPIError");
			}
		});

		mAPI.start(SampleUtil.test_item[0]);
	}
}
