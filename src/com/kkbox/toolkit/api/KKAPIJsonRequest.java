package com.kkbox.toolkit.api;

import android.content.Context;

import com.kkbox.toolkit.internal.api.KKAPIJsonRequestListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class KKAPIJsonRequest extends APIRequest {

	private KKAPIJsonRequestListener jsonRequestListener;

	public KKAPIJsonRequest(String url, Cipher cipher, long reloadPeriod, Context context) {
		super(url, cipher, reloadPeriod, context);
	}

	public KKAPIJsonRequest(String url, Cipher cipher) {
		super(url, cipher);
	}

	public KKAPIJsonRequest(String url, Cipher cipher, int socketTimeout) {
		super(url, cipher, socketTimeout);
	}

	@Override
	public Void doInBackground(Object... params) {
		jsonRequestListener = (KKAPIJsonRequestListener) params[0];
		return super.doInBackground(params);
	}

	@Override
	protected void readDataFromInputStream(ByteArrayOutputStream data) throws IOException {}

	@Override
	protected void preCompleteAndCachedAPI(ByteArrayOutputStream data, File cacheFile) throws BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
		jsonRequestListener.onStreamPreComplete(is);
		if (context != null) {
			try {
				int readLength;
				byte[] buffer = new byte[128];
				while ((readLength = is.read(buffer, 0, buffer.length)) != -1) {
					data.write(buffer, 0, readLength);
				}
				data.flush();
				FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
				outputStreamWriter.write(data.toString());
				outputStreamWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
