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
 * KKService is an abstract class that runs a service.
 * You will need to implement initServiceComponent() by yourself.
 */

package com.kkbox.toolkit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kkbox.toolkit.dialog.KKDialogManager;

public abstract class KKService extends Service {
	private static KKDialogManager dialogNotificationManager;
	private static KKServiceListener listener;
	private static boolean isRunning = false;

	public static void attachListener(KKServiceListener serviceListener) {
		listener = serviceListener;
		if (isRunning) {
			listener.onStarted();
		}
	}
	
	public static KKDialogManager getDialogNotificationManager() {
		return dialogNotificationManager;
	}

	public static boolean isRunning() {
		return isRunning;
	}

	protected abstract void initServiceComponent();

	@Override
	public IBinder onBind(Intent i) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dialogNotificationManager = new KKDialogManager();
		initServiceComponent();
		isRunning = true;
		if (listener != null) {
			listener.onStarted();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
}
