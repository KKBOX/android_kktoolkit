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
 * KKEventQueue
 */
package com.kkbox.toolkit.utils;

import java.util.ArrayList;

public class KKEventQueue {
	public static class ThreadType {
		public static final int CALLER_THREAD = 0;
		public static final int NEW_THREAD = 1;
	}

	private class KKEvent {
		public Runnable runnable;
		public int threadType = ThreadType.CALLER_THREAD;
		public Runnable postEventRunnable;
	}

	private final ArrayList<KKEvent> queue = new ArrayList<KKEvent>();
	private KKEventQueueListener listener;
	private boolean isRunning = false;
	private ArrayList<Integer> threadUnlockId = new ArrayList<Integer>();
	private boolean threadUnlockFlag = false;
	private final Object threadLock = new Object();

	public void add(Runnable runnable, int threadType) {
		final KKEvent newEvent = new KKEvent();
		newEvent.runnable = runnable;
		newEvent.threadType = threadType;
		queue.add(newEvent);
	}

	public void addNewThreadEvent(Runnable newThreadRunnable, Runnable postEventRunnable) {
		final KKEvent newEvent = new KKEvent();
		newEvent.runnable = newThreadRunnable;
		newEvent.threadType = ThreadType.NEW_THREAD;
		newEvent.postEventRunnable = postEventRunnable;
		queue.add(newEvent);
	}

	public void addCallerThreadEventWithLock(Runnable callerThreadRunnable, final int lockId) {
		add(callerThreadRunnable, ThreadType.CALLER_THREAD);
		add(new Runnable() {
			@Override
			public void run() {
				while (!threadUnlockId.contains(lockId) && !threadUnlockFlag) {
					try {
						synchronized (threadLock) {
							threadLock.wait();
						}
					} catch (final InterruptedException e) {}
				}
				threadUnlockId.clear();
			}
		}, ThreadType.NEW_THREAD);
	}

	public void unlockEvent(int lockId) {
		synchronized (threadLock) {
			threadUnlockId.add(lockId);
			threadLock.notifyAll();
		}
	}

	public void unlockAllEvents() {
		synchronized (threadLock) {
			threadUnlockFlag = true;
			threadLock.notifyAll();
		}
	}

	public void clearPendingEvents() {
		if (!isRunning) {
			queue.clear();
		} else {
			while (queue.size() > 1) {
				queue.remove(1);
			}
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public synchronized void start() {
		if (!isRunning) {
			run();
		}
	}

	public void setListener(KKEventQueueListener listener) {
		this.listener = listener;
	}

	private void run() {
		if (queue.size() > 0) {
			isRunning = true;
		} else {
			isRunning = false;
			threadUnlockFlag = false;
			threadUnlockId.clear();
			if (listener != null) {
				listener.onQueueCompleted();
			}
			return;
		}
		final KKEvent event = queue.get(0);
		if (event.threadType == ThreadType.NEW_THREAD) {
			new UserTask<Void, Void, Void>() {
				@Override
				public Void doInBackground(Void... params) {
					event.runnable.run();
					return null;
				}

				@Override
				public void onPostExecute(Void v) {
					if (event.postEventRunnable != null) {
						event.postEventRunnable.run();
					}
					queue.remove(event);
					run();
				}
			}.execute();
		} else if (event.threadType == ThreadType.CALLER_THREAD) {
			event.runnable.run();
			queue.remove(event);
			run();
		}
	}
}
