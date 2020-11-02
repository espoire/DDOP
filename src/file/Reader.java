package file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.stream.Stream;

public class Reader {
	public static String getEntireFile(String filepath) {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filepath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Cannot read file. " + e);
		}

		return contentBuilder.toString();
	}

	public static FileTime getModifiedTime(String filepath) {
		Path p = Paths.get(filepath);
		try {
		    return (FileTime) Files.getAttribute(p, "lastModifiedTime");
		} catch (IOException e) {
		    System.err.println("Cannot check the creation time. " + e);
		}
		return null;
	}
}
