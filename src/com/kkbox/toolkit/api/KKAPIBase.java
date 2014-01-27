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
package com.kkbox.toolkit.api;

import com.kkbox.toolkit.internal.api.APIBase;
import com.kkbox.toolkit.internal.api.APIRequestListener;
import com.kkbox.toolkit.internal.api.KKAPIRequestListener;


public abstract class KKAPIBase extends APIBase {

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
		public void onHttpStatusError(int statusCode) {
			if (!isResponseSilent) {
				onAPIHttpStatusError(statusCode);
			}
			isRunning = false;
		}

		@Override
		public void onHttpStatusError(int statusCode, String content) {
			if (!isResponseSilent) {
				onAPIHttpStatusError(statusCode, content);
			}
			isRunning = false;
		}

		@Override
		public void onPreComplete(String data) {
			errorCode = parse(data);
		}
	};

	@Override
	protected APIRequestListener getRequestListener() {
		return apiRequestListener;
	}

	protected abstract int parse(String data);
}
