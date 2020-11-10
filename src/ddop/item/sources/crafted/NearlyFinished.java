package ddop.item.sources.crafted;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.PropertiesList;
import util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NearlyFinished {
    public static void main(String... s) {
        NearlyFinished.printAllNearlyFinishedOptions();
    }

    private static final Map<String, Map<String, List<String>>> all = loadMap();
    // Item name 1
    //   Nearly Finished
    //     Stat Option 1
    //     Stat Option 2
    //     ...
    //   Almost There
    //     Stat Options...
    //   Finishing Touch
    //     ...

    private static Map<String, Map<String, List<String>>> loadMap() {
        String json = file.Reader.getEntireFile(Settings.NEARLY_FINISHED_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, Map<String, List<String>>>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void printAllNearlyFinishedOptions() {
        List<String> errors = new ArrayList<>();
        List<String> allJsons = new ArrayList<>();

        for(String itemName : all.keySet()) {
            Item item = ItemList.getNamedItem(itemName);
            if(item == null) {
                errors.add("Could not find item by name: " + itemName);
                continue;
            }

            PropertiesList template = item.getPropsClone();
            Map<String, List<String>> fullOptions = all.get(itemName);

            for(String craftableModName : fullOptions.keySet()) {
                List<String> enchantments = template.get("enchantments");

                for(int i = 0; i < enchantments.size(); i++) {
                    String mod = enchantments.get(i);
                    if(mod.contains(craftableModName)) enchantments.remove(i--);
                }
            }
            template.remove("upgradeable?");

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

            List<List<String>> options = new ArrayList<>(fullOptions.values());
            List<String> jsons = doTheThing(template, options);

            allJsons.addAll(jsons);
        }

        String nearlyFinishedOptionsJson = "";

        nearlyFinishedOptionsJson += "[";
        for(int i = 0; i < allJsons.size(); i++) {
            if(i != 0) nearlyFinishedOptionsJson += ", ";
            nearlyFinishedOptionsJson += allJsons.get(i);
        }
        nearlyFinishedOptionsJson += "]";

        file.Writer.overwrite(Settings.NEARLY_FINISHED_OPTIONS_JSON, nearlyFinishedOptionsJson);

        for(String error : errors) System.err.println(error);
    }

    private static List<String> doTheThing(PropertiesList template, List<List<String>> fullOptions) {
        return doTheThing(template, fullOptions, (String) null, 0);
    }

    private static List<String> doTheThing(PropertiesList template, List<List<String>> fullOptions, String versionTag, int i) {
        List<String> ret = new ArrayList<>();

        List<String> options = fullOptions.get(i);

        for(String option : options) {
            PropertiesList version = (PropertiesList) template.clone();
            version.put("enchantments", new ArrayList<>(version.get("enchantments")));

            String tag = versionTag;

            version.get("enchantments").add(option);
            if(tag == null) {
                tag = " ([";
            } else {
                tag += ", ";
            }
            tag += option;

            if(i + 1 == fullOptions.size()) {
                tag += "] version)";

                String versionName = version.getFirst("name");
                version.put("name", Collections.singletonList(versionName + tag));

                Item item = new Item(version);
                String json = item.toJson();
                ret.add(json);
            } else {
                List<String> jsons = doTheThing(version, fullOptions, tag, i+1);
                ret.addAll(jsons);
            }
        }

        return ret;
    }

    public static void printLegendaryCollectiveSightOptions() {
        List<Pair<String, String>> nearlyFinishedOptions = new ArrayList<>();
        nearlyFinishedOptions.add(new Pair<>("Strength +21", "str"));
        nearlyFinishedOptions.add(new Pair<>("Dexterity +21", "dex"));
        nearlyFinishedOptions.add(new Pair<>("Constitution +21", "con"));
        nearlyFinishedOptions.add(new Pair<>("Intelligence +21", "int"));
        nearlyFinishedOptions.add(new Pair<>("Wisdom +21", "wis"));
        nearlyFinishedOptions.add(new Pair<>("Charisma +21", "cha"));

        List<Pair<String, String>> almostThereOptions = new ArrayList<>();
        almostThereOptions.add(new Pair<>("Insightful Strength +10", "istr"));
        almostThereOptions.add(new Pair<>("Insightful Dexterity +10", "idex"));
        almostThereOptions.add(new Pair<>("Insightful Constitution +10", "icon"));
        almostThereOptions.add(new Pair<>("Insightful Intelligence +10", "iint"));
        almostThereOptions.add(new Pair<>("Insightful Wisdom +10", "iwis"));
        almostThereOptions.add(new Pair<>("Insightful Charisma +10", "icha"));

        System.out.println("[");
        for(Pair<String, String> nf : nearlyFinishedOptions) {
            for(Pair<String, String> at : almostThereOptions) {
                System.out.println("  {");
                System.out.println("    \"name\":[\"legendary collective sight " + nf.getValue() + " " + at.getValue() + " version\"],");
                System.out.println("    \"minimum level\":[\"29\"],");
                System.out.println("    \"item type\":[\"jewelry / goggles\"],");
                System.out.println("    \"slot\":[\"eye\"],");
                System.out.println("    \"enchantments\":[");
                System.out.println("      \"" + nf.getKey() + "\",");
                System.out.println("      \"" + at.getKey() + "\",");
                System.out.println("      \"quality resistance +4\",");
                System.out.println("      \"temperance of belief\",");
                System.out.println("      \"empty blue augment slot\"");
                System.out.println("    ],");
                System.out.println("    \"location\":[\"blown deadline, end chest\"]");
                System.out.println("  },");
            }
        }
        System.out.println("]");
    }
}
