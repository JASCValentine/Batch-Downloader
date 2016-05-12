package com.jascvalentine.batchdownloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {
	private Properties properties;
	
	private static final String TIMEOUT_KEY = "timeout";
	private static final String DELAY_KEY = "delay";
	private static final String DOWNLOAD_PATH_KEY = "downloadPath";

	public static final int DEFAULT_TIMEOUT = 3000;
	public static final int DEFAULT_DELAY = 1000;
	public static final Path DEFAULT_DOWNLOAD_PATH = Paths.get(
			System.getProperty("user.home", "."), "Downloads");
	
	public static final Path DEFAULT_CONFIG_PATH = Paths.get(".", "config.properties");

	public static Config create() {
		Path configPath = DEFAULT_CONFIG_PATH;
		if (Files.exists(configPath)) {
			return new Config(configPath);
		} else {
			return new Config(DEFAULT_TIMEOUT, DEFAULT_DELAY, DEFAULT_DOWNLOAD_PATH);
		}
	}
	
	protected Config() {
		properties = new Properties();
	}
	
	public Config(Path configPath) {
		this();
		try (InputStream inStream = Files.newInputStream(configPath)) {
			properties.load(inStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Config(int timeout, int delay, Path downloadPath) {
		this();
		properties.setProperty(TIMEOUT_KEY, Integer.toString(timeout));
		properties.setProperty(DELAY_KEY, Integer.toString(delay));
		properties.setProperty(DOWNLOAD_PATH_KEY, downloadPath.toString());
	}
	
	public void store() {
		try (OutputStream out = Files.newOutputStream(DEFAULT_CONFIG_PATH)) {
			properties.store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connect and read timeout of URL connection in milliseconds
	 * 
	 * @return
	 */
	public int getTimeout() {
		return getIntegerProperty(TIMEOUT_KEY, DEFAULT_TIMEOUT);
	}

	/**
	 * The delay between each downloads in milliseconds
	 * 
	 * @return
	 */
	public int getDelay() {
		return getIntegerProperty(DELAY_KEY, DEFAULT_DELAY);
	}

	/**
	 * The root path of save downloaded files
	 * 
	 * @return
	 */
	public Path getDownloadPath() {
		String value = properties.getProperty(DOWNLOAD_PATH_KEY);
		if (value == null)
			return DEFAULT_DOWNLOAD_PATH;

		try {
			return Paths.get(value);
		} catch (InvalidPathException e) {
			return DEFAULT_DOWNLOAD_PATH;
		}
	}

	protected int getIntegerProperty(String key, int defaultValue) {
		String value = properties.getProperty(key);
		if (key == null)
			return defaultValue;

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public void setTimeout(int timeout) {
		properties.setProperty(TIMEOUT_KEY, Integer.toString(timeout));
	}
	
	public void setDelay(int delay) {
		properties.setProperty(DELAY_KEY, Integer.toString(delay));
	}
	
	public void setDownloadPath(Path downloadPath) {
		properties.setProperty(DOWNLOAD_PATH_KEY, downloadPath.toString());
	}
}
