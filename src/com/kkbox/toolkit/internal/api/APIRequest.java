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
package com.kkbox.toolkit.internal.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.kkbox.toolkit.utils.KKDebug;
import com.kkbox.toolkit.utils.StringUtils;
import com.kkbox.toolkit.utils.UserTask;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.net.ssl.SSLException;

public abstract class APIRequest extends UserTask<Object, Void, Void> {

	public static class Method {
		public static final int GET = 0;
		public static final int POST = 1;
		public static final int PUT = 2;
		public static final int PATCH = 3;
		public static final int DELETE = 4;
	}

	public final static int DEFAULT_RETRY_LIMIT = 3;
	private APIRequestListener listener;
	private String getParams = "";
	private final String url;
	private static OkHttpClient httpClient = new OkHttpClient();
	private boolean isNetworkError = false;
	private boolean isHttpStatusError = false;
	private String errorMessage = "";
	private String cacheFileKey = null;
	private int httpStatusCode = 0;
	private Request.Builder requestBuilder;
	private FormEncodingBuilder requestBodyBuilder;
	private RequestBody requestBody;
	private MultipartBuilder multipartBuilder;
	private File cacheFile = null;
	private Cipher cipher = null;
	private Context context = null;
	private long cacheTimeOut = -1;
	private long cacheLastModify = 0;
	private InputStream is = null;
	private Response response;
	private Call call;
	private int retryLimit = DEFAULT_RETRY_LIMIT;
	private int method;
	private boolean isFromCache = false;

	public APIRequest(String url, Cipher cipher, long cacheTimeOut, Context context) {
		this(url, cipher, 10000, cacheTimeOut, context);
	}

	public APIRequest(String url, Cipher cipher) {
		this(url, cipher, 10000);
	}

	public APIRequest(String url, Cipher cipher, int socketTimeout) {
		httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
		httpClient.setReadTimeout(socketTimeout, TimeUnit.MILLISECONDS);
		requestBuilder = new Request.Builder();
		getParams = TextUtils.isEmpty(Uri.parse(url).getQuery()) ? "" : "?" + Uri.parse(url).getQuery();
		this.url = url.split("\\?")[0];
		this.cipher = cipher;
		this.method = Method.GET;
	}

	public APIRequest(String url, Cipher cipher, int socketTimeout, long cacheTimeOut, Context context) {
		this(url, cipher, socketTimeout);
		this.cacheTimeOut = cacheTimeOut;
		this.context = context;
	}

	public void addGetParam(String key, String value) {
		if (TextUtils.isEmpty(getParams)) {
			getParams = "?";
		} else if (!getParams.endsWith("&")) {
			getParams += "&";
		}
		getParams += key + "=" + value;
	}

	public void addGetParam(String parameter) {
		if (TextUtils.isEmpty(getParams)) {
			getParams = "?";
		} else if (!getParams.endsWith("&")) {
			getParams += "&";
		}
		getParams += parameter;
	}

	public void addPostParam(String key, String value) {
		if (key == null) {
			throw new NullPointerException("addPostParam with null key ");
		}
		if (value == null) {
			value = "";
			KKDebug.e("addPostParam key " + key + " with null value");
		}
		if (requestBodyBuilder == null) {
			requestBodyBuilder = new FormEncodingBuilder();
		}
		requestBodyBuilder.add(key, value);
	}

	// TODO: support multipart request
	public void addHeaderParam(String key, String value) {
		requestBuilder.addHeader(key, value);
	}

	public void addMultiPartPostParam(String key, String fileName, RequestBody requestBody) {
		if (multipartBuilder == null) {
			multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
		}
		multipartBuilder.addFormDataPart(key, fileName, requestBody);
	}

	public void addMultiPartPostParam(String key, String value) {
		if (multipartBuilder == null) {
			multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
		}
		multipartBuilder.addFormDataPart(key, value);
	}

	public void addStringPostParam(String data) {
		MediaType mediaType = MediaType.parse("text/plain");
		requestBody = RequestBody.create(mediaType, data);
	}

	public void addFilePostParam(String path) {
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");
		requestBody = RequestBody.create(mediaType, new File(path));
	}

	public void addByteArrayPostParam(final byte[] data) {
		MediaType mediaType = MediaType.parse("application/octet-stream");
		requestBody = RequestBody.create(mediaType, data);
	}

	public void addJSONPostParam(JSONObject jsonObject) {
		MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
		requestBody = RequestBody.create(mediaType, jsonObject.toString());
	}

	public String getResponseHeader(String key) {
		return response == null ? null : response.header(key);
	}

	public void setRetryCount(int retryLimit) {
		this.retryLimit = retryLimit;
	}

	public void cancel() {
		listener = null;
		// TODO: https://github.com/square/okhttp/issues/1592
		httpClient.getDispatcher().getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				if (call != null) {
					call.cancel();
				}
			}
		});
		this.cancel(true);
	}

	public void setMethod(int method) {
		this.method = method;
	}

	public void setCacheFileKey(String cacheFileKey) {
		this.cacheFileKey = cacheFileKey;
	}

	protected abstract void parseInputStream(InputStream inputStream, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException;

	@Override
	public Void doInBackground(Object... params) {
		int readLength;
		final byte[] buffer = new byte[128];
		listener = (APIRequestListener) params[0];
		int retryTimes = 0;
		ConnectivityManager connectivityManager = null;
		if (context != null) {
			final File cacheDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + "api");
			if (!cacheDir.exists()) {
				cacheDir.mkdir();
			}
			cacheFile = new File(cacheDir.getAbsolutePath() + File.separator + StringUtils.getMd5Hash(url + (cacheFileKey == null ? getParams : cacheFileKey)));
			connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
			cacheLastModify = cacheFile.lastModified();
		}

		if (context != null && cacheTimeOut > 0 && cacheFile.exists()
				&& ((System.currentTimeMillis() - cacheFile.lastModified() < cacheTimeOut)
				|| connectivityManager.getActiveNetworkInfo() == null)) {
			try {
				isFromCache = true;
				parseInputStream(new FileInputStream(cacheFile), cipher);
			} catch (IOException e) {
				isNetworkError = true;
				KKDebug.e(Log.getStackTraceString(e));
			} catch (Exception e) {
				isNetworkError = true;
				KKDebug.e(Log.getStackTraceString(e));
			}
		} else {
			do {
				try {
					KKDebug.i("Connect API url " + url + getParams);
					// TODO: Check multipart and post parameters request
					if (multipartBuilder != null) {
						requestBody = multipartBuilder.build();
					} else if (requestBodyBuilder != null) {
						requestBody = requestBodyBuilder.build();
					}
					if (requestBody != null) {
						if (method == Method.GET) {
							setMethod(Method.POST);
						}
						switch (method) {
							case Method.POST:
								requestBuilder.post(requestBody);
								break;
							case Method.PUT:
								requestBuilder.put(requestBody);
								break;
							case Method.PATCH:
								requestBuilder.patch(requestBody);
								break;
							case Method.DELETE:
								requestBuilder.delete(requestBody);
								break;
						}
					}
					if (TextUtils.isEmpty(getParams)) {
						requestBuilder.url(url);
					} else {
						requestBuilder.url(url + getParams);
					}
					call = httpClient.newCall(requestBuilder.build());
					response = call.execute();
					httpStatusCode = response.code();
					int httpStatusType = httpStatusCode / 100;
					switch (httpStatusType) {
						case 2:
							is = response.body().byteStream();
							isNetworkError = false;
							break;
						case 4:
							KKDebug.w("Get client error " + httpStatusCode + " with connection : " + url + getParams);
							is = response.body().byteStream();
							isHttpStatusError = true;
							isNetworkError = false;
							break;
						case 5:
							KKDebug.w("Get server error " + httpStatusCode + " with connection : " + url + getParams);
							is = response.body().byteStream();
							isHttpStatusError = true;
							isNetworkError = false;
							break;
						default:
							KKDebug.w("connection to " + url + getParams + " returns " + httpStatusCode);
							retryTimes++;
							isNetworkError = true;
							SystemClock.sleep(1000);
							break;
					}
				} catch (final SSLException e) {
					KKDebug.w("connection to " + url + getParams + " failed with " + e.getClass().getName());
					isNetworkError = true;
					errorMessage = e.getClass().getName();
					return null;
				} catch (final Exception e) {
					KKDebug.w("connection to " + url + getParams + " failed!");
					retryTimes++;
					isNetworkError = true;
					SystemClock.sleep(1000);
				}
			} while (isNetworkError && retryTimes < retryLimit);

			try {
				if (!isNetworkError && !isHttpStatusError && listener != null) {
					if (cacheTimeOut > 0) {
						FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
						while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
							fileOutputStream.write(buffer, 0, readLength);
						}
						fileOutputStream.close();
						cacheLastModify = new Date().getTime();
						parseInputStream(new FileInputStream(cacheFile), cipher);
					} else {
						parseInputStream(is, cipher);
					}
				} else {
					deleteCacheIfExist();
					if (isHttpStatusError) {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
							byteArrayOutputStream.write(buffer, 0, readLength);
						}
						byteArrayOutputStream.flush();
						if (cipher != null && ((httpStatusCode / 100) != 5)) {
							errorMessage = new String(cipher.doFinal(byteArrayOutputStream.toByteArray()));
						} else {
							errorMessage = byteArrayOutputStream.toString();
						}
					}
				}
			} catch (IOException e) {
				isNetworkError = true;
				KKDebug.e(Log.getStackTraceString(e));
			} catch (Exception e) {
				isNetworkError = true;
				KKDebug.e(Log.getStackTraceString(e));
			}
		}
		return null;
	}

	public void deleteCacheIfExist() {
		if(cacheTimeOut > 0 && cacheFile != null && cacheFile.delete()){
			KKDebug.e("Remove cached file success when request error.");
		}
	}

	public boolean isFromCache() {
		return isFromCache;
	}

	public long getCachedTime() {
		return cacheLastModify;
	}

	public void onPostExecute(Void v) {
		if (isHttpStatusError) {
			if (listener != null) {
				listener.onHttpStatusError(httpStatusCode, errorMessage);
			}
		} else if (isNetworkError) {
			if (listener != null) {
				listener.onNetworkError(errorMessage);
			}
		} else {
			if (listener != null) {
				listener.onComplete();
			}
		}
	}
}
