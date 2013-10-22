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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPUtils {

	public static byte[] gzipCompress(byte[] input) {
		if (input == null || input.length == 0) { return null; }
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(input);
			gzip.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (gzip != null) {
					gzip.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out.toByteArray();
	}

	public static byte[] gzipDecompress(byte[] output) {
		if (output == null || output.length == 0) { return null; }
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(output);
		GZIPInputStream gunzip = null;
		try {
			gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[8192];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (gunzip != null) {
					gunzip.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out.toByteArray();
	}
}
