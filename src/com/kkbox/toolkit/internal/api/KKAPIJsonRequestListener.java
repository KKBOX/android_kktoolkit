package com.kkbox.toolkit.internal.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public interface KKAPIJsonRequestListener extends APIRequestListener {

	abstract public void onStreamPreComplete(InputStream inputStream) throws UnsupportedEncodingException;
}
