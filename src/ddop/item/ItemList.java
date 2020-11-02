package ddop.item;

import ddop.Settings;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.optimizer.ScoredItemList;
import ddop.optimizer.valuation.ArmorType;
import ddop.optimizer.valuation.ValuationContext;
import file.Directory;
import file.ItemReader;
import util.Random;

import java.util.*;

public class ItemList implements Cloneable, Iterable<Item> {
	private static ItemList allNamedItems = null;
	private ArrayList<Item> items = new ArrayList<>();

	private ItemList() {}
	public ItemList(List<Item> items) {
		for(Item i : items) this.addItem(i);
	}

	public static ItemList loadWikiitemsDirectory(String directoryPath) {
		ItemList ret = new ItemList();
		
		Set<String> fileNames = Directory.getContents(directoryPath);
		for(String filename : fileNames) {
			ret.addItem(ItemReader.loadFromWikiFile(directoryPath, filename));
		}
		
		return ret;
	}

	private static ItemList loadJsonFile(String jsonFilePath) {
		List<Map<String, List<String>>> content = ItemReader.loadItemJsonFile(jsonFilePath);
		ItemList ret = new ItemList();

		for(Map<String, List<String>> itemProps : content) {
			PropertiesList pl = new PropertiesList(itemProps);
			Item i = new Item(pl);
			ret.addItem(i);
		}

		return ret;
	}

	private void addItem(Item i) {
		if(i != null && i.name != null) this.items.add(i);
	}
	
	public ItemList filterBy(ItemSlot slot) {
		if(slot == null) return this.clone();
		
		ItemList ret = new ItemList();
		
		for(Item i : items) {
			if(i.slot == slot) ret.addItem(i);
		}
		
		return ret;
	}
	
	public ItemList filterBy(ArmorType armorType) {
		if(armorType == null) return this;
		
		ItemList ret = new ItemList();
		
		for(Item i : items) {
			if(i.slot == ItemSlot.ARMOR && i.armorType == armorType) ret.addItem(i);
		}
		
		return ret;
	}
	
	public ItemList filterByLevel(int maximum) { return this.filterByLevel(0, maximum); }
	public ItemList filterByLevel(int minimum, int maximum) {
		if(maximum < 0) maximum = Integer.MAX_VALUE;
		ItemList ret = new ItemList();
		
		for(Item i : this.items) {
			if(minimum <= i.minLevel && i.minLevel <= maximum) ret.addItem(i);
		}
		
		return ret;
	}
	
	public Map<ItemSlot, ItemList> mapBySlot() {
		Map<ItemSlot, ItemList> ret = new LinkedHashMap<>();
		for(ItemSlot slot : ItemSlot.getAll()) ret.put(slot, this.filterBy(slot));
		return ret;
	}
	
	public Map<ArmorType, ItemList> mapByArmorType() {
		Map<ArmorType, ItemList> ret = new LinkedHashMap<>();
		for(ArmorType type : ArmorType.all) ret.put(type, this.filterBy(type));
		return ret;
	}
	
	public Map<ItemSlot, RandomAccessScoredItemList> toScoredMapBySlot(ValuationContext vc) {
		Map<ItemSlot, ItemList>       rawItemMap = ItemList.getAllNamedItems().filterByLevel(24, -1).mapBySlot();
		Map<ItemSlot, RandomAccessScoredItemList> itemMap    = new HashMap<>();
		
		for(ItemSlot slot : rawItemMap.keySet()) {
			ItemList itemList = rawItemMap.get(slot);
			ScoredItemList options = new ScoredItemList(itemList, vc);
			itemMap.put(slot, new RandomAccessScoredItemList(options));
		}
		
		return itemMap;
	}
	
	@SuppressWarnings("unchecked")
	public ItemList clone() {
		ItemList ret = null;
		try {
			ret = (ItemList) super.clone();
			ret.items = (ArrayList<Item>) ret.items.clone();
		} catch (Exception ignored) {}
		return ret;
	}
	
	public Item getRandom() {
		return (Item) Random.random(this.items);
	}
	
	public static Item getNamedItem(String name) {
		List<Item> ret = ItemList.getAllNamedItems().getItem(name);

		if(ret.size() != 1) {
			System.err.println("Found " + ret.size() + " named items called \"" + name + "\"...");
			return null;
		}

		return ret.get(0);
	}
	private List<Item> getItem(String name) {
		List<Item> ret = new ArrayList<>();
		
		name = name.toLowerCase();
		for(Item i : this.items) {
			if(i.name.equals(name)) ret.add(i);
		}
		
		if(ret.size() == 0) System.out.println("Item not found: " + name);
		
		return ret;
	}
	
	public static Collection<Item> toNamedItems(String[] names) {
		Collection<Item> ret = new ArrayList<>();
		
		for(String name : names) {
			ret.add(ItemList.getNamedItem(name));
		}
		
		return ret;
	}

	public Collection<Item> filterBy() {
		return new ArrayList<>(this.items);
	}
	
	public static ItemList getAllNamedItems() {
		if(ItemList.allNamedItems == null) ItemList.populateAllNamedItemsList();
		return ItemList.allNamedItems.clone();
	}

	private static void populateAllNamedItemsList() {
		ItemList.allNamedItems = ItemList.loadJsonFile(Settings.ITEM_JSON);
	}

	public String toString() {
		StringBuilder ret = new StringBuilder("ItemList:");
		for(Item i : this.items) ret.append("\n+- ").append(i.name);
		return ret.toString();
	}

	public String toJson() {
		StringBuilder json = new StringBuilder("[");

		boolean isFirst = true;
		for(Item i : this.items) {
			if(i == null) continue;

			if(!isFirst) json.append(",");
			isFirst = false;

			json.append(i.toJson());
		}

		json.append("]");

		return json.toString();
	}
	
	public int size() {
		return this.items.size();
	}
	
	@Override
	public Iterator<Item> iterator() {
		return this.items.iterator();
	}
}
