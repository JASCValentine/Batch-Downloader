package com.jascvalentine.batchdownloader.download;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public interface Downloader extends Closeable {
	public String download(String url, Path downloadFile) throws IOException;
}
