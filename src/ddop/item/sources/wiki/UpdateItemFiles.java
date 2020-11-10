package ddop.item.sources.wiki;

import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import file.CompileHTML;
import file.Directory;
import file.LinkReader;

import java.util.List;
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
			System.out.println("Compiling HTMLs to JSON summary...");
			List<Item> allItems = CompileHTML.loadAllItems(Settings.OUTPUT_DIRECTORY);
			String json = convertToJson(allItems);
			file.Writer.overwrite(Settings.WIKI_ITEMS_JSON, json);
		}
	}

	public static String convertToJson(List<Item> allItems) {
		String json = (new ItemList(allItems)).toJson();
		return json;
	}
}
