package com.kkbox.toolkit.file;

import java.io.File;

public class KKFileUtils {
	/**
	 * Check the file is not exist or it's length is equal zero
	 * @param path	File Path
	 * */
	public static boolean isFileEmpty(String path) {
		File file = new File(path);
		return isFileEmpty(file);
	}
	
	/**
	 * Check the file is not exist or it's length is equal zero
	 * @param file	What file object do you want to check
	 * */
	public static boolean isFileEmpty(File file) {
		return (file == null || !file.exists() || file.length() == 0);
	}
	
	
}
