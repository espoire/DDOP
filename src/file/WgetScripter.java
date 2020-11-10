package file;

import java.util.*;

public class WgetScripter {
	private static final String WIKI_ITEM_LINK_REGEX = "/page/Item:";
	private static final String WIKI_ITEM_LINK_SHORT_FORM = "/page/I:";
	private static final String WIKI_BASE_URL = "https://ddowiki.com";

	/** Converts a list of harvested DDOWiki item links into full URLs.<br />
	 * For example, <b>/page/Item:Duality,_the_Moral_Compass</b> would become <b>https://ddowiki.com/page/I:Duality,_the_Moral_Compass</b>
	 * 
	 * @param links - An ArrayList&lt;String> containing DDOWiki item links.
	 * @return ArrayList&lt;String> a new list of full URLs referring to the same pages on DDOWiki.
	 */
	static Set<String> convertWikiItemLinksToFullURL(Set<String> links) {
		Set<String> items = shortenWikiItemLinks(links);
		Set<String> ret = new HashSet<>();
		
		for(String item : items) {
			ret.add(WIKI_BASE_URL + item);
		}
		
		return ret;
	}
	
	
	
	
	static Set<String> shortenWikiItemLinks(Set<String> links) {
		Set<String> ret = new HashSet<>();
		
		for(String link : links) {
			link = link.replaceFirst(WIKI_ITEM_LINK_REGEX, WIKI_ITEM_LINK_SHORT_FORM);
			ret.add(link);
		}
		
		return ret;
	}




	private static final String DOWNLOADED_FILENAME_ITEM_REGEX = "^I(tem)?(%3A|:)[^<>:\"/\\\\|?*]+\\.html$";

	/** Converts downloaded wiki item filenames to the short relative wiki link format.<br />
	 * For example, <b>Item%3ADuality,_the_Moral_Compass.html</b> would become <b>/page/I:Duality,_the_Moral_Compass</b>
	 * 
	 * @param files - An ArrayList&lt;String> containing file names.
	 * @return ArrayList&lt;String> a new list of short wiki links.
	 */
	static Set<String> convertDownloadedFileNamesToShortWikiLinks(Set<String> files) {
		HashSet<String> ret = new HashSet<>();
		
		for(String file : files) {
			if(!file.matches(DOWNLOADED_FILENAME_ITEM_REGEX)) continue;
			file = file.replaceFirst("^I(tem)?(%3A|:)", "/page/I:").replaceFirst("\\.html$", "");
			ret.add(file);
		}
		
		return ret;
	}
	
	
	

	private static final String SCRIPT_HEADER =
			  "@echo off\n"
			+ "ECHO Automatically-generated DDOWiki downloader script.\n"
			+ "REM Invokes ./wget.exe\n"
			+ "REM Saves output to ./out/*\n"
			+ "\n"
			+ "REM Set working directory to directory in which the .bat file was double-clicked.\n"
			+ "CD %~dp0\n"
			+ "\n";
	private static final String SCRIPT_COMMENCEMENT_ECHO_COMMAND =
			  "ECHO Preparing to download from " + WIKI_BASE_URL + "\n"
			+ "ECHO Estimated time to download: ";
	private static final String SCRIPT_COMMENCEMENT_SLEEP_COMMAND =
			  "\nTIMEOUT 10\n\n";
	private static final String SCRIPT_FOOTER =
			  "\n\n"
			+ "ECHO Downloads complete!\n"
			+ "PAUSE\n"
			+ "EXIT";
	private static final int WAIT_BETWEEN_DOWNLOADS_SECONDS = 1;
	private static final String WINDOWS_7_SLEEP_COMMAND = "TIMEOUT /T " + WAIT_BETWEEN_DOWNLOADS_SECONDS + " /NOBREAK > NUL\n";
	/* wget options:
	 * -q  "Quiet"                   suppress console output
	 * -nc "No Clobber"              do not attempt to download files that we already have a copy of
	 * -P  "output directory Prefix" output to this directory
	 * -E  "Extensions"              include .html file extension
	 * --no-check-certificate		 ignore invalid https certificates
	 */
	private static final String WGET_COMMAND_PREFIX = "wget -q -nc -P out -E --no-check-certificate ";
	
	/** Converts a list of URLs into a Windows 7 batch script which will download the URLs to ./out/*.html with the aid of wget.exe.
	 * 
	 * @param urls - An ArrayList&lt;String> containing one full URL per String in the same format as example: <b>https://ddowiki.com/page/I:Duality,_the_Moral_Compass</b>
	 * @return A String containing a Windows 7 batch script, intended to be saved an ran as a .bat file.
	 */
	public static String generateWgetScript(Collection<String> urls) {
		StringBuilder script = new StringBuilder();
		
		script.append(SCRIPT_HEADER);
		script.append(SCRIPT_COMMENCEMENT_ECHO_COMMAND);
		script.append(getEstimatedWaitTimeString(urls));
		script.append(SCRIPT_COMMENCEMENT_SLEEP_COMMAND);
		
		int i = 0;
		for(String url : urls) {
			if(i != 0) script.append(WINDOWS_7_SLEEP_COMMAND);
			script.append(WGET_COMMAND_PREFIX).append(url.replaceAll("%", "%%")).append("\n");
			script.append("ECHO ").append(i + 1).append(" of ").append(urls.size()).append(" downloaded. (").append(((i + 1) * 100) / urls.size()).append("%%)\n\n");
			i++;
		}
		
		script.append(SCRIPT_FOOTER);
		
		return script.toString();
	}
	
	private static final int ESTIMATED_FILE_DOWNLOAD_DURATION_MILLIS = 200 + WAIT_BETWEEN_DOWNLOADS_SECONDS * 1000;
	private static String getEstimatedWaitTimeString(Collection<String> urls) {
		int millis = ESTIMATED_FILE_DOWNLOAD_DURATION_MILLIS * urls.size();
		int minutes = millis / 60000;
		int seconds = (millis - minutes * 60000) / 1000;
		
		String ret = "";
		
		if(minutes > 0) ret += minutes + "m";
		ret += seconds + "s";
		
		return ret;
	}
}
