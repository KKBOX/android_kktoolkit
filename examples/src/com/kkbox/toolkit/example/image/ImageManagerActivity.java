package com.kkbox.toolkit.example.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kkbox.toolkit.example.ExampleActivity;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.example.SampleUtil;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.image.KKImageRequest;

public class ImageManagerActivity extends ExampleActivity {
	private LinearLayout mLinearLayout;
	private Button mAuto, mManual, mClear, mDownload;
	private ImageView[] mWeatherIcon;
	private KKImageManager mImageManager;

	class ExampleLoadImageListener implements KKImageManager.OnBitmapReceivedListener {
		private ImageView imageView;

		public ExampleLoadImageListener(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void onBitmapReceived(KKImageRequest request, Bitmap bitmap) {
			imageView.setImageBitmap(bitmap);
			KKImageManager.autoRecycleViewSourceBitmap(imageView);
		}
	}

	class ExampleDownloadImageListener implements KKImageManager.OnImageDownloadedListener {
		private ImageView imageView;
		private String path;

		public ExampleDownloadImageListener(ImageView imageView, String path) {
			this.imageView = imageView;
			this.path = path;
		}

		@Override
		public void onImageDownloaded(KKImageRequest request) {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			imageView.setImageBitmap(bitmap);
			KKImageManager.autoRecycleViewSourceBitmap(imageView);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		mLinearLayout = (LinearLayout) findViewById(R.id.weather_table);
		mAuto = (Button) findViewById(R.id.update_view);
		mManual = (Button) findViewById(R.id.load_image);
		mDownload = (Button) findViewById(R.id.download_image);
		mClear = (Button) findViewById(R.id.clear_cache);

		mImageManager = new KKImageManager(this, null);

		mWeatherIcon = new ImageView[5];
		for (int i = 0; i < 5; i++) {
			mWeatherIcon[i] = new ImageView(this);
			mWeatherIcon[i].setLayoutParams(new ViewGroup.LayoutParams(150, 150));
			mWeatherIcon[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
			mLinearLayout.addView(mWeatherIcon[i]);
		}
		mAuto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				for (int i = 0; i < SampleUtil.pic_url.length; i++) {
					mImageManager.updateViewSource(mWeatherIcon[i], SampleUtil.pic_url[i], null, R.drawable.ic_launcher);
				}
			}
		});

		mManual.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				for (int i = 0; i < SampleUtil.pic_url.length; i++) {
					mImageManager.loadBitmap(SampleUtil.pic_url[i], null, new ExampleLoadImageListener(mWeatherIcon[i]));
				}
			}
		});

		mDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				for (int i = 0; i < SampleUtil.pic_url.length; i++) {
					String path = KKImageManager.getTempImagePath(ImageManagerActivity.this, SampleUtil.pic_url[i]);
					mImageManager.downloadBitmap(SampleUtil.pic_url[i], path, new ExampleDownloadImageListener(mWeatherIcon[i], path));
				}
			}
		});

		mClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				KKImageManager.clearCacheFiles(ImageManagerActivity.this);
			}
		});

	}

	private void resetIcon() {
		for (ImageView v : mWeatherIcon) {
			if (v != null) {
				v.setImageDrawable(null);
			}
		}
	}
}
