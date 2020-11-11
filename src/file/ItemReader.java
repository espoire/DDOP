package file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.item.Item;
import ddop.item.ItemSlot;
import ddop.item.PropertiesList;
import ddop.stat.Stat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemReader {
	public static List<Map<String, List<String>>> loadItemJsonFile(String jsonFilePath) {
		String json = file.Reader.getEntireFile(jsonFilePath);
		return ItemReader.loadItemJson(json);
	}

	public static List<Map<String, List<String>>> loadItemJson(String json) {
		Type listType = new TypeToken<List<Map<String, List<String>>>>() {}.getType();
		return new Gson().fromJson(json, listType);
	}

	public static Item loadFromWikiHTMLFile(String folder, String infile) {
		String filepath = folder + infile;
		String html = file.Reader.getEntireFile(filepath);
		String wikiItemSummary = cleanupHTML(html);
		
		Item ret = loadFromWikiSummary(wikiItemSummary, filepath);
		
		if(ret == null) {
			System.out.println("Not equipment, or wiki format not supported: " + infile);
		}
		
		return ret;
	}
	
	private static final String WIKI_HEADER_REGEX              = "(?s)^.*?<div id=\"mw-content-text\"";
	private static final String HTML_HIDDEN_SPAN_REGEX         = "<span style=\"display: none;\">.*?</span>";
	private static final String WIKI_TOOLTIP_SPAN_REGEX        = "<span[^>]*?class=\"tooltip\".*";
	private static final String HTML_TAG_REGEX                 = "<[^>]*>";
	private static final String WIKI_ITEM_IF_CLAUSE_REGEX      = " [iI]f .*";
	private static final String CARRIAGE_RETURN_REGEX          = "\r";
	private static final String HTML_SPECIAL_SPACE_REGEX       = "&#160;|&#nbsp;|&#32;";
	private static final String HTML_SPECIAL_APOSTROPHE_REGEX  = "&#39;|â€™";
	private static final String BLANK_LINE_REGEX               = "(?s)\\n\\s*\\n";
	private static final String PADDING_REGEX                  = "(?s)\\s*\\n\\s*";
	private static final String BLANK_LINES_AT_ENDS_REGEX      = "(?s)(^\\n+)|(\\n+$)";
	public static String cleanupHTML(String html) {
		if(html == null) return null;
		
		html = html.replaceAll(WIKI_HEADER_REGEX,         "<div");
		
		int tableEnd = html.lastIndexOf("</table>");
		if(tableEnd < 0) return "";
		html = html.substring(0, tableEnd);
		
		String plainText = html
			.replaceAll(HTML_HIDDEN_SPAN_REGEX,           "")
			.replaceAll(WIKI_TOOLTIP_SPAN_REGEX,          "")
			.replaceAll(HTML_TAG_REGEX,                   "")
			.replaceAll(WIKI_ITEM_IF_CLAUSE_REGEX,        "")
			.replaceAll(CARRIAGE_RETURN_REGEX,            "")
			.replaceAll(HTML_SPECIAL_SPACE_REGEX,         " ")
			.replaceAll(HTML_SPECIAL_APOSTROPHE_REGEX,    "'")
			.replaceAll(BLANK_LINE_REGEX,                 "\n")
			.replaceAll(PADDING_REGEX,                    "\n")
			.replaceAll(BLANK_LINES_AT_ENDS_REGEX,        "");
		
		return plainText;
	}
	
	public static Item loadFromWikiFile(String directory, String filename) {
		return ItemReader.loadFromWikiFile(directory + "\\" + filename);
	}
	
	private static Item loadFromWikiFile(String filepath) {
		String summary = Reader.getEntireFile(filepath);
		return ItemReader.loadFromWikiSummary(summary, filepath);
	}

	public static Item loadFromWikiSummary(String summary, String filepath) {
		return loadFromWikiSummary(summary.split("\n"), filepath);
	}
	
	private static Map<String, Integer> KEYS;
	private static Map<String, Integer> generateKeysMap() {
		Map<String, Integer> ret = new HashMap<>();

		ret.put("name", 1);
		ret.put("damage", 1);
		ret.put("damage and type", 1);
		ret.put("critical roll", 1);
		ret.put("critical threat range", 1);
		ret.put("weapon type", 1);
		ret.put("slot", ItemSlot.getAll().length);
		ret.put("minimum level", 1);
		ret.put("handedness", 1);
		ret.put("attack mod", 1);
		ret.put("damage mod", 1);
		ret.put("made from", 1);
		ret.put("material", 1);
		ret.put("location", 1);
		ret.put("enchantments", Integer.MAX_VALUE);
		ret.put("enhancements", Integer.MAX_VALUE);
		ret.put("upgradeable?", 1);
		ret.put("item type", 1);
		ret.put("armor type", 1);
		ret.put("feat requirement", 1);
		ret.put("required trait", 1);
		ret.put("race absolutely excluded", 1);
		ret.put("armor bonus", 2);
		ret.put("maximum dexterity bonus", 1);
		ret.put("max dex bonus", 1);
		ret.put("armor check penalty", 1);
		ret.put("arcane spell failure", 1);
		ret.put("shield type", 1);
		ret.put("proficiency class", 1);
		ret.put("proficiency", 1);
		ret.put("shield bonus", 1);
		ret.put("damage reduction", 1);
		ret.put("accepts sentience?", 1);

		ret.put("unique item capacity", 0);
		ret.put("max stack size", 0);
		ret.put("race absolutely required", 0);
		ret.put("use magical device dc", 0);
		ret.put("notes", 0);
		ret.put("description", 0);
		ret.put("tips", 0);
		ret.put("hardness", 0);
		ret.put("base value", 0);
		ret.put("weight", 0);
		ret.put("binding", 0);
		ret.put("durability", 0);

		ret.put("this item cannot be fed to sentient jewels.", 0);
		ret.put("this item currently cannot be fed to sentient jewels and probably should be able to.", 0);

		return ret;
	}

	private static Item loadFromWikiSummary(String[] lines, String filepath) {
		if(ItemReader.KEYS == null) ItemReader.KEYS = generateKeysMap();
		PropertiesList wikiProperties = new PropertiesList();
		
		String key = "name";
		List<String> values = new ArrayList<>();
		
		for(String line : lines) {
			line = line.toLowerCase();
			
			if(util.Array.contains(KEYS.keySet(), line)) {
				if(shouldDrop(key, values)) return null;
				if(shouldSave(key, values)) {
					if(values.size() > KEYS.get(key))
						values = values.subList(0, KEYS.get(key));
					wikiProperties.put(key, values);
				}
				
				if(KEYS.get(line) > 0) {
					key = line;
				} else {
					key = null;
				}

				values = new ArrayList<>();
			} else {
				values.add(line);
			}
		}

		if(shouldSave(key, values)) {
			if(values.size() > KEYS.get(key))
				values = values.subList(0, KEYS.get(key));
			wikiProperties.put(key, values);
		}
		
		wikiProperties = cleanup(wikiProperties);
		if(wikiProperties == null) return null;
		
		return new Item(wikiProperties);
	}

	private static boolean shouldSave(String key, List<String> values) {
		if(key == null || key.equals("null")) return false;
		if(values.size() == 0) return false;

		if(key.equals("attack mod") && values.get(0).equals("str")) return false;
		if(key.equals("damage mod") && values.get(0).equals("str")) return false;
		if(key.equals("upgradeable?") && values.get(0).equals("not upgradeable")) return false;
		if(key.equals("upgradeable?") && values.get(0).equals("fully upgraded")) return false;
		if(key.equals("feat requirement") && values.get(0).equals("cloth armor proficiency")) return false;
		if(key.equals("required trait") && values.get(0).equals("none")) return false;
		if(key.equals("maximum dexterity bonus") && values.get(0).equals("none")) return false;
		if(key.equals("maximum dex bonus") && values.get(0).equals("none")) return false;
		if(key.equals("armor check penalty") && values.get(0).equals("0")) return false;
		if(key.equals("arcane spell failure") && values.get(0).equals("0")) return false;
		if(key.equals("arcane spell failure") && values.get(0).equals("0%")) return false;
		if(key.equals("proficiency class") && values.get(0).equals("simple weapon proficiency")) return false;
		if(key.equals("shield bonus") && values.get(0).equals("0")) return false;
		if(key.equals("shield bonus") && values.get(0).equals("+0")) return false;
		if(key.equals("damage reduction") && values.get(0).equals("none")) return false;
		if(key.equals("material") && values.get(0).equals("none")) return false;
		if(key.equals("race absolutely excluded") && values.get(0).equals("none")) return false;

		return true;
	}

	private static boolean shouldDrop(String key, List<String> values) {
		if(key == null) return false;

		if(key.equals("feat requirement") && values.get(0).equals("cosmetic armor proficiency")) return true;
		if(key.equals("shield type") && values.get(0).equals("cosmetic shield")) return true;

		return false;
	}

	private static final String[] RANDOM_MODS_REGEX = new String[] {"mythic (weapon|armor|belt|hands|ring|trinket|goggle|boot|wrist|neck|cloak|head) boost.*"};
	private static final String[] CLICKIES_REGEX = new String[] {"[^,]+,.*(charge).*"};
	private static final String[] FLAVOR_ENCHANTMENTS = new String[] {
			"the moral compass",
			"echoes of 2006: take a trip into the past!",
			"beyond the soul of flame",
			"beyond the soul of stone",
			"beyond the soul of tide",
			"beyond the soul of storm",
	};
	private static final String[] DAMAGE_TYPE_MODS = new String[] {
			"blunted ammunition",
			"cold iron",
			"silver",
			"feat: pierce silver",
			"returning",
			"tira's splendor", // Silver
			"metalline",
			
	};
	private static final String[] RUNE_ARM_SHOT_REGEX = new String[] {
			"maximum charge tier:.*",
			"corrupted exploding fire shot",
			"occasional overcooling",
			"corrupted fire blast",
			
	};
	private static final String[] UNIMPLEMENTED_META_MODS = new String[] {
			"off hand:",
			"main hand:",
			"either hand:",
			"better offhanded: while this item is in your offhand, it gains +2[w].",
			"trace of madness",
	};
	
	@SuppressWarnings("unused")
	private static PropertiesList cleanup(PropertiesList pl) {
		List<String> mods = pl.get("enchantments");
		if(mods == null) {
			mods = pl.get("enhancements");
			if(mods == null) return null;
		}
		
		for(String mod : mods.toArray(new String[mods.size()])) {
            if(mod.startsWith("* ")) {
            	mods.remove(mod);
            	mod = mod.substring(2);
            	mods.add(mod);
            }
			
			if(util.Array.matches (RANDOM_MODS_REGEX,       mod) ||
			   util.Array.contains(FLAVOR_ENCHANTMENTS,     mod) ||
			   util.Array.contains(UNIMPLEMENTED_META_MODS, mod) ||
				(Settings.IGNORE_SET_BONUSES  && util.Array.contains(Stat.SET_BONUSES, mod)) ||
				(Settings.IGNORE_DAMAGE_TYPES && util.Array.contains(DAMAGE_TYPE_MODS,      mod)) ||
				(Settings.IGNORE_CLICKIES     && util.Array.matches (CLICKIES_REGEX,        mod)) ||
				(Settings.IGNORE_RUNEARM_SHOT && util.Array.matches (RUNE_ARM_SHOT_REGEX,   mod)) ) {
				mods.remove(mod);
			}
		}
		
		pl.put("enchantments", mods);
		
		return pl;
	}
}
