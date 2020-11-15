package ddop.stat;

import ddop.stat.conversions.NamedStat;
import ddop.stat.conversions.SetBonus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/** Implements a Stat whitelist. */
public class StatFilter extends HashSet<String> {
    /** Appropriate for raw enchantment strings, such as matching "Enhanced Speed XIV" to "melee alacrity". */
    public boolean containsEnchantment(String enchantment) {
        Stat stat = Stat.parseStat(enchantment);
        List<Stat> descendants = new ArrayList<>(Collections.singletonList(stat));

        descendants.addAll(SetBonus.getDescendants(descendants));
        descendants = NamedStat.convertAll(descendants);

        for(Stat s : descendants)
            if(super.contains(s.category))
                return true;

        return false;
    }

//    Inherited from superclass. Appropriate for already-processed Stat categories.
//    public boolean contains(String category) {
//        return super.contains(category);
//    }
}
