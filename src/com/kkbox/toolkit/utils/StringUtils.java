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
 * StringUtils
 */
package com.kkbox.toolkit.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class StringUtils {
	public static String reverseString(String toReverse) {
		final StringBuffer buffer = new StringBuffer(toReverse);
		return buffer.reverse().toString();
	}

	public static String getMd5Hash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);

			while (md5.length() < 32)
				md5 = "0" + md5;

			return md5;
		} catch (NoSuchAlgorithmException e) {
			KKDebug.e(e.toString());
			return null;
		}
	}

	public static String hashMapToString(HashMap<String, String> map) {
		String tmpString = "";
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			tmpString += key + "," + map.get(key);
			if (iterator.hasNext()) {
				tmpString += ",";
			}
		}
		return tmpString;
	}

	public static String inputStreamToString(InputStream inputStream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		if (bufferedReader != null) {
			bufferedReader.close();
		}
		return stringBuilder.toString();
	}

	public static HashMap<String, String> stringToHashMap(String data) {
		HashMap<String, String> map = new HashMap<String, String>();
		if (!data.equals("")) {
			String[] stringArray = data.split(",");
			for (int i = 0; i < stringArray.length; i += 2) {
				map.put(stringArray[i], stringArray[i + 1]);
			}
		}
		return map;
	}

	public static String timeMillisToString(long timeMillis, String dateFormat) {
		Date targetDate = new Date(timeMillis);
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(targetDate);
	}
}
