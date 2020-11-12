package ddop.item;

import ddop.Settings;
import ddop.dto.LevelRange;
import ddop.optimizer.RandomAccessScoredItemList;
import ddop.optimizer.ScoredItemList;
import ddop.optimizer.valuation.ArmorType;
import ddop.optimizer.valuation.ValuationContext;
import ddop.stat.Stat;
import file.Directory;
import file.ItemReader;
import org.jetbrains.annotations.NotNull;
import util.Random;

import java.util.*;

public class ItemList implements Cloneable, Iterable<Item> {
	private static ItemList allNamedItems = null;
	private ArrayList<Item> items = new ArrayList<>();

	private ItemList() {}
	public ItemList(List<Item> items) {
		for(Item i : items) this.addItem(i);
	}

	public ItemList merge(Iterable<Item> another) {
		ItemList ret = this.clone();

		ret.addAll(another);

		return ret;
	}

	public static ItemList loadWikiitemsDirectory(String directoryPath) {
		ItemList ret = new ItemList();
		
		Set<String> fileNames = Directory.getContents(directoryPath);
		for(String filename : fileNames) {
			ret.addItem(ItemReader.loadFromWikiFile(directoryPath, filename));
		}
		
		return ret;
	}

	public static ItemList loadJsonFile(String... jsonFilePaths) {
		ItemList ret = new ItemList();

		for(String jsonFilePath : jsonFilePaths)
			ret.addJsonFile(jsonFilePath);

		return ret;
	}

	private void addJsonFile(String jsonFilePath) {
		List<Map<String, List<String>>> content = ItemReader.loadItemJsonFile(jsonFilePath);

		for(Map<String, List<String>> itemProps : content) {
			PropertiesList pl = new PropertiesList(itemProps);
			Item           i  = new Item(pl);
			this.addItem(i);
		}
	}

	private void addAll(Iterable<Item> items) { for(Item i : items) this.addItem(i); }
	private void addItem(Item i) {
		if(i == null) return;
		if(i.name == null) return;
		this.items.add(i);
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

	public ItemList filterBy(Set<ArmorType> allowedArmorTypes) {
		if(allowedArmorTypes == null || allowedArmorTypes.size() == 0) return this;

		ItemList ret = new ItemList();

		for(Item i : items)
			if(i.slot != ItemSlot.ARMOR || allowedArmorTypes.contains(i.armorType))
				ret.addItem(i);

		return ret;
	}

	public ItemList filterByLevel(LevelRange levelRange) {
		ItemList ret = new ItemList();

		for(Item i : this.items)
			if(levelRange.includes(i.minLevel))
				ret.addItem(i);
		
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
		Map<ItemSlot, ItemList>            rawItemMap = this.mapBySlot();
		Map<ItemSlot, RandomAccessScoredItemList> ret = new HashMap<>();
		
		for(ItemSlot slot : rawItemMap.keySet()) {
			ItemList itemList = rawItemMap.get(slot);

			ScoredItemList options = new ScoredItemList(itemList, vc);

			ret.put(slot, new RandomAccessScoredItemList(options));
		}
		
		return ret;
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

	public Item getNamedItem(String name) {
		List<Item> ret = this.getItem(name);

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

		ItemList items = ItemList.getAllNamedItems();
		for(String name : names)
			ret.add(items.getNamedItem(name));
		
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
		ItemList.allNamedItems = ItemList.loadJsonFile(Settings.ITEM_SOURCES_JSON);
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
	public @NotNull Iterator<Item> iterator() {
		return this.items.iterator();
	}

    public Set<String> getAllStatCategories() {
		Set<String> ret = new HashSet<>();

		for(Item i : this.items) {
			for(Stat s : i.getStats()) {
				ret.add(s.category);
			}
		}

		return ret;
    }
}
