package ddop.item.sources.crafted;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.dto.LevelRange;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.PropertiesList;
import ddop.optimizer.valuation.ArmorType;
import ddop.stat.Stat;
import ddop.stat.StatFilter;
import util.NumberFormat;

import java.lang.reflect.Type;
import java.util.*;

public class CannithCraftedItemSource {
    private static class CannithCraftingRecipeBook {
        private CannithStatScaling scaling;
        private CannithItemTemplates options;
    }

    private static class CannithItemTemplates extends HashMap<String, CannithItemTemplate> {
        public CannithItemTemplates filterSlots(Set<ItemSlot> includedItemSlots) {
            for(String itemType : new ArrayList<>(this.keySet())) {
                CannithItemTemplate template = this.get(itemType);

                template.filterTo(includedItemSlots);

                if(template.slots.size() == 0) this.remove(itemType);
            }

            return this;
        }

        public CannithItemTemplates filterStats(StatFilter statsFilter) {
            for(String itemType : new ArrayList<>(this.keySet())) {
                CannithItemTemplate template = this.get(itemType);

                template.filterStats(statsFilter);

                if(template.options.size() == 0) this.remove(itemType);
            }

            return this;
        }

        public CannithItemTemplates setArmorType(ArmorType at) {
            if(at == null) {
                this.remove("armor");
            } else {
                CannithItemTemplate armorTemplate = this.get("armor");
                if (armorTemplate != null) armorTemplate.armorType = at;
            }

            return this;
        }

        public CannithItemTemplates applyPowerScaling(CannithStatPowerMap powerScaling) {
            this.values().forEach(
                    template -> template.applyPowerScaling(powerScaling)
            );

            return this;
        }

        public void nameChildren() {
            for(String itemType : new ArrayList<>(this.keySet())) {
                CannithItemTemplate template = this.get(itemType);
                template.name = itemType;
            }
        }

        public List<Item> generateItems() {
            ArrayList<Item> ret = new ArrayList<>();

            for(String itemType : new ArrayList<>(this.keySet())) {
                CannithItemTemplate template = this.get(itemType);
                ret.addAll(template.generateItems());
            }

            return ret;
        }

        public CannithItemTemplates filterRedundantOptions() {
            this.values().forEach(CannithItemTemplate::filterRedundantOptions);

            return this;
        }

        public CannithItemTemplates dropRuneArmsAndCollars() {
            this.remove("rune arm");
            this.remove("collar");

            return this;
        }
    }

    private static class CannithStatScaling extends HashMap<String, List<Object>> {
        public CannithStatPowerMap getStatPowerMap(int level) {
            CannithStatPowerMap ret = new CannithStatPowerMap();

            ret.level = level;
            for(String stat : this.keySet())
                ret.put(stat, this.get(stat, level));

            return ret;
        }

        private double get(String stat, int level) {
            List<Object> powers = super.get(stat);
            if(powers == null) return Double.NaN;

            Object power = powers.get(level);

            return parseDiceNotation(power);
        }

        private double parseDiceNotation(Object power) {
            if(power instanceof Double) return (double) power;
            String dice = (String) power;

            int count = Integer.parseInt(dice.split("d")[0]);
            int die   = Integer.parseInt(dice.split("d")[1]);

            return count * (1 + die) / 2.0;
        }
    }

    private static class CannithStatPowerMap extends HashMap<String, Double> {
        private int level;

        public boolean hasScaling(String stat) {
            if(! this.containsKey(stat)) return false;
            return ! this.get(stat).isNaN();
        }
    }

    private static class CannithItemTemplate {
        private List<String> slots;
        private Map<String, List<String>> options;

        private String name;
        private int level;
        private ArmorType armorType;

        public List<Item> generateItems() {
            PropertiesList itemTemplate = this.generateItemTemplate();
            List<List<String>> options = new ArrayList<>(this.options.values());

            return CraftedItemGenerator.generateItems(itemTemplate, options);
        }

        private PropertiesList generateItemTemplate() {
            PropertiesList ret = new PropertiesList();

            ret.put("name", "cannith crafted " + this.name + (this.armorType != null ? " (" + this.armorType.name.toLowerCase() + ")" : ""));
            ret.put("minimum level", String.valueOf(this.level));
            ret.put("slot", this.slots);

            return ret;
        }

        public void filterTo(Set<ItemSlot> includedItemSlots) {
            this.slots.removeIf(slot -> ! includedItemSlots.contains(ItemSlot.getSlot(slot)));
        }

        public void filterStats(StatFilter statsFilter) {
            for(String affixSlot : new ArrayList<>(this.options.keySet())) {
                List<String> affixes = this.options.get(affixSlot);

                affixes.removeIf(stat -> ! statsFilter.contains(Stat.parseStat(stat).category));

                if(affixes.size() == 0) this.options.remove(affixSlot);
            }
        }

        public void applyPowerScaling(CannithStatPowerMap powerScaling) {
            this.level = powerScaling.level;

            for(List<String> affixes : this.options.values()) {
                affixes.replaceAll(affix -> {
                    if(powerScaling.hasScaling(affix))
                        return affix + " +" + NumberFormat.maybeInteger(powerScaling.get(affix), 1);

                    return affix;
                });
            }
        }

        public void filterRedundantOptions() {
            for(List<String> affixes : this.options.values()) {
                if(affixes.contains("resistance")) {
                    affixes.remove("fortitude");
                    affixes.remove("reflex");
                    affixes.remove("will");
                }

                affixes.remove("regeneration"); // It's a fucking joke, even at level 1. 1 HP / min.
            }
        }
    }

    public static Collection<Item> generateList(LevelRange levelRange, Set<ItemSlot> includedItemSlots, Set<ArmorType> allowedArmorTypes, StatFilter statsFilter) { return generateList(levelRange.maximum, includedItemSlots, ArmorType.pickPreferredArmorType(allowedArmorTypes), statsFilter); }
    public static Collection<Item> generateList(int level, Set<ItemSlot> includedItemSlots, ArmorType preferredArmorType, StatFilter statsFilter) {
        CannithCraftingRecipeBook recipes = CannithCraftedItemSource.loadRecipeJSON();

        CannithStatPowerMap powerScaling = recipes.scaling.getStatPowerMap(level);
        recipes.options
                .dropRuneArmsAndCollars()
                .setArmorType(preferredArmorType)
                .filterSlots(includedItemSlots)
                .filterRedundantOptions()
                .applyPowerScaling(powerScaling)
                .filterStats(statsFilter)
                .nameChildren();

        return recipes.options.generateItems();
    }

    private static CannithCraftingRecipeBook loadRecipeJSON() {
        String json = file.Reader.getEntireFile(Settings.CANNITH_RECIPE_DEFINITIONS_JSON);
        Type type = new TypeToken<CannithCraftingRecipeBook>() {}.getType();
        return new Gson().fromJson(json, type);
    }
}
