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

import java.util.ArrayList;

/**
 * Created by gigichien on 13/10/22.
 */
public class KKListViewActivity extends KKActivity {
    private KKListView mListView;
    private ArrayAdapter<String> mAdapter;
    private ExampleWeatherAPI mAPI;
    private ExampleWeatherAPI.WeatherData mWeatherData;
    private ArrayList<String> mCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        this.setContentView(root);
        mListView = new KKListView(this);
        root.addView(mListView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mListView.setPullToRefresh(pullToRefreshListener);
        mListView.setLoadMore(loadMoreListener);
        mCityList = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            mCityList.add(i, SampleUtil.test_list_item[i]);
        }
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter(KKListViewActivity.this, android.R.layout.simple_list_item_1, mCityList);
            mListView.setAdapter(mAdapter);
        }
        loadForecastData(0, 1);
    }

    private KKListView.OnRefreshListener pullToRefreshListener = new KKListView.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadForecastData(0, 10);
        }
    };

    private KKListView.OnLoadMoreListener loadMoreListener = new KKListView.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            mListView.loadMoreFinished();
        }
    };

    private int queue = 0;
    private KKAPIListener exampleAPIListener = new KKAPIListener() {
        @Override
        public void onAPIComplete() {
            String city = mAPI.getWeatherData().city_name;
            int size = mCityList.size();
            int index = 0;
            for (int i = 0; i < size; i++) {
                if (SampleUtil.test_list_item[i].equals(city)) {
                    index = i;
                    break;
                }
            }
            String description = SampleUtil.test_list_item[index] + " - " + mAPI.getWeatherData().description;

            mCityList.set(index, description);

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }

            queue--;

            if (queue <= 0) {
                mListView.loadCompleted();
                queue = 0;
            }
        }

        @Override
        public void onAPIError(int errorCode) {

        }
    };


    private void loadForecastData(int start, int end) {

        if (mAPI == null) {
            mAPI = new ExampleWeatherAPI();
            mAPI.setAPIListener(exampleAPIListener);

        }
        for (int i = start; i < end; i++) {

            Log.e("GGG", "loadDorecastData - " +SampleUtil.test_list_item[i]);
            mAPI.setAPIListener(exampleAPIListener);
            mAPI.start(SampleUtil.test_list_item[i]);
            queue++;
        }
    }
}
