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
 * RC4j
 */
package com.kkbox.toolkit.crypto;

public class Rc4j {
	private final byte[] engineState = new byte[256];
	private int i = 0;
	private int j = 0;

	private static byte[] currentKey;
	private static byte[] currentEngineState = new byte[256];

	public Rc4j(byte[] key) {
		if (!key.equals(currentKey)) {
			currentKey = key;
			int length = key.length;
			if (length > 256) {
				length = 256;
			}
			int a = 0, b = 0;
			for (a = 0; a < 256; a++) {
				engineState[a] = (byte)a;
			}
			byte num;
			for (a = 0; a < 256; a++) {
				b = (b + (0xff & key[(a % length)]) + engineState[a]) & 0xff;

				num = engineState[a];
				engineState[a] = engineState[b];
				engineState[b] = num;
			}
			for (a = 0; a < 256; a++) {
				currentEngineState[a] = engineState[a];
			}
		} else {
			for (int a = 0; a < 256; a++) {
				engineState[a] = currentEngineState[a];
			}
		}
	}

	public void crypt(byte[] buffer) {
		crypt(buffer, buffer.length);
	}

	public void crypt(byte[] buffer, int length) {
		byte num;
		for (int a = 0; a < length; a++) {
			i = (i + 1) & 0xff;
			j = (j + engineState[i]) & 0xff;

			num = engineState[i];
			engineState[i] = engineState[j];
			engineState[j] = num;

			buffer[a] = (byte)(buffer[a] ^ engineState[((engineState[i] + engineState[j]) & 0xff)]);
		}
	}
	
	public void crypt(byte[] buffer, int offset, int length) {
		byte num;
		for (int a = 0; a < length; a++) {
			i = (i + 1) & 0xff;
			j = (j + engineState[i]) & 0xff;

			num = engineState[i];
			engineState[i] = engineState[j];
			engineState[j] = num;

			buffer[a + offset] = (byte)(buffer[a + offset] ^ engineState[((engineState[i] + engineState[j]) & 0xff)]);
		}
	}
	
	public void skip(long numBytes) {
		for (long a = 0; a < numBytes; a++) {
			i = (i + 1) & 0xff;
			j = (j + engineState[i]) & 0xff;

			final byte num = engineState[i];
			engineState[i] = engineState[j];
			engineState[j] = num;
		}
	}
}