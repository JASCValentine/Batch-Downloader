package com.jascvalentine.batchdownloader.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.jascvalentine.batchdownloader.download.Downloader;

public class DownloadWorker extends SwingWorker<Void, Object[]> {
	private Downloader downloader;
	private int delay;
	private List<String> urlList;
	private List<Path> downloadPaths;
	private PrintWriter output;

	/**
	 * 
	 * @param downloader
	 * @param delay
	 * @param urlList
	 * @param downloadPaths
	 * @param output
	 *            Similar to standard out
	 */
	public DownloadWorker(Downloader downloader, int delay,
			List<String> urlList, List<Path> downloadPaths, PrintWriter output) {
		this.downloader = downloader;
		this.delay = delay;
		this.urlList = urlList;
		this.downloadPaths = downloadPaths;
		this.output = output;
	}

	@Override
	protected Void doInBackground() throws Exception {
		int complete = 0;

		for (int i = 0; i < urlList.size(); i++) {
			String url = urlList.get(i);
			Path downloadFile = downloadPaths.get(i);

			Object[] chunk = new Object[2];
			try {
				String statusLine = downloader.download(url, downloadFile);
				chunk[0] = url + " : " + statusLine;
			} catch (IOException e) {
				chunk[0] = url + " : " + e;
				chunk[1] = e;
			}
			publish(chunk);

			++complete;
			firePropertyChange("complete", null, Integer.valueOf(complete));

			float progress = 100.0f * complete / urlList.size();
			setProgress(Math.min(100, Math.round(progress)));

			// don't sleep at last element
			if (i < urlList.size() - 1) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}

		return null;
	}

	@Override
	protected void process(List<Object[]> chunks) {
		for (Object[] o : chunks) {
			output.println((String) o[0]);
			if (o[1] != null) {
				((Exception) o[1]).printStackTrace(output);
			}
		}
	}

	@Override
	protected void done() {
		try {
			get();
			output.println("Done");
		} catch (ExecutionException e) {
			e.getCause().printStackTrace(output);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
