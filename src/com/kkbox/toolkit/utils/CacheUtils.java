package com.kkbox.toolkit.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CacheUtils {
	public static String getCachePath(Context context, String tag, String id) {
		final File cacheDir = new File(context.getCacheDir().getAbsolutePath() + File.separator + tag);
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		return cacheDir.getAbsolutePath() + File.separator + StringUtils.getMd5Hash(id);
	}

	public static void saveDataToCache(String data, File cacheFile) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
