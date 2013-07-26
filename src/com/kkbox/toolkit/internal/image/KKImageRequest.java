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
import android.util.Log;
import android.view.View;

import com.kkbox.toolkit.image.KKImageListener;
import com.kkbox.toolkit.image.KKImageManager;
import com.kkbox.toolkit.utils.KKDebug;
import com.kkbox.toolkit.utils.UserTask;

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

public class KKImageRequest extends UserTask<Object, Integer, Bitmap> {
	private HttpClient httpclient;
	private KKImageRequestListener listener;
	private HttpResponse response;
	private Context context;
	private View view;
	private KKImageListener imageCacheListener;
	private String url = "";
	private String localPath;
	private int actionType;
	private boolean isNetworkError = false;
	private Cipher cipher = null;

	public KKImageRequest(Context context, String url, String localPath, View view, boolean updateBackground, Cipher cipher) {
		this.view = view;
		if (updateBackground) {
			actionType = KKImageManager.ActionType.UPDATE_VIEW_BACKGROUND;
		} else {
			actionType = KKImageManager.ActionType.UPDATE_VIEW_SOURCE;
		}
		init(context, url, localPath, cipher);
	}

	public KKImageRequest(Context context, String url, String localPath, Cipher cipher) {
		actionType = KKImageManager.ActionType.DOWNLOAD;
		init(context, url, localPath, cipher);
	}

	public KKImageRequest(Context context, String url, String localPath, KKImageListener imageCacheListener, Cipher cipher) {
		this.imageCacheListener = imageCacheListener;
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
		return imageCacheListener;
	}

	public int getActionType() {
		return actionType;
	}

	@Override
	public Bitmap doInBackground(Object... params) {
		listener = (KKImageRequestListener)params[0];
		Bitmap bitmap;
		final int BUFFER_SIZE = 1024;
		try {
			int readLength;
			byte[] buffer = new byte[BUFFER_SIZE];
			// TODO: use fileOutoutStream instead
			RandomAccessFile localRandomAccessFile = null;
			RandomAccessFile cacheRandomAccessFile = null;
			String cachePath = KKImageManager.getTempImagePath(context, url);
			File cacheFile = new File(cachePath);
			File localFile = null;
			if (localPath != null) {
				localFile = new File(localPath);
			}
			try {
				if (cacheFile.exists()) {
					if (actionType == KKImageManager.ActionType.DOWNLOAD) {
						if (localFile == null || !localFile.exists()) {
							cacheRandomAccessFile = new RandomAccessFile(cachePath, "r");
							localRandomAccessFile = new RandomAccessFile(localPath, "rw");
							do {
								readLength = cacheRandomAccessFile.read(buffer, 0, BUFFER_SIZE);
								if (readLength != -1) {
									if (cipher != null) {
										buffer = cipher.doFinal(buffer);
									}
									localRandomAccessFile.write(buffer, 0, readLength);
								}
							} while (readLength != -1);
							cacheRandomAccessFile.close();
							localRandomAccessFile.close();
							return null;
						}
					} else {
						bitmap = BitmapFactory.decodeFile(cachePath);
						if (bitmap != null) {
							return bitmap;
						}
					}
				} else if (localFile != null && localFile.exists()) {
					if (actionType == KKImageManager.ActionType.DOWNLOAD) {
						return null;
					} else {
						cacheRandomAccessFile = new RandomAccessFile(cachePath, "rw");
						localRandomAccessFile = new RandomAccessFile(localPath, "r");
						do {
							readLength = localRandomAccessFile.read(buffer, 0, BUFFER_SIZE);
							if (readLength != -1) {
								if (cipher != null) {
									buffer = cipher.doFinal(buffer);
								}
								cacheRandomAccessFile.write(buffer, 0, readLength);
							}
						} while (readLength != -1);
						cacheRandomAccessFile.close();
						localRandomAccessFile.close();
						bitmap = BitmapFactory.decodeFile(cachePath);
						if (bitmap != null) {
							return bitmap;
						}
					}
				}
			} catch (Exception e) {}
			final HttpGet httpget = new HttpGet(url);
			response = httpclient.execute(httpget);
			final InputStream is = response.getEntity().getContent();
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
					return BitmapFactory.decodeStream(is);
				}
				while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
					try {
						cacheRandomAccessFile.write(buffer, 0, readLength);
					} catch (IOException e) {
						cacheRandomAccessFile.close();
						File file = new File(cachePath);
						file.delete();
						return null;
					}
				}
				cacheRandomAccessFile.close();
				return BitmapFactory.decodeFile(cachePath);
			}
		} catch (final Exception e) {
			KKDebug.w("connetion to " + url + " failed! " + Log.getStackTraceString(e));
			isNetworkError = true;
		}
		return null;
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
}
