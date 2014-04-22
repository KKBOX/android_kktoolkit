package com.kkbox.toolkit.example.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kkbox.toolkit.example.ExampleActivity;
import com.kkbox.toolkit.example.R;
import com.kkbox.toolkit.example.SampleUtil;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.image.KKImageRequest;
import com.kkbox.toolkit.utils.KKDebug;

import java.io.IOException;

public class ImageManagerActivity extends ExampleActivity {
	private ImageView[] viewIcon;
	private KKImageManager imageManager;
	private ImageView viewImage;

	class ExampleLoadImageListener implements KKImageManager.OnBitmapReceivedListener {
		private ImageView imageView;

		public ExampleLoadImageListener(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void onBitmapReceived(KKImageRequest request, Bitmap bitmap) {
			Animation fadeIn = AnimationUtils.loadAnimation(ImageManagerActivity.this, R.anim.fade_in);
			imageView.startAnimation(fadeIn);
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
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.weather_table);
		Button buttonAutoLoad = (Button) findViewById(R.id.button_auto_load);
		Button buttonManualLoad = (Button) findViewById(R.id.button_manual_load);
		Button buttonDownload = (Button) findViewById(R.id.button_download);
		Button buttonClearCache = (Button) findViewById(R.id.button_clear_cache);
		Button buttonSaveCache = (Button) findViewById(R.id.button_save_cache);
		Button buttonLoadCache = (Button) findViewById(R.id.button_load_cache);
		viewImage = (ImageView) findViewById(R.id.view_image);
		imageManager = new KKImageManager(this, null);
		imageManager.enableSequentialImageLoading(true);

		viewIcon = new ImageView[5];
		for (int i = 0; i < 5; i++) {
			viewIcon[i] = new ImageView(this);
			viewIcon[i].setLayoutParams(new ViewGroup.LayoutParams(150, 150));
			viewIcon[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
			linearLayout.addView(viewIcon[i]);
		}
		buttonAutoLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				for (int i = 0; i < SampleUtil.pic_url.length; i++) {
					imageManager.updateViewSource(viewIcon[i], SampleUtil.pic_url[i], null, R.drawable.ic_launcher);
				}
			}
		});

		buttonManualLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				for (int i = 0; i < SampleUtil.pic_url.length; i++) {
					imageManager.loadBitmap(SampleUtil.pic_url[i], null, new ExampleLoadImageListener(viewIcon[i]));
				}
			}
		});

		buttonDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				for (int i = 0; i < SampleUtil.pic_url.length; i++) {
					String path = KKImageManager.getTempImagePath(ImageManagerActivity.this, SampleUtil.pic_url[i]);
					imageManager.downloadBitmap(SampleUtil.pic_url[i], path, new ExampleDownloadImageListener(viewIcon[i], path));
				}
			}
		});

		buttonClearCache.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				KKImageManager.clearCacheFiles(ImageManagerActivity.this);
			}
		});

		buttonSaveCache.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.small);
				try {
					imageManager.saveCache("example_cache", bitmap);
				} catch (IOException e) {
					KKDebug.e("Failed to save image cache");
				}
			}
		});

		buttonLoadCache.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetIcon();
				Bitmap bitmap = imageManager.loadCache("example_cache");
				if (bitmap == null) {
					KKDebug.e("No image cache is available");
				} else {
					viewImage.setImageBitmap(bitmap);
				}
			}
		});
	}

	private void resetIcon() {
		for (ImageView imageView : viewIcon) {
			if (imageView != null) {
				imageView.setImageDrawable(null);
			}
		}
		viewImage.setImageDrawable(null);
	}
}
