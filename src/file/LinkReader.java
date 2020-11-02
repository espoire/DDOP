package file;

import java.util.*;

import ddop.Settings;
import util.Array;

/**
 * The LinkReader class reads an HTML file and extracts URLs from "a" tag href arguments.
 * 
 * In particular, it is intended to gather a list of "Item:" pages from DDOWiki, taking a DDOWiki HTML page as input.
 * 
 * @author Espoire
 *
 */
public class LinkReader {
	public static String getScriptFromUpdatePages(Set<String> inputFiles, Set<String> forceReloadFiles) {
		if(inputFiles == null || inputFiles.size() == 0) return null;
		
		Set<String> urls = new HashSet<>();
		
		for(String file : inputFiles) {
			Set<String> itemLinks = loadItemLinksFromWikiHTMLFile(file);
			
			if(!forceReloadFiles.contains(file)) {
				itemLinks = Directory.filterDuplicateLinks(itemLinks);
			}
			
			Collection<String> newUrls = WgetScripter.convertWikiItemLinksToFullURL(itemLinks);
			urls.addAll(newUrls);
		}
		
		if(urls.size() == 0) return null;
		
		String result = WgetScripter.generateWgetScript(urls);
		return result;
	}
	
	/** Takes a DDOWiki HTML file, and outputs a list of all "Item:" pages link by it.
	 * 
	 * @param fileName - The file name to be loaded
	 * @return An ArrayList&lt;String&gt; containing one link href per String, with no duplicates. Mostly relative links, not full URLs.
	 */
	private static Set<String> loadItemLinksFromWikiHTMLFile(String fileName) {
		String filePath = Settings.SOURCE_DIRECTORY + "\\" + fileName;
		String html = file.Reader.getEntireFile(filePath);
		Set<String> wikiLinkList = WgetScripter.shortenWikiItemLinks(getAllItemLinks(html));
		
		return wikiLinkList;
	}

	private static final String WIKI_ITEM_LINK_REGEX = "(?s)\".*?<a href=\"/page/Item:";
	private static final String WIKI_FOOTER_REGEX    = "(?s)\".*$";
	private static final String BLANK_STRING_REGEX   = "^\\s*$";
	private static Set<String> getAllItemLinks(String html) {
		String[] plainText = ("\"" + html).replaceAll(WIKI_ITEM_LINK_REGEX, "\n/page/Item:")
										  .replaceAll(WIKI_FOOTER_REGEX, "")
										  .split("\n");
		
		Set<String> ret = new HashSet<>();
		
		for(String line : plainText) {
			// Remove blanks
			if(line.matches(BLANK_STRING_REGEX)) continue;
			
			// Remove duplicates
			if(Array.contains(ret, line)) continue;
			
			// Cleanup HTML character escapes
			line = line.replaceAll("%3A", ":");
			line = line.replaceAll("%2B", "+");
			line = line.replaceAll("%27", "'");
			line = line.replaceAll("%E2%80%99", "'");
			
			ret.add(line);
		}
		
		return ret;
	}
}
