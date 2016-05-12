package com.jascvalentine.batchdownloader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static final char PLACEHOLDER = '^';

	public static List<String> processUrl(String template, List<String> keyLines) {
		if (template.indexOf(PLACEHOLDER) == -1) {
			throw new IllegalArgumentException("Missing placeholder");
		}

		List<String> list = new ArrayList<>(keyLines.size());

		for (String keyLine : keyLines) {
			StringBuilder sb = new StringBuilder(template);
			int fromIndex = 0;
			for (String key : keyLine.split(" ")) {
				int index = sb
						.indexOf(Character.toString(PLACEHOLDER), fromIndex);
				sb.replace(index, index + 1, key);
				fromIndex += index + key.length();
			}

			list.add(sb.toString());
		}

		return list;
	}

	public static List<Path> processDownloadPath(String rootPath,
			List<String> renames) {
		List<Path> paths = new ArrayList<>(renames.size());

		for (String s : renames) {
			paths.add(Paths.get(rootPath, s));
		}

		return paths;
	}
}
