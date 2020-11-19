package ddop.item;

import java.util.*;

public class ItemSlot {
	public static final ItemSlot
			ARMOR = new ItemSlot("Armor"),
			BACK = new ItemSlot("Back"),
			EYE = new ItemSlot("Eye").alias("Eyes"),
			FINGER = new ItemSlot("Finger", 2),
			FEET = new ItemSlot("Feet"),
			HAND = new ItemSlot("Hand").alias("Hands"),
			HEAD = new ItemSlot("Head"),
			MAIN_HAND = new ItemSlot("Main Hand"),
			NECK = new ItemSlot("Neck"),
			OFF_HAND = new ItemSlot("Off Hand"),
			QUIVER = new ItemSlot("Quiver"),
			TRINKET = new ItemSlot("Trinket"),
			WAIST = new ItemSlot("Waist"),
			WRIST = new ItemSlot("Wrist");

	public static ItemSlot[] getAll() { return new ItemSlot[] {HEAD, NECK, EYE, ARMOR, WRIST, TRINKET, BACK, WAIST, FINGER, FEET, HAND, MAIN_HAND, OFF_HAND, QUIVER}; }
	private static final HashMap<String, ItemSlot> byName = new HashMap<>();




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

	public static ItemSlot getSlot(String s) {
		return ItemSlot.byName.get(s);
	}

	public static Set<ItemSlot> getUnskippedSlots(List<ItemSlot> skipSlots) {
		List<ItemSlot> working = new ArrayList<>(Arrays.asList(ItemSlot.getAll()));
		working.add(ItemSlot.FINGER); // Needs to be "skipped" twice.

		for(ItemSlot toRemove : skipSlots)
			working.remove(toRemove);

		return new HashSet<>(working);
	}

	public static Set<ItemSlot> getSlots(PropertiesList pl) {
		if(pl.containsKey("weapon type")) return Collections.singleton(ItemSlot.MAIN_HAND);
		if(pl.containsKey("armor type")) return Collections.singleton(ItemSlot.ARMOR);
		if(pl.containsKey("shield type")) return Collections.singleton(ItemSlot.OFF_HAND);
		if(pl.containsKey("item type"))
			if(pl.getFirst("item type").matches("^quiver( / ([a-z]+ )?quiver)?$"))
				return Collections.singleton(ItemSlot.QUIVER);
		if(pl.containsKey("required trait"))
			if(pl.getFirst("required trait") != null)
				if(pl.getFirst("required trait").equals("artificer rune arm use"))
					return Collections.singleton(ItemSlot.OFF_HAND);
		
		List<String> slots = pl.get("Slot");
		Set<ItemSlot> ret = new HashSet<>();
		boolean isCosmetic = false;
		if(slots != null) for(String s : slots) {
			if(s.contains("cosmetic")) {
				isCosmetic = true;
				continue;
			}

			ItemSlot slot = ItemSlot.getSlot(s);
			if(slot != null) {
				ret.add(slot);
			} else {
				System.err.println("Unrecognised item slot: " + slot);
			}
		}

		if(ret.size() > 0) return ret;
		if(isCosmetic) return null;

		System.err.println("Slot not found for " + pl.toString("Name"));
		return null;
	}
}
