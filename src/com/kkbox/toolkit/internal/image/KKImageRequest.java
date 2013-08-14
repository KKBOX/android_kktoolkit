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
 * KKImageRequest
 */
package com.kkbox.toolkit.internal.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.kkbox.toolkit.image.KKImageListener;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.image.KKImageOnReceiveHttpHeaderListener;
import com.kkbox.toolkit.utils.UserTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.crypto.Cipher;

public class KKImageRequest extends UserTask<Object, Header[], Bitmap> {
	private final int BUFFER_SIZE = 1024;
	private byte[] buffer = new byte[BUFFER_SIZE];
	private HttpClient httpclient;
	private KKImageRequestListener listener;
	private HttpResponse response;
	private Context context;
	private View view;
	private KKImageListener imageListener;
	private KKImageOnReceiveHttpHeaderListener onReceiveHttpHeaderListener;
	private String url = "";
	private String localPath;
	private String cachePath;
	private int actionType;
	private boolean isNetworkError = false;
	private Cipher cipher = null;
	private boolean saveToLocal = false;

	public KKImageRequest(Context context, String url, String localPath, KKImageOnReceiveHttpHeaderListener onReceiveHttpHeaderListener,
			View view, boolean updateBackground, Cipher cipher, boolean saveToLocal) {
		this.view = view;
		this.saveToLocal = saveToLocal;
		if (updateBackground) {
			actionType = KKImageManager.ActionType.UPDATE_VIEW_BACKGROUND;
		} else {
			actionType = KKImageManager.ActionType.UPDATE_VIEW_SOURCE;
		}
		this.onReceiveHttpHeaderListener = onReceiveHttpHeaderListener;
		init(context, url, localPath, cipher);
	}

	public KKImageRequest(Context context, String url, String localPath, KKImageOnReceiveHttpHeaderListener onReceiveHttpHeaderListener,
			Cipher cipher) {
		actionType = KKImageManager.ActionType.DOWNLOAD;
		this.onReceiveHttpHeaderListener = onReceiveHttpHeaderListener;
		init(context, url, localPath, cipher);
	}

	public KKImageRequest(Context context, String url, String localPath, KKImageListener imageListener, Cipher cipher) {
		this.imageListener = imageListener;
		actionType = KKImageManager.ActionType.CALL_LISTENER;
		init(context, url, localPath, cipher);
	}

	private void init(Context context, String url, String localPath, Cipher cipher) {
		BasicHttpParams params = new BasicHttpParams();
		params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
		params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, 10000);
		httpclient = new DefaultHttpClient(params);
		this.url = url;
		this.localPath = localPath;
		this.context = context;
		this.cipher = cipher;
	}

	public void cancel() {
		listener = null;
		this.cancel(true);
	}

	public String getUrl() {
		return url;
	}

	public View getView() {
		return view;
	}

	public KKImageListener getImageCacheListener() {
		return imageListener;
	}

	public int getActionType() {
		return actionType;
	}

	@Override
	public Bitmap doInBackground(Object... params) {
		listener = (KKImageRequestListener)params[0];
		Bitmap bitmap;

		try {
			int readLength;
			// TODO: use fileOutoutStream instead
			RandomAccessFile localRandomAccessFile = null;
			RandomAccessFile cacheRandomAccessFile = null;
			cachePath = KKImageManager.getTempImagePath(context, url);
			File cacheFile = new File(cachePath);
			File localFile = null;
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
						bitmap = BitmapFactory.decodeFile(cachePath);
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
						cryptToFile(localPath, cachePath);
						bitmap = BitmapFactory.decodeFile(cachePath);
						if (bitmap != null) {
							return bitmap;
						} else {
							removeCacheFile();
						}
					}
				}
			} catch (Exception e) {
				removeInvalidImageFiles();
			}
			// Do fetch server resource if either cache nor local file is not valid to read
			final HttpGet httpget = new HttpGet(url);
			response = httpclient.execute(httpget);
			final InputStream is = response.getEntity().getContent();
			publishProgress(response.getAllHeaders());
			if (actionType == KKImageManager.ActionType.DOWNLOAD) {
				localRandomAccessFile = new RandomAccessFile(localPath, "rw");
				while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
					if (cipher != null) {
						buffer = cipher.doFinal(buffer);
					}
					localRandomAccessFile.write(buffer, 0, readLength);
				}
				localRandomAccessFile.close();
				return null;
			} else {
				try {
					cacheRandomAccessFile = new RandomAccessFile(cachePath, "rw");
				} catch (IOException e) {
					// we don't save to SD card if cache is full
					return BitmapFactory.decodeStream(is);
				}
				while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
					try {
						cacheRandomAccessFile.write(buffer, 0, readLength);
					} catch (IOException e) {
						cacheRandomAccessFile.close();
						removeCacheFile();
						return null;
					}
				}
				cacheRandomAccessFile.close();
				bitmap = BitmapFactory.decodeFile(cachePath);
				if (bitmap != null) {
					if (saveToLocal && localPath != null) {
						cryptToFile(cachePath, localPath);
					}
					return bitmap;
				} else {
					removeCacheFile();
				}
			}
		} catch (final Exception e) {
			isNetworkError = true;
			removeInvalidImageFiles();
		}
		return null;
	}

	@Override
	public void onProgressUpdate(Header[]... data) {
		if (onReceiveHttpHeaderListener != null) {
			onReceiveHttpHeaderListener.onReceiveHttpHeader(data[0]);
		}
	}

	@Override
	public void onPostExecute(Bitmap bitmap) {
		if (listener == null) { return; }
		if (isNetworkError || (actionType != KKImageManager.ActionType.DOWNLOAD && bitmap == null)) {
			listener.onNetworkError(this);
		} else {
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
