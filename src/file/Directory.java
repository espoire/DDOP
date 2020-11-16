package file;

import util.Array;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The Directory class is used to assist with operations upon a Windows 7 directory and its contents.
 * The primary function is to list all the files in a folder, optionally preforming some limited preprocessing on that list.
 * 
 * @author Espoire
 *
 */
public class Directory {
	/** Lists all file names in a given directory.
	 * 
	 * @param directory The directory to list.
	 * @return A Set&lt;String&gt; containing one filename per String, excluding subdirectories.
	 */
	public static Set<String> getContents(String directory) {
		return getContentsImplementation(directory, false);
	}

	/**
	 * Lists the full path to each file in a given directory.
	 *
	 * @param directory The directory to list.
	 * @return A Set&lt;String&gt; containing one filepath per String, excluding subdirectories.
	 */
	public static Set<String> getContentsAsFilepaths(String directory) {
		return getContentsImplementation(directory, true);
	}

	private static Set<String> getContentsImplementation(String directory, boolean includeFullPath) {
		File[] files = new File(directory).listFiles();
		Set<String> ret = new LinkedHashSet<>();

		if(files != null) for(File f : files) {
			if(f.isDirectory()) continue;
			ret.add((includeFullPath ? directory + "\\" : "") + f.getName());
		}

		return ret;
	}

	private static Set<String> getContentsItemLinks() {
		Set<String> fileNames = getContents(ddop.Settings.OUTPUT_DIRECTORY);
		Set<String> shortLinks = WgetScripter.convertDownloadedFileNamesToShortWikiLinks(fileNames);
		
		return shortLinks;
	}

	static Set<String> filterDuplicateLinks(Set<String> shortLinks) {
		Set<String> directoryLinks = getContentsItemLinks();
		Set<String> ret = new HashSet<>();
		
		for(String shortLink : shortLinks) {
			if(Array.contains(directoryLinks, shortLink)) continue;
			ret.add(shortLink);
		}
		
		return ret;
	}
}
