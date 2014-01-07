package com.kkbox.toolkit.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import android.graphics.Bitmap;
import android.util.Log;

import com.kkbox.toolkit.BuildConfig;
import com.kkbox.toolkit.file.KKFileUtils;
import com.kkbox.toolkit.utils.KKDebug;

public class KKImageUtils {
	public class GetMode {
		public static final int DOWNLOAD = 1;
		public static final int CACHE = 2;
	}

	public static final ByteBuffer createBuffer(int size) {
		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		buffer.order(ByteOrder.nativeOrder());
		buffer.rewind();
		return buffer;
	}
	
	public static final Bitmap createBitmap(int width, int height) {
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		return Bitmap.createBitmap(width, height, conf);
	}
	
	public static final Bitmap createBitmap(ByteBuffer buffer, int width, int height) {
		Bitmap bitmap = createBitmap(width, height);
		bitmap.copyPixelsFromBuffer(buffer);
		return bitmap;
	}

	public static ByteBuffer BUFFER;
	private static void BuildBuffer(int size) {
		if(BUFFER == null) {
			BUFFER = createBuffer(size);
			KKDebug.i("Set Buffer Init", "Size: " + String.valueOf(size) + "Limite: " + String.valueOf(BUFFER.limit()));
		} else {
			if(BUFFER.limit() < size) {
				BUFFER.limit(size);
			}
			BUFFER.position(0);
			KKDebug.i("Set Buffer ReSize", "Size: " + String.valueOf(size) + "Limite: " + String.valueOf(BUFFER.limit()));
		}
	}

	/**
	 * Get Bitmap From Cache File, if the image file is save pure pixels when
	 * it downloaded.
	 * */
	public synchronized static Bitmap getBitmapFromCacheFileWhenDecode(String path) {
		long startTime = System.currentTimeMillis();
		File file = new File(path);
		if(KKFileUtils.isFileEmpty(file)) {
			return null;
		}
		
		Bitmap bitmap = null;
		try {
			final int fileSize = (int)file.length();
			BuildBuffer(fileSize);
//			if(BUFFER == null || (BUFFER.limit() + 1) != fileSize) {
//				BUFFER = createBuffer(fileSize);
//			}

			@SuppressWarnings("resource")
			FileChannel fileChannel = new FileInputStream(path).getChannel();
			fileChannel.read(BUFFER);
			
			BUFFER.rewind();
			final int width = BUFFER.getInt();
			final int height = BUFFER.getInt();
			
			bitmap = createBitmap(BUFFER, width, height);
		} catch (FileNotFoundException e) {
			if(BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if(BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		} 
		final long endTime = System.currentTimeMillis();

		KKDebug.e("Decode Time", String.valueOf(endTime - startTime));
		return bitmap;
	}

}
