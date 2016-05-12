package com.jascvalentine.batchdownloader.ui;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentWriter extends Writer {
	private Document doc;

	public DocumentWriter(Document doc) {
		this.doc = Objects.requireNonNull(doc);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		write(new String(cbuf, off, len));
	}

	@Override
	public void write(String str) throws IOException {
		try {
			doc.insertString(doc.getLength(), str, null);
		} catch (BadLocationException e) {			
		}
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		if (len == 0) return;
		
		write(str.substring(off, off + len));
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}
}
