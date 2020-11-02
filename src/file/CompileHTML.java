package file;

import ddop.item.Item;
import ddop.Settings;

import java.nio.file.attribute.FileTime;
import java.util.Set;

/** This class compiles all downloaded "Item:" HTML files into a short wiki summary file in the directory specified by Settings.ITEM_DIRECTORY.
 * 
 * @author Espoire
 *
 */
public class CompileHTML {
	public static void main(String... s) {
		CompileHTML.compileAll(Settings.OUTPUT_DIRECTORY);
	}
	
	/** Accepts a directory, and compiles all files within it into wiki summaries.
	 * 
	 * @param directory - The directory to look in (non-recursive).
	 */
	public static void compileAll(String directory) {
		System.out.println("Attempting to compile to item summaries all files in: " + directory);
		
		Set<String> files = Directory.getContents(directory);
		int filesRead = files.size();
		
		System.out.println(filesRead + " files found. Compiling...");
		
		int filesWritten = 0;
		
		for(String filename : files) {
			boolean writeSuccessful = CompileHTML.compile(directory, filename);
			if(writeSuccessful) filesWritten++;
		}
		
		if(filesWritten > 0) {
			System.out.println("Finished! " + filesWritten + " item files updated.");
		} else {
			System.out.println("Finished! No files needed to be updated.");
		}
	}

	/** Attempts to compile a single specified file into a wiki summary, and save it into the directory specified by Settings.ITEM_DIRECTORY.
	 * 
	 * @param directory - The directory that the file is in.
	 * @param filename - The specific file's name.
	 * @return <b>true</b> if successful, <b>false</b> otherwise.
	 */
	private static boolean compile(String directory, String filename) {
		if(filename == null) return false;
		if(filename.length() > 100) {
			System.err.println("Filename exceeds 100 characters, skipping file: " + filename);
			return false;
		}
		
		String name = getItemNameFromFilename(filename);
		if(name == null || name.length() > 100) return false;
		
		String filePath = directory + "\\" + filename;
		String html = file.Reader.getEntireFile(filePath);
		FileTime sourceTimestamp = file.Reader.getModifiedTime(filePath);
		String wikiItemSummary = ItemReader.cleanupHTML(html);
		
		if(wikiItemSummary == null) return false;
		Item i = ItemReader.loadFromWikiSummary(wikiItemSummary, filePath);
		if(i == null) return false;
		
		String outputFilePath = Settings.ITEM_DIRECTORY + "\\" + name + ".wikiitem";
		
		boolean writeSuccessful = false;
		if(Writer.isFirstNewer(filePath, outputFilePath)) {
			writeSuccessful = Writer.overwrite(outputFilePath, wikiItemSummary);
			if(writeSuccessful) Writer.setFileTime(outputFilePath, sourceTimestamp);
			System.out.println(" . " + filename);
		}

		return writeSuccessful;
	}

	private static String getItemNameFromFilename(String filename) {
		if(filename.endsWith(".html")) {
			filename = filename.substring(0, filename.length() -5);
			if(filename.startsWith("Item%3A")) filename = filename.substring(7);
			if(filename.startsWith("I%3A")) filename = filename.substring(4);
			filename = filename.replaceAll("_", " ");
			filename = filename.toLowerCase();
			return filename;
		}
		
		return null;
	}
}
