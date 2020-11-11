package ddop.item.sources.wiki;

import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.sources.crafted.NearlyFinished;
import file.CompileHTML;
import file.Directory;
import file.LinkReader;

import java.util.ArrayList;
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

			List<Item> noCraftable = new ArrayList<>(allItems);
			noCraftable.removeIf(item -> NearlyFinished.appliesTo(item));
			file.Writer.overwrite(Settings.WIKI_ITEMS_JSON, convertToJson(noCraftable));

			List<Item> nearlyFinishedBases = new ArrayList<>(allItems);
			nearlyFinishedBases.removeIf(item -> ! NearlyFinished.appliesTo(item));
			file.Writer.overwrite(Settings.NEARLY_FINISHED_BASE_ITEMS_JSON, convertToJson(nearlyFinishedBases));
		}
	}

	public static String convertToJson(List<Item> allItems) {
		return (new ItemList(allItems)).toJson();
	}
}
