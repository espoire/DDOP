package ddop.item.sources.crafted;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.PropertiesList;
import util.NumberFormat;

import java.lang.reflect.Type;
import java.util.*;

public class NearlyFinished {
    /* Format example:
     *
     * Item name 1
     *   Nearly Finished
     *     Stat Option 1
     *     Stat Option 2
     *     ...
     *   Almost There
     *     Stat Options...
     *   Finishing Touch
     *     ...
     */
    private static final Map<String, Map<String, List<String>>> OPTIONS = readNearlyFinishedOptionsJson();
    private static Map<String, Map<String, List<String>>> readNearlyFinishedOptionsJson() {
        String json = file.Reader.getEntireFile(Settings.NEARLY_FINISHED_RECIPE_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    // Update nearlyFinishedOptions file. Reads from wiki items file, and nearlyFinishedDefinitions file.
    public static void generateJsonFile() {
        System.out.println("Generating Nearly Finished item versions...");

        List<String> itemVersionJsons = generateItemVersionJsons();
        String       jsonFileContent  = generateJsonFileContent(itemVersionJsons);

        file.Writer.overwrite(Settings.NEARLY_FINISHED_COMPLETED_ITEMS_JSON, jsonFileContent);

        System.out.println("\nWrote nearly finished item versions to " + Settings.NEARLY_FINISHED_COMPLETED_ITEMS_JSON);
        System.out.println("Items: " + itemVersionJsons.size());
        System.out.println("Length: " + NumberFormat.readableLargeNumber(jsonFileContent.length()));
    }

    private static String generateJsonFileContent(List<String> allJsons) {
        StringBuilder nearlyFinishedOptionsJson = new StringBuilder();

        nearlyFinishedOptionsJson.append("[");
        for(int i = 0; i < allJsons.size(); i++) {
            if(i != 0) nearlyFinishedOptionsJson.append(", ");
            nearlyFinishedOptionsJson.append(allJsons.get(i));
        }
        nearlyFinishedOptionsJson.append("]");

        return nearlyFinishedOptionsJson.toString();
    }

    private static List<String> generateItemVersionJsons() {
        List<String> allJsons = new ArrayList<>();

        for(Map.Entry<String, Map<String, List<String>>> nearlyFinishedOption : NearlyFinished.OPTIONS.entrySet()) {
            Map<String, List<String>> craftingOptions = nearlyFinishedOption.getValue();
            String                    itemName        = nearlyFinishedOption.getKey();
            PropertiesList            itemTemplate    = getItemTemplate(itemName);
            if(itemTemplate == null) continue;

            List<String> itemVersionJsons = applyAllEnchantmentOptions(itemTemplate, craftingOptions);

            allJsons.addAll(itemVersionJsons);
        }

        return allJsons;
    }

    private static ItemList NEARLY_FINISHED_BASE_ITEMS;
    private static PropertiesList getItemTemplate(String itemName) {
        if(NEARLY_FINISHED_BASE_ITEMS == null) NEARLY_FINISHED_BASE_ITEMS = ItemList.loadJsonFile(Settings.NEARLY_FINISHED_BASE_ITEMS_JSON);
        Item item = NEARLY_FINISHED_BASE_ITEMS.getNamedItem(itemName);

        if(item == null) return null;
        return item.getPropsClone();
    }

    private static List<String> applyAllEnchantmentOptions(PropertiesList itemTemplate, Map<String, List<String>> craftingOptions) {
        Set<String>        craftingTags       =                 craftingOptions.keySet();
        List<List<String>> enchantmentOptions = new ArrayList<>(craftingOptions.values());

        cleanup(itemTemplate, craftingTags);

        return CraftedItemGenerator.generateJsons(itemTemplate, enchantmentOptions);
    }

    private static void cleanup(PropertiesList itemTemplate, Set<String> craftingTags) {
        removeUnneededInfo(itemTemplate);
        removeCraftingTags(itemTemplate, craftingTags);
    }

    // TODO move to item, or even item loading from HTML
    private static void removeUnneededInfo(PropertiesList template) {
        template.remove("durability");
        template.remove("binding");
        template.remove("weight");
        template.remove("description");
        template.remove("base value");
        template.remove("hardness");
        template.remove("tips");
        template.remove("use magical device dc");

        String race = template.getFirst("race absolutely required");
        if(race != null) if(race.equals("none") || race.equals("[[:]]")) template.remove("race absolutely required");

        String trait = template.getFirst("required trait");
        if(trait != null) if(trait.equals("none")) template.remove("required trait");
    }
    private static void removeCraftingTags(PropertiesList template, final Set<String> tags) {
        List<String> enchantments = template.get("enchantments");

        for(String tag : tags)
            enchantments.removeIf(mod -> mod.contains(tag));

        template.remove("upgradeable?");
    }



    public static boolean appliesTo(Item item) {
        return NearlyFinished.OPTIONS.containsKey(item.name);
    }
}
