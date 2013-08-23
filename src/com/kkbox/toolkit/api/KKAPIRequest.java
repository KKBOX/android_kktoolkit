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
 * KKAPIRequest
 */
package com.kkbox.toolkit.api;

import android.os.SystemClock;

import com.kkbox.toolkit.internal.api.KKAPIRequestListener;
import com.kkbox.toolkit.utils.KKDebug;
import com.kkbox.toolkit.utils.UserTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.crypto.Cipher;

public class KKAPIRequest extends UserTask<Object, Void, Void> {
	private KKAPIRequestListener listener;
	private String getParams = "";
	private final String url;
	private HttpClient httpclient;
	private boolean isNetworkError = false;
	private boolean isHttpStatusError = false;
	private int httpStatusCode = 0;
	private ArrayList<NameValuePair> postParams;
	private MultipartEntity multipartEntity;
	private StringEntity stringEntity;
	private FileEntity fileEntity;
	private ByteArrayEntity byteArrayEntity;
	private Cipher cipher = null;

	public KKAPIRequest(String url, Cipher cipher) {
		this(url, cipher, 10000);
	}

	public KKAPIRequest(String url, Cipher cipher, int socketTimeout) {
		BasicHttpParams params = new BasicHttpParams();
		params.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);
		params.setIntParameter(HttpConnectionParams.SO_TIMEOUT, socketTimeout);
		httpclient = new DefaultHttpClient(params);
		this.url = url;
		this.cipher = cipher;
	}
	
	public void addGetParam(String key, String value) {
		if (getParams == "") {
			getParams = "?";
		} else {
			getParams += "&";
		}
		getParams += key + "=" + value;
	}

	public void addGetParam(String parameter) {
		if (getParams == "") {
			getParams = "?";
		} else {
			getParams += "&";
		}
		getParams += parameter;
	}

	public void addPostParam(String key, String value) {
		if (postParams == null) {
			postParams = new ArrayList<NameValuePair>();
		}
		postParams.add((new BasicNameValuePair(key, value)));
	}

	public void addMultiPartPostParam(String key, ContentBody contentBody) {
		if (multipartEntity == null) {
			multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		}
		multipartEntity.addPart(key, contentBody);
	}

	public void addStringPostParam(String data) {
		try {
			stringEntity = new StringEntity(data, HTTP.UTF_8);
		} catch (Exception e) {};
	}

	public void addFilePostParam(String path) {
		fileEntity = new FileEntity(new File(path), URLEncodedUtils.CONTENT_TYPE + HTTP.CHARSET_PARAM + HTTP.UTF_8);
	}

	public void addByteArrayPostParam(byte[] data) {
		byteArrayEntity = new ByteArrayEntity(data);
		byteArrayEntity.setContentType("application/octet-stream");
	}

	public void cancel() {
		listener = null;
		this.cancel(true);
	}

	@Override
	public Void doInBackground(Object... params) {
		int readLength;
		final ByteArrayOutputStream data = new ByteArrayOutputStream();
		final byte[] buffer = new byte[128];
		listener = (KKAPIRequestListener)params[0];
		int retryTimes = 0;
		do {
			try {
				HttpResponse response;
				if (postParams != null || multipartEntity != null || stringEntity != null || fileEntity != null || byteArrayEntity != null) {
					final HttpPost httppost = new HttpPost(url + getParams);
					if (postParams != null) {
						httppost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
					}
					if (multipartEntity != null) {
						httppost.setEntity(multipartEntity);
					}
					if (stringEntity != null) {
						httppost.setEntity(stringEntity);
					}
					if (fileEntity != null) {
						httppost.setEntity(fileEntity);
					}
					if (byteArrayEntity != null) {
						httppost.setEntity(byteArrayEntity);
					}
					response = httpclient.execute(httppost);
				} else {
					response = httpclient.execute(new HttpGet(url + getParams));
				}
				httpStatusCode = response.getStatusLine().getStatusCode();
				switch (httpStatusCode) {
					case 200:
						final InputStream is = response.getEntity().getContent();
						while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
							data.write(buffer, 0, readLength);
						}
						data.flush();
						isNetworkError = false;
						break;
					case 404:
					case 403:
					case 400:
						isHttpStatusError = true;
						isNetworkError = false;
						break;
					default:
						KKDebug.w("connetion to " + url + getParams + " returns " + httpStatusCode);
						retryTimes++;
						isNetworkError = true;
						SystemClock.sleep(1000);
						break;
				}
			} catch (final IOException e) {
				KKDebug.w("connetion to " + url + getParams + " failed!");
				retryTimes++;
				isNetworkError = true;
				SystemClock.sleep(1000);
			}
		} while (isNetworkError && retryTimes < 3);
		try {
			if (!isNetworkError && !isHttpStatusError) {
				if (listener != null) {
					if (cipher != null) {
						listener.onPreComplete(new String(cipher.doFinal(data.toByteArray())));
					} else {
						listener.onPreComplete(data.toString());
					}
				}
			}
		} catch (Exception e) {};
		return null;
	}

	@Override
	public void onPostExecute(Void v) {
		if (listener == null) { return; }
		if (isHttpStatusError) {
			listener.onHttpStatusError(httpStatusCode);
		} else if (isNetworkError) {
			listener.onNetworkError();
		} else {
			listener.onComplete();
		}
	}
}
