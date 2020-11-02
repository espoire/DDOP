package ddop.item;

import java.util.Collection;
import java.util.HashSet;

public class SetItems {
    public static final Collection<SetItems> all = new HashSet<>();
    
    private final String name;
    private final Collection<Item> members;
    private final int bonusThreshold;
    
    private SetItems(String name, String[] itemNames, int bonusThreshold) {
        this.name = name;
        this.members = new HashSet<Item>();
        for(String itemName : itemNames) this.members.add(ItemList.getNamedItem(itemName));
        this.bonusThreshold = bonusThreshold;
    
        SetItems.all.add(this);
    }
    
    public static final SetItems LEGENDARY_FLAMECLEANSED_FURY =
            new SetItems("Legendary Flamecleansed Fury",
                    new String[] {
                            "Legendary Blessed Bulwark",
                            "Legendary Blessed Vestments",
                            "Legendary Hallowed Trail",
                            "Legendary Hallowed Castigators",
                            "Tattered Scrolls of the Broken One"
                    },
                    3
            );
}
