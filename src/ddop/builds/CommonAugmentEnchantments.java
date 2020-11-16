package ddop.builds;

import ddop.stat.Stat;
import ddop.stat.conversions.NamedStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommonAugmentEnchantments {
    private static final List<String> enchantments = new ArrayList<String>(Arrays.asList(
            "well-rounded +12",
            "insightful well-rounded +5",
            "profane well-rounded +2",
            "exceptional well-rounded +1",
            "striding +30%",
            "false life +48",
            "vitality +20",
            "sheltering +32",
            "fortification +100%",
            "elemental resistance +30",
            "resistance +10",
            "greater heroism",
            "good luck +2",
            "blindness immunity",
            "fear immunity",
            "deathblock",
            "all skills except umd +19",
            "accuracy +20",
            "deadly +10",
            "ghost touch",
            "melee alacrity +15%",
            "ranged alacrity +15%",
            "potency +139",
            "enhancement spell focus mastery +2",
            "enhanced ki +1"
    ));

    private static Collection<Stat> stats;
    public static Collection<Stat> getStats() {
        if(stats == null) {
            stats = new ArrayList<>();
            for(String enchantment : enchantments)
                stats.addAll(NamedStat.convert(Stat.parseStat(enchantment)));
        }

        return stats;
    }
}
