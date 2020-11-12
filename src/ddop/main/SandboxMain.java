package ddop.main;

import ddop.Settings;
import ddop.dto.LevelRange;
import ddop.item.Item;
import ddop.item.ItemList;
import ddop.item.loadout.EquipmentLoadout;
import ddop.item.sources.crafted.SlaversCraftedItemSource;
import ddop.optimizer.valuation.ShintaoScorer;
import ddop.optimizer.valuation.StatScorer;
import ddop.stat.conversions.NamedStat;
import ddop.stat.conversions.SetBonus;
import file.CompileHTML;
import file.WgetScripter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

@SuppressWarnings("all")
public class SandboxMain {
    public static void main(String... s) {
        Collection<Item> items = SlaversCraftedItemSource.generateList(new LevelRange(1, 30), new ShintaoScorer(30).getQueriedStatCategories());

        if(items != null) for(Item i : items)
            System.out.println(i);
    }

    private static void printAllUnvaluedStats(StatScorer scorer) {
        System.out.println("The following stats are not valued by the provided StatScorer implementation:");
        System.out.println();

        Set<String> valued = scorer.getQueriedStatCategories();
        ItemList items = ItemList.getAllNamedItems();
        Set<String> categories = items.getAllStatCategories();
        categories.removeIf(s -> {
            if(valued.contains(s)) return true;
            if(NamedStat.isNamed(s)) return true;
            if(SetBonus.isSetBonus(s)) return true;
            return false;
        });

        for(String s : categories) System.out.println(s);
    }

    private static void printItemFromHTML() {
        Item i = CompileHTML.loadItemFromHTML(Settings.OUTPUT_DIRECTORY, "I%3ALegendary_Collective_Sight.html");
        System.out.println(i);
    }

    private static void printItemSetStats() {
        EquipmentLoadout el = new EquipmentLoadout();

        el.put(
                "mantle of escher",
                "van richten's spectacles",
                "legendary corroded iron figurine",
                "legendary drow sage's cowl",
                "legendary omniscience, legendary tumbleweed",
                "legendary cloak of the city's champion",
                "legendary levik's bracers",
                "doctor leroux's curious implant",
                "epic purging the pantheon",
                "azure guard",
                "molten silver gauntlets",
                "quiver of alacrity"
        );

        el.printItemNamesToConsole();
        el.printStatTotalsToConsole();
    }

    private static void customWgetScript() {

        String[] missingItems = new String[]{
                "https://ddowiki.com/page/I:The_Cornerstone_Champion",
                "https://ddowiki.com/page/I:Silver_Dragonscale_Capelet",
                "https://ddowiki.com/page/I:Silver_Dragonscale_Helmet",
                "https://ddowiki.com/page/I:Wildwood_Wrists",
                "https://ddowiki.com/page/I:Hoarfrost,_Herald_of_the_Bitter_Ice",
                "https://ddowiki.com/page/I:The_Hallowed_Splinters",
                "https://ddowiki.com/page/I:The_Labrythine_Edge",
                "https://ddowiki.com/page/I:The_Fractured_Elegance",
                "https://ddowiki.com/page/I:Untold,_Crack_in_the_Sky",
                "https://ddowiki.com/page/I:The_Everstorm,_Maelstrom_Courser",
                "https://ddowiki.com/page/I:The_Wide_Open_Sky",
                "https://ddowiki.com/page/I:Stickerclick,_the_Bitter_Hail_of_Bolts",
                "https://ddowiki.com/page/I:The_Shattered_Hilt_of_Constellation",
                "https://ddowiki.com/page/I:The_Broken_Blade_of_Constellation",
                "https://ddowiki.com/page/I:Baz'Morath,_the_Curator_of_Decay",
                "https://ddowiki.com/page/I:Legendary_Concentrated_Chaos"
        };

        String script = WgetScripter.generateWgetScript(Arrays.asList(missingItems));

        System.out.println(script);
    }
}
