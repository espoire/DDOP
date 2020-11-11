package ddop.item.sources.crafted;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.PropertiesList;

import java.lang.reflect.Type;
import java.util.*;

public class NearlyFinished {
    // Update nearlyFinishedOptions file. Reads from wiki items file, and nearlyFinishedDefinitions file.
    public static void main(String... s) {
        NearlyFinished.generateJsonFile();
    }

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
        String json = file.Reader.getEntireFile(Settings.NEARLY_FINISHED_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void generateJsonFile() {
        List<String> itemVersionJsons = generateItemVersionJsons();
        String       jsonFileContent  = generateJsonFileContent(itemVersionJsons);

        file.Writer.overwrite(Settings.NEARLY_FINISHED_OPTIONS_JSON, jsonFileContent);
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

    private static PropertiesList getItemTemplate(String itemName) {
        Item item = ItemList.getNamedItem(itemName);

        if(item == null) return null;
        return item.getPropsClone();
    }

    private static List<String> applyAllEnchantmentOptions(PropertiesList itemTemplate, Map<String, List<String>> craftingOptions) {
        Set<String>        craftingTags       =                 craftingOptions.keySet();
        List<List<String>> enchantmentOptions = new ArrayList<>(craftingOptions.values());

        cleanup(itemTemplate, craftingTags);

        return recursiveApplyEnchantmentOptions(itemTemplate, enchantmentOptions, null, 0);
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

    private static List<String> recursiveApplyEnchantmentOptions(PropertiesList itemTemplate, List<List<String>> enchantmentOptions, String versionTag, int listPosition) {
        List<String> ret = new ArrayList<>();
        List<String> enchantments = enchantmentOptions.get(listPosition++);

        for(String enchantment : enchantments) {
            PropertiesList version = (PropertiesList) itemTemplate.clone();

            insertEnchantment(version, enchantment);
            String tag = extendTag(versionTag, enchantment);

            if(listPosition == enchantmentOptions.size()) {
                tag = finishTag(tag);
                insertVersionTag(version, tag);

                ret.add(version.toJson());
            } else {
                List<String> jsons = recursiveApplyEnchantmentOptions(version, enchantmentOptions, tag, listPosition);
                ret.addAll(jsons);
            }
        }

        return ret;
    }

    private static void insertEnchantment(PropertiesList version, String enchantment) {
        List<String> versionEnchantments = new ArrayList<>(version.get("enchantments"));
        versionEnchantments.add(enchantment);
        version.put("enchantments", versionEnchantments);
    }

    private static String extendTag(String versionTag, String enchantment) {
        String tag = versionTag;
        if(tag == null) {
            tag = " ([";
        } else {
            tag += ", ";
        }
        tag += enchantment;
        return tag;
    }

    private static String finishTag(String tag) {
        tag += "] version)";
        return tag;
    }

    private static void insertVersionTag(PropertiesList version, String tag) {
        version.put("name", version.getFirst("name") + tag);
    }
}
