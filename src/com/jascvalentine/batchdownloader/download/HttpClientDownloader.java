package com.jascvalentine.batchdownloader.download;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClients;

public class HttpClientDownloader implements Downloader {
	private HttpClient client;
	private RequestConfig config;

	public HttpClientDownloader(int timeout) {
		client = HttpClients.createDefault();
		config = RequestConfig.custom().setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout).setSocketTimeout(timeout).build();
	}

	@Override
	public String download(String url, Path downloadFile) throws IOException {
		HttpGet get = new HttpGet(url);
		get.setConfig(config);

		HttpResponse response = null;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity == null)
				throw new IOException("Empty response");

			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				Files.createDirectories(downloadFile);

				if (downloadFile.getFileName().toString().indexOf('.') == -1) {
					// missing file extension, guess from Content Type header
					Header header = entity.getContentType();
					if (header != null) {
						String extension = null;
						switch (header.getValue()) {
						case "image/png":
							extension = ".png";
							break;
						case "image/jpeg":
							extension = ".jpg";
							break;
						}
						downloadFile = downloadFile.resolveSibling(downloadFile
								.getFileName() + extension);
					}
				}

				try (OutputStream out = Files.newOutputStream(downloadFile)) {
					entity.writeTo(out);
				}
			}

			return statusLine.toString();
		} finally {
			HttpClientUtils.closeQuietly(response);
		}
	}

	@Override
	public void close() throws IOException {
		HttpClientUtils.closeQuietly(client);
	}
}
