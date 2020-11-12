package ddop.item.sources.crafted;

import ddop.item.Item;
import ddop.item.PropertiesList;

import java.util.ArrayList;
import java.util.List;

public class CraftedItemGenerator {
    public static List<String> generateJsons(PropertiesList itemTemplate, List<List<String>> enchantmentOptions) {
        List<String> ret = new ArrayList<>();
        List<Item> items = generateItems(itemTemplate, enchantmentOptions);

        for(Item i : items)
            ret.add(i.toJson());

        return ret;
    }

    public static List<Item> generateItems(PropertiesList itemTemplate, List<List<String>> options) {
        return recursiveApplyEnchantmentOptions(itemTemplate, options, null, 0);
    }

    private static List<Item> recursiveApplyEnchantmentOptions(PropertiesList itemTemplate, List<List<String>> enchantmentOptions, String versionTag, int listPosition) {
        List<Item> ret = new ArrayList<>();
        List<String> enchantments = enchantmentOptions.get(listPosition++);

        for(String enchantment : enchantments) {
            PropertiesList version = (PropertiesList) itemTemplate.clone();

            insertEnchantment(version, enchantment);
            String tag = extendTag(versionTag, enchantment);

            if(listPosition == enchantmentOptions.size()) {
                tag = finishTag(tag);
                insertVersionTag(version, tag);

                ret.add(new Item(version));
            } else {
                List<Item> items = recursiveApplyEnchantmentOptions(version, enchantmentOptions, tag, listPosition);
                ret.addAll(items);
            }
        }

        return ret;
    }

    private static void insertEnchantment(PropertiesList version, String enchantment) {
        List<String> versionEnchantments = new ArrayList<>();
        if(version.get("enchantments") != null) versionEnchantments.addAll(version.get("enchantments"));

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
