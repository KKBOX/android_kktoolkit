package com.kkbox.toolkit.api;

import android.os.Build;
import android.util.JsonReader;

import com.kkbox.toolkit.internal.api.KKAPIJsonRequestListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public abstract class KKAPIJsonBase extends APIBase {

	private KKAPIJsonRequestListener apiJsonRequestListener = new KKAPIJsonRequestListener() {
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
		public void onStreamPreComplete(InputStream inputStream) throws UnsupportedEncodingException {
			if (Build.VERSION.SDK_INT >= 11) {
				JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
				errorCode = parse(reader);
			}
		}
	};

	public KKAPIJsonBase() {
		requestListener = apiJsonRequestListener;
	}

	protected abstract int parse(JsonReader reader);
}
