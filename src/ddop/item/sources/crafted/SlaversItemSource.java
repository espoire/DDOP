package ddop.item.sources.crafted;

import ddop.item.ItemSlot;
import ddop.stat.StatSource;

import java.util.Map;
import java.util.Set;

public class SlaversItemSource extends CraftedItemSource {
//    Set<ItemSlot> applicableSlots = new HashSet<ItemSlot>(new ItemSlot[] {
//        ItemSlot.NECK,
//        ItemSlot.TRINKET,
//        ItemSlot.WRIST,
//        ItemSlot.WAIST,
//        ItemSlot.FINGER,
//        ItemSlot.FEET
//    });
    @Override
    public boolean appliesTo(ItemSlot slot) {
        return false;
    }
    
    @Override
    protected Map<String, Set<StatSource>> getAffixLists() {
        return null;
    }
}
