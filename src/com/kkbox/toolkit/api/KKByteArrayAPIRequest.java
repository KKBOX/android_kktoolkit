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

import android.content.Context;

import com.kkbox.toolkit.internal.api.APIRequest;
import com.kkbox.toolkit.internal.api.KKByteArrayAPIRequestListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class KKByteArrayAPIRequest extends APIRequest {

	KKByteArrayAPIRequestListener requestListener;

	public KKByteArrayAPIRequest(String url, Cipher cipher, long reloadPeriod, Context context) {
		super(url, cipher, reloadPeriod, context);
	}

	public KKByteArrayAPIRequest(String url, Cipher cipher) {
		super(url, cipher);
	}

	public KKByteArrayAPIRequest(String url, Cipher cipher, int socketTimeout) {
		super(url, cipher, socketTimeout);
	}

	public KKByteArrayAPIRequest(String url, Cipher cipher, int socketTimeout, long cacheTimeOut, Context context) {
		super(url, cipher, socketTimeout, cacheTimeOut, context);
	}

	@Override
	public Void doInBackground(Object... params) {
		requestListener = (KKByteArrayAPIRequestListener) params[0];
		return super.doInBackground(params);
	}

	@Override
	protected void parseInputStream(InputStream inputStream, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int readLength;
		byte[] buffer = new byte[128];
		while ((readLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
			byteArrayOutputStream.write(buffer, 0, readLength);
		}
		byteArrayOutputStream.flush();
		byte[] jsonData;
		if (cipher != null) {
			jsonData = cipher.doFinal(byteArrayOutputStream.toByteArray());
		} else {
			jsonData = byteArrayOutputStream.toByteArray();
		}
		requestListener.onPreComplete(jsonData);
	}
}
