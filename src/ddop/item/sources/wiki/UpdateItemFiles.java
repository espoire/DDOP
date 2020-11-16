package ddop.item.sources.wiki;

import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.sources.crafted.NearlyFinished;
import file.CompileHTML;
import file.LinkReader;
import file.Reader;
import util.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateItemFiles {
	public static void update(Set<String> reloadUpdates) {
		String updateScript = LinkReader.getScriptFromUpdatePages(Reader.getEntireDirectory(Settings.SOURCE_DIRECTORY), reloadUpdates);
		
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
			allItems.removeIf(item -> item.slots == null || item.slots.size() == 0);

			List<Item> noCraftable = new ArrayList<>(allItems);
			noCraftable.removeIf(NearlyFinished::appliesTo);
			String noCraftableJson = convertToJson(noCraftable);
			file.Writer.overwrite(Settings.WIKI_ITEMS_JSON, noCraftableJson);
			System.out.println("\nWrote non-crafting items to " + Settings.WIKI_ITEMS_JSON);
			System.out.println("Items: " + noCraftable.size());
			System.out.println("Length: " + NumberFormat.readableLargeNumber(noCraftableJson.length()));

			List<Item> nearlyFinishedBases = new ArrayList<>(allItems);
			nearlyFinishedBases.removeIf(item -> ! NearlyFinished.appliesTo(item));
			String nearlyFinishedBasesJson = convertToJson(nearlyFinishedBases);
			file.Writer.overwrite(Settings.NEARLY_FINISHED_BASE_ITEMS_JSON, nearlyFinishedBasesJson);
			System.out.println("\nWrote nearly finished base items to " + Settings.NEARLY_FINISHED_BASE_ITEMS_JSON);
			System.out.println("Items: " + nearlyFinishedBases.size());
			System.out.println("Length: " + NumberFormat.readableLargeNumber(nearlyFinishedBasesJson.length()));

			NearlyFinished.generateJsonFile();
		}
	}

	public static String convertToJson(List<Item> allItems) {
		return (new ItemList(allItems)).toJson();
	}
}
