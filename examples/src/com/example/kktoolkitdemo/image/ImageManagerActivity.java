package com.example.kktoolkitdemo.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kktoolkitdemo.R;
import com.example.kktoolkitdemo.SampleUtil;
import com.kkbox.toolkit.image.KKImageListener;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.ui.KKActivity;

public class ImageManagerActivity extends KKActivity{
    LinearLayout mLinearLayout;
    Button mAuto, mManual, mClear;
    ImageView[] mWeatherIcon;
    KKImageManager mImageManager;
    TextView mStatus;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mLinearLayout = (LinearLayout) findViewById(R.id.weather_table);
        mAuto = (Button) findViewById(R.id.update_view);
        mManual = (Button) findViewById(R.id.load_image);
        mClear = (Button) findViewById(R.id.clear_cache);
        mStatus = (TextView) findViewById(R.id.image_status);

        mImageManager = new KKImageManager(this, null);

        mWeatherIcon = new ImageView[5];
        for(int i =0; i < 5 ;i++){
            mWeatherIcon[i] = new ImageView(this);
            mWeatherIcon[i].setLayoutParams(new ViewGroup.LayoutParams(150, 150));
            mWeatherIcon[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            mLinearLayout.addView(mWeatherIcon[i]);
        }
        mAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = SampleUtil.pic_url.length;
                resetIcon();
                for(int i = 0; i < size; i++){
                    mImageManager.updateViewSource(mWeatherIcon[i], SampleUtil.pic_url[i], null, R.drawable.ic_launcher);
                }
            }
        });

        mManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int size = SampleUtil.pic_url.length;
                resetIcon();
                for(int i = 0; i < size ; i++) {
                    final ImageView v = mWeatherIcon[i];
                    
                    mImageManager.loadBitmap(new KKImageListener() {
                        @Override
                        public void onReceiveBitmap(Bitmap bitmap) {
                            v.setImageBitmap(bitmap);
                            mImageManager.autoRecycleViewSourceBitmap(v);
                        }
                    }, SampleUtil.pic_url[i], null);
                }
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetIcon();
                mImageManager.clearCacheFiles(ImageManagerActivity.this);
            }
        });

    }

    private void resetIcon(){
        for(ImageView v : mWeatherIcon){
            if(v != null){
                v.setImageDrawable(null);
            }
        }
    }
}
