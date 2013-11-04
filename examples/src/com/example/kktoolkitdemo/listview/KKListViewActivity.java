package com.example.kktoolkitdemo.listview;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.kktoolkitdemo.R;
import com.example.kktoolkitdemo.SampleUtil;
import com.example.kktoolkitdemo.api.ExampleForecastAPI;
import com.example.kktoolkitdemo.api.ExampleWeatherAPI;
import com.kkbox.toolkit.api.KKAPIListener;
import com.kkbox.toolkit.api.KKAPIRequest;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.KKListView;
import com.kkbox.toolkit.utils.KKDebug;

import java.util.ArrayList;

public class KKListViewActivity extends KKActivity {
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
            KKDebug.i(TAG,"onRefresh");
            loadForecastData(false);
        }
    };

    private KKListView.OnLoadMoreListener loadMoreListener = new KKListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            KKDebug.i(TAG,"onLoadMore");
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
