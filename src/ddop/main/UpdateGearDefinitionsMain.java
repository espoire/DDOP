package ddop.main;

import ddop.item.sources.wiki.UpdateItemFiles;

import java.util.HashSet;
import java.util.Set;

public class UpdateGearDefinitionsMain {
    public static void main(String... s) {
        Set<String> reloadUpdates = new HashSet<>();
//        reloadUpdates.add("Update_45_named_items");
//        reloadUpdates.add("Update_46_named_items");
//        reloadUpdates.add("Update_47_named_items");
        
        UpdateItemFiles.update(reloadUpdates);
    }
}
