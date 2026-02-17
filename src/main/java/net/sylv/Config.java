package net.sylv;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class Config {
	public String stripComments(List<String> lines) {
		// parse out comments (yay, i hope this causes no issues)
		StringBuilder builder = new StringBuilder(lines.size());
		for (String str : lines) {
			boolean inStr = false;
			boolean inComment = false;
			char[] chars = str.toCharArray();

			for (int i = 0; i < chars.length-1; i++) {
				char lastChar = chars[i];
				char nom = chars[i+1];
				if (!inComment && nom == '"' && lastChar != '\\') { // string
					inStr = !inStr;
				} else if (!inStr && nom == '/' && lastChar == '/') { // full line comment
					inComment = true;
					break;
				} else if ((!inStr && !inComment)&& nom == '*' && lastChar == '/') { // inline comment start
					inComment = true;
				} else if (inComment && nom == '/' && lastChar == '*') { // inline
					inComment = false;
					i += 1;
					continue;
				}

				if (!inComment) {
					builder.append(lastChar);
				}
			}

			// last character
			if (!inComment && chars.length > 0) {
				builder.append(chars[chars.length-1]);
			}

			builder.append('\n');
		}

		return builder.toString();
	}

	public void loadConfig() throws IOException {
		File f = new File("config.jsonc");
		List<String> srcLines;
		if (f.canRead()) {
			srcLines = Files.readAllLines(f.toPath());
		} else {
			try (InputStream str =  Config.class.getClassLoader().getResourceAsStream("config.jsonc")) {
				if (str == null) {
					throw new RuntimeException("No user defined config, and can't load default config");
				}

				InputStreamReader inputStreamReader = new InputStreamReader(str, StandardCharsets.UTF_8);
				try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
					srcLines = bufferedReader.lines().collect(Collectors.toList());
				}
			}


			if (f.canWrite() && !f.exists()) {
				Files.write(f.toPath(), srcLines);
			}
		}

		JSONObject jo = new JSONObject(stripComments(srcLines));
		System.out.println("nya");
	}
}
