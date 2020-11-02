package ddop.item.loadout;

import ddop.item.ItemList;

public class NamedLoadout extends EquipmentLoadout {
    public String name;
    public NamedLoadout(String name, String[] items) {
        super(ItemList.toNamedItems(items));
        this.name = name;
    }
    
    @Override
    protected String getTagLine() {
        return this.name + " (" + this.size() + " items)";
    }
}
