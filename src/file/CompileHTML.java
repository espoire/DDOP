package file;

import ddop.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** This class compiles all downloaded "Item:" HTML files into a List of Items.
 * 
 * @author Espoire
 *
 */
public class CompileHTML {
	/** Accepts a directory, and compiles all files within it into a List of Items.
	 * 
	 * @param directory - The directory to look in (non-recursive).
	 */
	public static List<Item> loadAllItems(String directory) {
		System.out.println("Attempting to compile to item summaries all files in: " + directory);
		
		Set<String> filenames = Directory.getContentsAsFilenames(directory);
		int filesRead = filenames.size();
		
		System.out.println(filesRead + " files found. Compiling...");
		
		List<Item> ret = new ArrayList<>();
		
outer:	for(String filename : filenames) {
			Item i = CompileHTML.loadItemFromHTML(directory, filename);

			if(i == null || i.name == null || i.slots == null) continue;
			for(Item existing : ret)
				if(existing.name.equals(i.name))
					if(existing.minLevel == i.minLevel)
						continue outer;

			ret.add(i);
		}

		System.out.println("Finished! " + ret.size() + " items loaded.");
		return ret;
	}

	/** Attempts to compile a single specified file into an Item.
	 * 
	 * @param directory - The directory that the file is in.
	 * @param filename - The specific file's name.
	 * @return Item, or null if failed.
	 */
	public static Item loadItemFromHTML(String directory, String filename) {
		if(filename == null) return null;
		if(filename.length() > 60) {
			System.err.println("Filename exceeds 60 characters, skipping file: " + filename);
			return null;
		}
		
		String name = getItemNameFromFilename(filename);
		if(name == null || name.length() > 60) return null;
		
		String filePath = directory + "\\" + filename;
		String html = file.Reader.getEntireFile(filePath);
		String wikiItemSummary = ItemReader.cleanupHTML(html);
		
		if(wikiItemSummary == null) return null;
		Item i = ItemReader.loadFromWikiSummary(wikiItemSummary);

		return i;
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
