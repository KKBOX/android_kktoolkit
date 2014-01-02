package com.kkbox.toolkit.internal.api;

public abstract interface APIRequestListener {

	abstract public void onComplete();

	abstract public void onHttpStatusError(int statusCode);

	abstract public void onNetworkError();
}
