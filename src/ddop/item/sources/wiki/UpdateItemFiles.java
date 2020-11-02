package ddop.item.sources.wiki;

import ddop.Settings;
import ddop.item.ItemList;
import file.CompileHTML;
import file.Directory;
import file.LinkReader;

import java.util.Set;

public class UpdateItemFiles {
	public static void update(Set<String> reloadUpdates) {
		String updateScript = LinkReader.getScriptFromUpdatePages(Directory.getContents(Settings.SOURCE_DIRECTORY), reloadUpdates);
		
		if(updateScript != null) {
			System.out.println(updateScript);
			System.out.println();
			System.out.println("-----------------");
			System.out.println();
			System.out.println("Please run the above batch script, then rerun the UpdateGearDefinitions program.");
		} else {
			System.out.println("All raw item HTMLs up to date.");
			System.out.println("Compiling HTMLs to wikiitem summaries...");
			CompileHTML.compileAll(Settings.OUTPUT_DIRECTORY);
			convertWikiItemsToJson();
		}
	}

	public static void convertWikiItemsToJson() {
		String json = ItemList.loadWikiitemsDirectory(Settings.ITEM_DIRECTORY).toJson();
		file.Writer.overwrite(Settings.ITEM_JSON, json);
	}
}
