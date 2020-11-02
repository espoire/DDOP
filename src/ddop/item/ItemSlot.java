package ddop.item;

import java.util.HashMap;

public class ItemSlot {
	private static final HashMap<String, ItemSlot> byName = new HashMap<String, ItemSlot>();
	
	public final String name;
	public final int limit; // 1 for most item types, 2 for Finger.
	
	private ItemSlot(String name) {
		this(name, 1);
	}

	private ItemSlot(String name, int limit) {
		this.name = name;
		this.limit = limit;
		
		ItemSlot.byName.put(name.toLowerCase(), this);
	}

	private ItemSlot alias(String string) {
		ItemSlot.byName.put(string.toLowerCase(), this);
		return this;
	}
	
	public static final ItemSlot
		HEAD = new ItemSlot("Head"),
		NECK = new ItemSlot("Neck"),
		EYE = new ItemSlot("Eye").alias("Eyes"),
		ARMOR = new ItemSlot("Armor"),
		WRIST = new ItemSlot("Wrist"),
		TRINKET = new ItemSlot("Trinket"),
		BACK = new ItemSlot("Back"),
		WAIST = new ItemSlot("Waist"),
		FINGER = new ItemSlot("Finger", 2),
		FEET = new ItemSlot("Feet"),
		HAND = new ItemSlot("Hand").alias("Hands"),
		MAIN_HAND = new ItemSlot("Main Hand"),
		OFF_HAND = new ItemSlot("Off Hand"),
		QUIVER = new ItemSlot("Quiver");
	
	public static ItemSlot[] getAll() { return new ItemSlot[] {HEAD, NECK, EYE, ARMOR, WRIST, TRINKET, BACK, WAIST, FINGER, FEET, HAND, MAIN_HAND, OFF_HAND, QUIVER}; }

	public static ItemSlot getSlot(PropertiesList pl) {
		if(pl.containsKey("weapon type")) return ItemSlot.MAIN_HAND;
		if(pl.containsKey("armor type")) return ItemSlot.ARMOR;
		if(pl.containsKey("shield type")) return ItemSlot.OFF_HAND;
		if(pl.containsKey("required trait") && pl.getFirst("required trait") != null && pl.getFirst("required trait").equals("artificer rune arm use")) return ItemSlot.OFF_HAND;
		
		String slot = pl.getFirst("Slot");
		if(slot != null) {
			ItemSlot ret = ItemSlot.byName.get(slot);
			if(ret != null) return ret;
			System.err.println("Unrecognised item slot: " + slot);
		}
		
		if(pl.containsKey("item type") && pl.getFirst("item type").matches("^quiver( / ([a-z]+ )?quiver)?$")) return ItemSlot.QUIVER;
		
		System.err.println("Slot not found for " + pl.toString("Name"));
		return null;
	}
}
