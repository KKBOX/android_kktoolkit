package com.kkbox.toolkit.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import android.graphics.Bitmap;

import com.kkbox.toolkit.BuildConfig;
import com.kkbox.toolkit.file.KKFileUtils;

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
	
	/**
	 * Get Bitmap From Cache File, if the image file is save pure pixels when
	 * it downloaded.
	 * */
	public static Bitmap getBitmapFromCacheFileWhenDecode(String path) {
		File file = new File(path);
		if(KKFileUtils.isFileEmpty(file)) {
			return null;
		}
		
		Bitmap bitmap = null;
		try {
			ByteBuffer buffer = createBuffer((int)file.length());
			
			@SuppressWarnings("resource")
			FileChannel fileChannel = new FileInputStream(path).getChannel();
			fileChannel.read(buffer);
			
			buffer.rewind();
			final int width = buffer.getInt();
			final int height = buffer.getInt();
			
			bitmap = createBitmap(buffer, width, height);
		} catch (FileNotFoundException e) {
			if(BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if(BuildConfig.DEBUG) {
				e.printStackTrace();
			}
		} 
		
		return bitmap;
	}

}
