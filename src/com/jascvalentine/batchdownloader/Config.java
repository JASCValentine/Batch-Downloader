package com.jascvalentine.batchdownloader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Config {
	private static final String TIMEOUT_KEY = "timeout";
	private static final String DELAY_KEY = "delay";
	private static final String DOWNLOAD_PATH_KEY = "downloadPath";

	public static final int DEFAULT_TIMEOUT = 3000;
	public static final int DEFAULT_DELAY = 1000;
	public static final Path DEFAULT_DOWNLOAD_PATH = Paths.get(
			System.getProperty("user.home", "."), "Downloads");

	private Preferences preferences;

	public Config() {
		preferences = Preferences.userNodeForPackage(getClass());
	}

	/**
	 * Connect and read timeout of URL connection in milliseconds
	 * 
	 * @return
	 */
	public int getTimeout() {
		return preferences.getInt(TIMEOUT_KEY, DEFAULT_TIMEOUT);
	}

	/**
	 * The delay between each downloads in milliseconds
	 * 
	 * @return
	 */
	public int getDelay() {
		return preferences.getInt(DELAY_KEY, DEFAULT_DELAY);
	}

	/**
	 * The root path of save downloaded files
	 * 
	 * @return
	 */
	public Path getDownloadPath() {
		String path = preferences.get(DOWNLOAD_PATH_KEY, null);
		return path != null ? Paths.get(path) : DEFAULT_DOWNLOAD_PATH;
	}

	public void setTimeout(int timeout) {
		preferences.putInt(TIMEOUT_KEY, timeout);
	}

	public void setDelay(int delay) {
		preferences.putInt(DELAY_KEY, delay);
	}

	public void setDownloadPath(Path downloadPath) {
		preferences.put(DOWNLOAD_PATH_KEY, downloadPath.toString());
	}
}
