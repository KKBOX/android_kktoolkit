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
 * KKAPIBase is a abstract class 
 */
package com.kkbox.toolkit.api;

import com.kkbox.toolkit.internal.api.KKAPIRequestListener;
import com.kkbox.toolkit.utils.KKDebug;

public abstract class KKAPIBase {
	public static class ErrorCode {
		public static final int NO_ERROR = 0;
		public static final int NETWORK_NOT_AVAILABLE = -101;
		public static final int UNKNOWN_SERVER_ERROR = -102;
		public static final int INVALID_API_FORMAT = -103;
	}

	private KKAPIRequest request;
	private KKAPIListener apiListener;

	private int errorCode;
	private boolean isRunning = false;
	private boolean isResponseSilent = false;

	private KKAPIRequestListener apiRequestListener = new KKAPIRequestListener() {
		@Override
		public void onComplete() {
			if (errorCode == ErrorCode.NO_ERROR) {
				onAPIComplete();
			} else if (!isResponseSilent) {
				onAPIError(errorCode);
			}
			isRunning = false;
		}

		@Override
		public void onNetworkError() {
			if (!isResponseSilent) {
				onAPINetworkError();
			}
			isRunning = false;
		}

		@Override
		public void onPreComplete(String data) {
			errorCode = parse(data);
		}

		@Override
		public void onHttpStatusError(int statusCode) {
			if (!isResponseSilent) {
				onAPIHttpStatusError(statusCode);
			}
			isRunning = false;
		}
	};

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

	protected abstract int parse(String data);

	protected void execute(KKAPIRequest request) {
		this.request = request;
		isRunning = true;
		request.execute(apiRequestListener);
	}
}
