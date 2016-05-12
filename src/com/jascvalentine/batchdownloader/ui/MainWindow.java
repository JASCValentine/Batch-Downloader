package com.jascvalentine.batchdownloader.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jascvalentine.batchdownloader.Config;
import com.jascvalentine.batchdownloader.Main;
import com.jascvalentine.batchdownloader.download.Downloader;
import com.jascvalentine.batchdownloader.download.HttpClientDownloader;

public class MainWindow {
	private JLabel lblTemplate;
	private JTextField taTemplate;
	private JLabel lblDownloadPath;
	private JTextField taDownloadPath;
	private JButton btnDownloadPath;
	private JLabel lblPattern;
	private JTextArea taPattern;
	private JLabel lblRename;
	private JTextArea taRename;

	private JTextArea taOutput;

	private JLabel lblTimeout;
	private JSpinner spTimeout;
	private JLabel lblDelay;
	private JSpinner spDelay;
	private JButton btnStart;
	private JButton btnClear;
	private JProgressBar pgDownload;
	private JLabel lblProgress;

	private Config config;

	private WindowListener windowListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			// closing window while focused on JSpinner
			try {
				spTimeout.commitEdit();
			} catch (ParseException e1) {
				// ignore
			}
			try {
				spDelay.commitEdit();
			} catch (ParseException e1) {
				// ignore
			}

			config.setDelay(((Number) spDelay.getValue()).intValue());
			config.setTimeout(((Number) spTimeout.getValue()).intValue());
			config.setDownloadPath(Paths.get(taDownloadPath.getText()));
			config.store();
		}
	};

	public MainWindow() {
		config = Config.create();
	}

	public Container getMainComponent() {
		lblTemplate = new JLabel("Template: ");
		lblTemplate.setToolTipText(String.format(
				"Use %c as placeholder character",
				Character.valueOf(Main.PLACEHOLDER)));
		taTemplate = new JTextField();
		lblDownloadPath = new JLabel("Download Path: ");
		taDownloadPath = new JTextField();
		btnDownloadPath = new JButton("Browse...");
		btnDownloadPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (fc.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
					SwingUtilities.getWindowAncestor(fc);
					taDownloadPath.setText(fc.getSelectedFile().toString());
				}
			}
		});
		lblPattern = new JLabel("Pattern: ");
		taPattern = new JTextArea();
		lblRename = new JLabel("Rename: ");
		taRename = new JTextArea();

		taOutput = new JTextArea();
		taOutput.setEditable(false);

		lblTimeout = new JLabel("Timeout: ");
		spTimeout = new JSpinner();
		spTimeout.setEditor(new JSpinner.NumberEditor(spTimeout, "0"));
		spTimeout.setPreferredSize(new Dimension(80, 20));
		lblDelay = new JLabel("Delay: ");
		spDelay = new JSpinner();
		spDelay.setEditor(new JSpinner.NumberEditor(spDelay, "0"));
		spDelay.setPreferredSize(new Dimension(80, 20));
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStart_actionPerformed(e);
			}
		});
		btnClear = new JButton("Clear Output");
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taOutput.setText(null);
			}
		});
		pgDownload = new JProgressBar();
		lblProgress = new JLabel();

		JPanel pnlCenter = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.BELOW_BASELINE_TRAILING;
		gbc.gridx = 0;
		pnlCenter.add(lblTemplate, gbc);
		pnlCenter.add(lblDownloadPath, gbc);
		pnlCenter.add(lblPattern, gbc);
		pnlCenter.add(lblRename, gbc);

		gbc.gridx = GridBagConstraints.RELATIVE;
		gbc.gridy = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		pnlCenter.add(taTemplate, gbc);

		gbc.gridy++;
		gbc.gridwidth = 1;
		pnlCenter.add(taDownloadPath, gbc);
		gbc.weightx = 0;
		pnlCenter.add(btnDownloadPath, gbc);

		gbc.gridy++;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.insets = new Insets(1, 0, 1, 0);
		pnlCenter.add(new JScrollPane(taPattern), gbc);

		gbc.gridy++;
		pnlCenter.add(new JScrollPane(taRename), gbc);

		JPanel pnlOutput = new JPanel(new BorderLayout());
		pnlOutput.setBorder(BorderFactory.createTitledBorder("Output"));
		pnlOutput.add(new JScrollPane(taOutput));

		JSplitPane spCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
				pnlCenter, pnlOutput);
		spCenter.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
		spCenter.setResizeWeight(0.5);

		JPanel pnlSouth = new JPanel();
		pnlSouth.add(lblTimeout);
		pnlSouth.add(spTimeout);
		pnlSouth.add(lblDelay);
		pnlSouth.add(spDelay);
		pnlSouth.add(btnStart);
		pnlSouth.add(btnClear);
		pnlSouth.add(pgDownload);
		pnlSouth.add(lblProgress);

		JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(spCenter, BorderLayout.CENTER);
		pnl.add(pnlSouth, BorderLayout.SOUTH);

		taDownloadPath.setText(config.getDownloadPath().toString());
		spTimeout.setValue(Integer.valueOf(config.getTimeout()));
		spDelay.setValue(Integer.valueOf(config.getDelay()));

		return pnl;
	}

	private void btnStart_actionPerformed(ActionEvent e) {
		String template = taTemplate.getText();
		String downloadPath = taDownloadPath.getText();

		List<String> keyLines = Arrays.asList(taPattern.getText().split("\n"));
		List<String> renameLines = Arrays
				.asList(taRename.getText().split("\n"));

		final List<String> urls = Main.processUrl(template, keyLines);
		List<Path> downloadPaths = Main.processDownloadPath(downloadPath,
				renameLines);

		int timeout = ((Number) spTimeout.getValue()).intValue();
		int delay = ((Number) spDelay.getValue()).intValue();

		final Downloader downloader = new HttpClientDownloader(timeout);
		DocumentWriter output = new DocumentWriter(taOutput.getDocument());
		@SuppressWarnings("resource")
		DownloadWorker worker = new DownloadWorker(downloader, delay, urls,
				downloadPaths, new PrintWriter(output, true)) {
			@Override
			protected void done() {
				super.done();
				pgDownload.setValue(0);
				lblProgress.setText(null);
				btnStart.setEnabled(true);
				try {
					downloader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		};
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					int progress = ((Number) evt.getNewValue()).intValue();
					pgDownload.setValue(progress);
				} else if ("complete".equals(evt.getPropertyName())) {
					String s = String.format("%d / %d", evt.getNewValue(),
							Integer.valueOf(urls.size()));
					lblProgress.setText(s);
				}
			}
		});
		pgDownload.setValue(0);
		btnStart.setEnabled(false);
		worker.execute();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// ignore
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow main = new MainWindow();

				JFrame frame = new JFrame("Batch Downloader");
				frame.addWindowListener(main.windowListener);
				frame.setContentPane(main.getMainComponent());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});
	}
}
