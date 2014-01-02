package com.kkbox.toolkit.api;

import android.os.Build;
import android.util.JsonReader;

import com.kkbox.toolkit.internal.api.KKAPIJsonRequestListener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public abstract class KKAPIJsonBase extends KKAPIBase {

	private KKAPIJsonRequestListener apiJsonRequestListener = new KKAPIJsonRequestListener() {

		@Override
		public void onStreamPreComplete(InputStream inputStream) throws UnsupportedEncodingException {
			if (Build.VERSION.SDK_INT >= 11) {
				JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
				parse(reader);
			}
		}

		@Override
		public void onComplete() {
			apiRequestListener.onComplete();
		}

		@Override
		public void onPreComplete(String data) {}

		@Override
		public void onHttpStatusError(int statusCode) {
			apiRequestListener.onHttpStatusError(statusCode);
		}

		@Override
		public void onNetworkError() {
			apiRequestListener.onNetworkError();
		}
	};

	public KKAPIJsonBase() {
		requestListener = apiJsonRequestListener;
	}

	protected abstract int parse(JsonReader reader);
}
