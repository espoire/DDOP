package ddop.item.sources.crafted;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.dto.LevelRange;
import ddop.item.Item;
import ddop.item.PropertiesList;
import ddop.stat.Stat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class SlaversCraftedItemSource {
    private static class SlaversTier {
        private int level;
        private Map<String, List<String>> options;
    }

    public static Collection<Item> generateList(LevelRange levelRange, Set<String> filter) {
        Map<String, SlaversTier> recipeMap = SlaversCraftedItemSource.loadRecipeMap();

        SlaversTier selected = selectHighestTierInLevelRange(recipeMap, levelRange);
        if (selected == null) return null;

        applyFilter(selected, filter);

        return enumerateItemsFromOptions(selected);
    }

    private static void applyFilter(SlaversTier selected, Set<String> filter) {
        if(filter == null || filter.size() == 0) return;

        Set<String> slots = new HashSet<>(selected.options.keySet());
        for (String slot : slots) {
            List<String> effectOptions = selected.options.get(slot);

            effectOptions.removeIf(s -> !filter.contains(Stat.parseStat(s).category));
            if (effectOptions.size() == 0) selected.options.remove(slot);
        }
    }

    @Nullable
    private static SlaversTier selectHighestTierInLevelRange(Map<String, SlaversTier> recipeMap, LevelRange levelRange) {
        filterByLevel(recipeMap, levelRange);

        if(recipeMap.size() != 0) return selectHighestTierAvailable(recipeMap);
        return null;
    }

    @Nullable
    private static SlaversTier selectHighestTierAvailable(Map<String, SlaversTier> recipeMap) {
        SlaversTier selected = null;
        for(SlaversTier st : recipeMap.values())
            if(selected == null || st.level > selected.level)
                selected = st;

        return selected;
    }

    private static void filterByLevel(Map<String, SlaversTier> recipeMap, LevelRange levelRange) {
        Set<String> tiers = new HashSet<>(recipeMap.keySet());
        for(String tier : tiers) {
            SlaversTier st = recipeMap.get(tier);

            if(! levelRange.includes(st.level))
                recipeMap.remove(tier);
        }
    }

    private static Collection<Item> enumerateItemsFromOptions(SlaversTier selected) {
        PropertiesList itemTemplate = generateItemTemplate(selected);
        List<List<String>> options = new ArrayList<>(selected.options.values());

        return CraftedItemGenerator.generateItems(itemTemplate, options);
    }

    @NotNull
    private static PropertiesList generateItemTemplate(SlaversTier selected) {
        PropertiesList itemTemplate = new PropertiesList();

        itemTemplate.put("name", "slavers crafted");
        itemTemplate.put("minimum level", String.valueOf(selected.level));
        itemTemplate.put("slot", Arrays.asList(
                "waist",
                "neck",
                "trinket",
                "finger",
                "wrist",
                "feet"
        ));

        return itemTemplate;
    }

    private static Map<String, SlaversTier> loadRecipeMap() {
        String json = file.Reader.getEntireFile(Settings.SLAVERS_RECIPE_DEFINITIONS_JSON);
        Type type = new TypeToken<Map<String, SlaversTier>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    private static void printFilteredOptionsDebug(Map<String, List<String>> options) {
        int totalOptions = 1;
        for(String slot : options.keySet()) {
            List<String> effectOptions = options.get(slot);
            int count = effectOptions.size();

            System.out.println(slot + ": " + count + " effect options.");
            totalOptions *= count;

            for(String option : effectOptions)
                System.out.println("  " + option);
        }
        System.out.println(totalOptions + " total permutations.");
    }
}
