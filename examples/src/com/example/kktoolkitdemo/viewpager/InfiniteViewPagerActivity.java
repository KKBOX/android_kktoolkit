package com.example.kktoolkitdemo.viewpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kktoolkitdemo.R;
import com.example.kktoolkitdemo.SampleUtil;
import com.kkbox.toolkit.listview.adapter.InfiniteViewPagerAdapter;
import com.kkbox.toolkit.ui.InfiniteViewPager;
import com.kkbox.toolkit.ui.KKActivity;
import com.kkbox.toolkit.ui.OnInfiniteViewPagerPageChangeListener;
import com.kkbox.toolkit.utils.KKDebug;

import java.util.ArrayList;

/**
 * Created by gigichien on 13/10/21.
 */
public class InfiniteViewPagerActivity extends KKActivity {
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
        mViewPager.setOnPageChangeListener(new OnInfiniteViewPagerPageChangeListener(mViewPager) {
            @Override
            public void onLoopPageSelected(int position) {
                KKDebug.i(SampleUtil.LOG_TAG, "OnInfiniteViewPagerPageChangeListener onLoopPageSelected");
            }

            @Override
            public void onPageScrollLeft() {
                KKDebug.i(SampleUtil.LOG_TAG, "OnInfiniteViewPagerPageChangeListener onPageScrollLeft");
            }

            @Override
            public void onPageScrollRight() {
                KKDebug.i(SampleUtil.LOG_TAG, "OnInfiniteViewPagerPageChangeListener onPageScrollRight");
            }
        });

        mViewPager.setCurrentItem(0);
    }

    private class ViewPagerAdapter extends InfiniteViewPagerAdapter{

        public ViewPagerAdapter(ArrayList<Integer> content, boolean loopEnabled) {
            super((ArrayList)content, loopEnabled);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView v = new TextView(getApplicationContext());
            v.setGravity(Gravity.CENTER);
            Integer index = (Integer)getItem(position);
            switch(index.intValue()){
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
            ((ViewPager)container).addView(v);
            return v;
        }
    }
}
