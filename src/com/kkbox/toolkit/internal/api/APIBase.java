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
import android.net.NetworkInfo;

import com.kkbox.toolkit.api.KKAPIListener;
import com.kkbox.toolkit.utils.KKDebug;

public abstract class APIBase {

	public static class ErrorCode {
		public static final int NO_ERROR = 0;
		public static final int NETWORK_NOT_AVAILABLE = -101;
		public static final int UNKNOWN_SERVER_ERROR = -102;
		public static final int INVALID_API_FORMAT = -103;
	}

	private APIRequest request;
	private KKAPIListener apiListener;

	protected int errorCode;
	protected boolean isRunning = false;
	protected boolean isResponseSilent = false;

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		} else {
			return false;
		}
	}

	public void cancel() {
		if (request != null) {
			request.cancel();
		}
	}

	public void setResponseSilent(boolean isResponseSilent) {
		this.isResponseSilent = isResponseSilent;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setAPIListener(KKAPIListener listener) {
		apiListener = listener;
	}

	protected void onAPINetworkError() {
		KKDebug.i(getClass().getSimpleName() + " completed with network error");
		if (apiListener != null) {
			apiListener.onAPIError(ErrorCode.NETWORK_NOT_AVAILABLE);
		}
	}

	protected void onAPIError(int errorCode) {
		KKDebug.i(getClass().getSimpleName() + " completed with errorCode: " + errorCode);
		if (apiListener != null) {
			apiListener.onAPIError(errorCode);
		}
	}

	protected void onAPIComplete() {
		KKDebug.i(getClass().getSimpleName() + " completed");
		if (apiListener != null) {
			apiListener.onAPIComplete();
		}
	}

	protected void onAPIHttpStatusError(int statusCode) {
		onAPIError(ErrorCode.UNKNOWN_SERVER_ERROR);
	}

	protected void execute(APIRequest request) {
		this.request = request;
		isRunning = true;
		request.execute(getRequestListener());
	}

	protected abstract APIRequestListener getRequestListener();
}
