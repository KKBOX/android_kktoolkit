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
package com.kkbox.toolkit.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.kkbox.toolkit.image.KKImageManager.OnBitmapReceivedListener;
import com.kkbox.toolkit.image.KKImageManager.OnImageDownloadedListener;
import com.kkbox.toolkit.internal.image.KKImageRequestListener;
import com.kkbox.toolkit.utils.UserTask;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.Cipher;

public class KKImageRequest extends UserTask<Object, Headers, Bitmap> {
	private final int BUFFER_SIZE = 1024;
	private byte[] buffer = new byte[BUFFER_SIZE];
	private OkHttpClient httpClient = new OkHttpClient();
	private Response response;
	private KKImageRequestListener listener;
	private Headers headers;
	private Context context;
	private View view;
	private OnBitmapReceivedListener onBitmapReceivedListener;
	private OnImageDownloadedListener onImageDownloadedListener;
	private String url = "";
	private String localPath;
	private String cachePath;
	private int actionType;
	private boolean isNetworkError = false;
	private Cipher cipher = null;
	private boolean saveToLocal = false;
	private boolean interuptFlag = false;
	private static ReentrantLock fileLock = new ReentrantLock();

	public KKImageRequest(Context context, String url, String localPath, View view, boolean updateBackground, Cipher cipher,
			boolean saveToLocal, KKImageRequestListener imageRequestListener) {
		// update only
		this.view = view;
		this.saveToLocal = saveToLocal;
		if (updateBackground) {
			actionType = KKImageManager.ActionType.UPDATE_VIEW_BACKGROUND;
		} else {
			actionType = KKImageManager.ActionType.UPDATE_VIEW_SOURCE;
		}
		init(context, url, localPath, cipher, imageRequestListener);
	}

	public KKImageRequest(Context context, String url, String localPath, View view, boolean updateBackground, Cipher cipher,
			boolean saveToLocal, KKImageRequestListener imageRequestListener, OnImageDownloadedListener onImageDownloadedListener) {
		// update and save
		this(context, url, localPath, view, updateBackground, cipher, saveToLocal, imageRequestListener);
		this.onImageDownloadedListener = onImageDownloadedListener;
	}

	public KKImageRequest(Context context, String url, String localPath, Cipher cipher, KKImageRequestListener imageRequestListener,
	        OnImageDownloadedListener onImageDownloadedListener) {
		// download
		actionType = KKImageManager.ActionType.DOWNLOAD;
		this.onImageDownloadedListener = onImageDownloadedListener;
		init(context, url, localPath, cipher, imageRequestListener);
	}

	public KKImageRequest(Context context, String url, String localPath, Cipher cipher, KKImageRequestListener imageRequestListener,
	        OnBitmapReceivedListener onBitmapReceivedListener) {
		// callback
		actionType = KKImageManager.ActionType.CALL_LISTENER;
		this.onBitmapReceivedListener = onBitmapReceivedListener;
		init(context, url, localPath, cipher, imageRequestListener);
	}

	private void init(Context context, String url, String localPath, Cipher cipher, KKImageRequestListener imageRequestListener) {
		httpClient.setConnectTimeout(10000, TimeUnit.MILLISECONDS);
		httpClient.setReadTimeout(10000, TimeUnit.MILLISECONDS);
		this.url = url;
		this.localPath = localPath;
		this.context = context;
		this.cipher = cipher;
		this.listener = imageRequestListener;
	}

	public synchronized void cancel() {
		if (!cancel(true)) {
			if (listener != null) {
				listener.onCancelled(this);
			}
			listener = null;
		}
		interuptFlag = true;
	}

	@Override
	public synchronized void onCancelled() {
		if (listener != null) {
			listener.onCancelled(this);
		}
		listener = null;
	}

	public String getUrl() {
		return url;
	}

	public View getView() { // TODO: refactor this
		return view;
	}

	public int getActionType() {
		return actionType;
	}

	public Headers getHttpResponseHeaders() {
		return headers;
	}

	@Override
	public Bitmap doInBackground(Object... params) {
		Bitmap bitmap;
		try {
			int readLength;
			cachePath = KKImageManager.getTempImagePath(context, url);
			File cacheFile = new File(cachePath);
			File localFile = null;
			String tempFilePath = context.getCacheDir().getAbsolutePath() + File.separator + "image" + File.separator + hashCode();
			if (localPath != null) {
				localFile = new File(localPath);
			}
			try {
				if (cacheFile.exists()) {
					if (actionType == KKImageManager.ActionType.DOWNLOAD) {
						if (localFile == null || !localFile.exists()) {
							cryptToFile(cachePath, localPath);
						}
						return null;
					} else {
						bitmap = decodeBitmap(cachePath);
						if (bitmap != null) {
							if (localPath != null && saveToLocal && (localFile == null || !localFile.exists())) {
								cryptToFile(cachePath, localPath);
							}
							return bitmap;
						} else {
							removeCacheFile();
						}
					}
				}
				if (localFile != null && localFile.exists()) {
					if (actionType == KKImageManager.ActionType.DOWNLOAD) {
						return null;
					} else {
						cryptToFile(localPath, tempFilePath);
						moveFileTo(tempFilePath, cachePath);
						bitmap = decodeBitmap(cachePath);
						if (bitmap != null) {
							return bitmap;
						} else {
							removeCacheFile();
						}
					}
				}
			} catch (Exception e) {}
			// Do fetch server resource if either cache nor local file is not valid to read
			if (!KKImageManager.networkEnabled) {
				return null;
			}
			response = httpClient.newCall(new Request.Builder().url(url).build()).execute();
			if (!response.isSuccessful()) {
				throw new Exception("Unexpected code " + response);
			}
			final InputStream is = response.body().byteStream();
			headers = response.headers();
			removeInvalidImageFiles();
			if (actionType == KKImageManager.ActionType.DOWNLOAD) {
				RandomAccessFile tempFile = new RandomAccessFile(tempFilePath, "rw");
				while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
					if (interuptFlag) {
						return null;
					}
					if (cipher != null) {
						buffer = cipher.doFinal(buffer);
					}
					tempFile.write(buffer, 0, readLength);
				}
				tempFile.close();
				moveFileTo(tempFilePath, localPath);
				return null;
			} else {
				RandomAccessFile tempFile;
				try {
					tempFile = new RandomAccessFile(tempFilePath, "rw");
				} catch (IOException e) {
					// we don't save to SD card if cache is full
					return BitmapFactory.decodeStream(is);
				}
				try {
					while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
						if (interuptFlag) {
							return null;
						}
						tempFile.write(buffer, 0, readLength);
					}
				} catch (IOException e) {
					tempFile.close();
					return null;
				}
				tempFile.close();
				moveFileTo(tempFilePath, cachePath);
				bitmap = decodeBitmap(cachePath);
				if (bitmap != null) {
					if (saveToLocal && localPath != null) {
						cryptToFile(cachePath, localPath);
					}
					return bitmap;
				}
			}
		} catch (final Exception e) {
			isNetworkError = true;
			removeInvalidImageFiles();
		}
		return null;
	}

	@Override
	public synchronized void onPostExecute(Bitmap bitmap) {
		if (interuptFlag || listener == null) {
			return;
		}
		if (isNetworkError || (actionType != KKImageManager.ActionType.DOWNLOAD && bitmap == null)) {
			listener.onNetworkError(this);
		} else {
			if (onBitmapReceivedListener != null) {
				onBitmapReceivedListener.onBitmapReceived(this, bitmap);
			}
			if (onImageDownloadedListener != null) {
				onImageDownloadedListener.onImageDownloaded(this);
			}
			listener.onComplete(this, bitmap);
		}
	}

	private void removeInvalidImageFiles() {
		removeCacheFile();
		if (localPath != null) {
			File localFile = new File(localPath);
			localFile.delete();
		}
	}

	private void removeCacheFile() {
		File cacheFile = new File(cachePath);
		cacheFile.delete();
	}

	private void moveFileTo(String originalPath, String targetPath) {
		fileLock.lock();
		File targetFile = new File(targetPath);
		targetFile.delete();
		FileChannel originalChannel = null;
		FileChannel targetChannel = null;
		try
		{
			originalChannel = new FileInputStream(originalPath).getChannel();
			targetChannel = new FileOutputStream(targetPath).getChannel();
			originalChannel.transferTo(0, originalChannel.size(), targetChannel);
		} catch (IOException e) {}
		try
		{
			if (originalChannel != null)
				originalChannel.close();
			if (targetChannel != null)
				targetChannel.close();
		} catch (IOException e) {}
		fileLock.unlock();
	}

	private Bitmap decodeBitmap(String path) {
		try {
			File file = new File(path);
			return BitmapFactory.decodeStream(new FileInputStream(file));
		} catch (IOException e) {
			return null;
		}
	}

	private void cryptToFile(String sourceFilePath, String targetFilePath) throws Exception {
		// FIXME: should have two functions: decyptToFile and encryptToFile
		RandomAccessFile sourceFile = new RandomAccessFile(sourceFilePath, "r");
		RandomAccessFile targetFile = new RandomAccessFile(targetFilePath, "rw");
		int readLength;
		do {
			readLength = sourceFile.read(buffer, 0, BUFFER_SIZE);
			if (readLength != -1) {
				if (cipher != null) {
					buffer = cipher.doFinal(buffer);
				}
				targetFile.write(buffer, 0, readLength);
			}
		} while (readLength != -1);
		sourceFile.close();
		targetFile.close();
	}
}
