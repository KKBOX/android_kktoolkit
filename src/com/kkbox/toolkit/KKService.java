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
package com.kkbox.toolkit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.kkbox.toolkit.dialog.KKDialogManager;
import com.kkbox.toolkit.utils.KKEventQueue;
import com.kkbox.toolkit.utils.KKEventQueueListener;

public abstract class KKService extends Service {
	private static KKDialogManager dialogNotificationManager;
	private static KKServiceListener listener;
	private static boolean isRunning = false;
	private KKEventQueue eventQueue = new KKEventQueue();
	private static int runningFlag = -1;

	private final KKEventQueueListener eventQueuelistener = new KKEventQueueListener() {
		@Override
		public void onQueueCompleted() {
			isRunning = true;
			if (listener != null) {
				listener.onRunning(runningFlag);
			}
			onPostCreate(runningFlag);
		}
	};

	public static void attachListener(KKServiceListener serviceListener) {
		listener = serviceListener;
		if (isRunning) {
			listener.onRunning(runningFlag);
		}
	}

	public static KKDialogManager getDialogNotificationManager() {
		return dialogNotificationManager;
	}

	public static void notifyProgress(int flag) {
		if (listener != null) {
			listener.onProgress(flag);
		}
	}
	
	public static boolean isRunning() {
		return isRunning;
	}

	public static void setRunningFlag(int flag) {
		runningFlag = flag;
	}

	protected abstract void initServiceComponent(int runningFlag, KKEventQueue eventQueue);
	
	protected abstract void onPostCreate(int flag);

	@Override
	public IBinder onBind(Intent i) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dialogNotificationManager = new KKDialogManager();
		initServiceComponent(runningFlag, eventQueue);
		eventQueue.setListener(eventQueuelistener);
		eventQueue.start();
	}

	public void reInitServiceComponent() {
		initServiceComponent(runningFlag, eventQueue);
		eventQueue.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
}
