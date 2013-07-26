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
 * RC4 encrypt/decrypt
 */
package com.kkbox.toolkit.crypto;


public class Rc4 {
	static private boolean useJNI = true;

	static {
		try {
			System.loadLibrary("rc4");
		} catch (UnsatisfiedLinkError e) {
			useJNI = false;
		}
	}

	private Rc4j rc4j;
	private long jniPointer;

	private native long setKey(long ptr, byte[] key);

	private native int _getNextOutput(long ptr);

	private native void _skip(long ptr, long skipBytes);

	private native void _crypt(long ptr, byte[] src, int src_off, byte[] dst, int dst_off, int length);

	private native void _release(long ptr);

	private native void _mark(long ptr);

	private native void _restore(long ptr);

	public Rc4(byte[] key) {
		if (useJNI) {
			jniPointer = setKey(-1, key);
		} else {
			rc4j = new Rc4j(key);
		}
	}

	protected void finalize() throws Throwable {
		if (useJNI) {
			_release(jniPointer);
		}

		super.finalize();
	}

	public void skip(int length) {
		if (useJNI) {
			_skip(jniPointer, length);
		} else {
			rc4j.skip(length);
		}
	}

	public void crypt(byte[] src, int offset, int length) {
		if (useJNI) {
			_crypt(jniPointer, src, offset, src, offset, length);
		} else {
			rc4j.crypt(src, offset, length);
		}
	}

	public void crypt(byte[] src, int length) {
		if (useJNI) {
			_crypt(jniPointer, src, 0, src, 0, length);
		} else {
			rc4j.crypt(src, length);
		}
	}

	public void crypt(byte[] src) {
		if (useJNI) {
			_crypt(jniPointer, src, 0, src, 0, src.length);
		} else {
			rc4j.crypt(src);
		}
	}
}
