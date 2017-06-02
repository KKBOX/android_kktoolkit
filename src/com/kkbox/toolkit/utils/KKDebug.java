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
package com.kkbox.toolkit.utils;

import android.os.Debug;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class KKDebug {
	private static boolean debugEnabled;
	private static String debugLogPath;
	private static DataOutputStream logStream;
	private static long runningTimestamp = 0;

	public static void setEnablePrintLog(boolean enabled) {
		debugEnabled = enabled;
	}

	public static void setDebugLogPath(String path) {
		debugLogPath = path;
	}

	public static boolean enabledPrintLog() {
		return debugEnabled;
	}

	public static void i(Object msg) {
		if (debugEnabled) {
			Log.i("KKBOX", "" + msg);
			writeLog("" + msg);
		}
	}

	public static void i(String tag, Object msg) {
		if (debugEnabled) {
			Log.i(tag, "" + msg);
			writeLog("" + msg);
		}
	}

	public static void e(Object msg) {
		if (debugEnabled) {
			Log.e("KKBOX", getClassLineNumber() + msg);
			writeLog("" + msg);
		}
	}

	public static void e(String tag, Object msg) {
		if (debugEnabled) {
			Log.e(tag, getClassLineNumber() + msg);
			writeLog("" + msg);
		}
	}

	public static void w(Object msg) {
		if (debugEnabled) {
			Log.w("KKBOX", "" + msg);
			writeLog("" + msg);
		}
	}

	public static void w(String tag, Object msg) {
		if (debugEnabled) {
			Log.w(tag, "" + msg);
			writeLog("" + msg);
		}
	}
	
	public static void wtf(Object msg) {
		if (debugEnabled) {
			Log.wtf("KKBOX", "" + msg);
			writeLog("" + msg);
		}
	}
	
	public static void startToMeasureRunningTime() {
		runningTimestamp = System.currentTimeMillis();
	}
	
	public static long printRunningTime(Object msg) {
		long timeLength = System.currentTimeMillis() - runningTimestamp;
		KKDebug.i(msg + " running time: " + timeLength);
		return timeLength;
	}

	private static void writeLog(String msg) {
		if (debugLogPath == null) { return; }
		try {
			if (logStream == null) {
				final Calendar calendar = Calendar.getInstance();
				final String dateTime = String.format("%04d%02d%02d_%02d%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
								calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
				final String logTimeFolderPath = debugLogPath + File.separator + dateTime;
				final File logTimeFolder = new File(logTimeFolderPath);
				if (!logTimeFolder.exists()) {
					if (!logTimeFolder.mkdir()) { return; }
				}
				final File logFile = new File(logTimeFolderPath + File.separator + "kkbox.log");
				if (!logFile.exists()) {
					logFile.createNewFile();
				}
				logStream = new DataOutputStream(new FileOutputStream(logFile));
			}
			logStream.write(String.format("[%s]\t%s\n", DateFormat.getDateTimeInstance().format(new Date()), msg).getBytes());
		} catch (Exception e) {
			Log.e("KKBOX", Log.getStackTraceString(e));
		}
	}

	private static String getClassLineNumber() {
		try {
			StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
			if (stackTraceElements == null || stackTraceElements.length == 0) { return ""; }
			int deep = 0;
			for (int i = 0; i < stackTraceElements.length; i++) {
				String className = stackTraceElements[i].getClassName();
				if (className.indexOf("KKDebug") >= 0) {
					deep++;
				}
				if (deep == 2) { return "[" + stackTraceElements[i + 1].getFileName() + " line:"
						+ stackTraceElements[i + 1].getLineNumber() + "] "; }
			}
			return "";
		} catch (Exception e) {
			Log.i("KKBOX", Log.getStackTraceString(e));
			return "";
		}
	}

	public static void printHeapMemory() {
		Double allocated = new Double(Debug.getNativeHeapAllocatedSize()) / new Double((1048576));
		Double available = new Double(Debug.getNativeHeapSize()) / 1048576.0;
		Double free = new Double(Debug.getNativeHeapFreeSize()) / 1048576.0;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		KKDebug.i("Heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
		KKDebug.i("Heap memory: allocated " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " +
				df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
	}
}
