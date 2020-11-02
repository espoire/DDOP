package file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddop.Settings;
import ddop.item.Item;
import ddop.item.PropertiesList;
import ddop.stat.Stat;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
	
	private static final String[] KEYS = new String[] {
			"proficiency class",
			"damage and type",
			"critical threat range",
			"weapon type",
			"slot",
			"race absolutely required",
			"minimum level",
			"required trait",
			"use magical device dc",
			"handedness",
			"attack mod",
			"damage mod",
			"binding",
			"durability",
			"made from",
			"hardness",
			"base value",
			"weight",
			"location",
			"enchantments",
			"upgradeable?",
			"description",
			"tips",
			"item type",
			"material",
			"armor type",
			"feat requirement",
			"race absolutely excluded",
			"armor bonus",
			"maximum dexterity Bonus",
			"armor check penalty",
			"arcane spell failure",
			"notes",
			"shield type",
			"proficiency",
			"shield bonus",
			"max dex bonus",
			"damage reduction",
			"damage",
			"critical roll",
			"enhancements",
			"this item cannot be fed to sentient jewels.",
			"unique item capacity",
			"max stack size",
			"this item currently cannot be fed to sentient jewels and probably should be able to.",
	};

	private static Item loadFromWikiSummary(String[] lines, String filepath) {
		PropertiesList wikiProperties = new PropertiesList();
		
		String key = "name";
		ArrayList<String> values = new ArrayList<>();
		
		for(String line : lines) {
			line = line.toLowerCase();
			
			if(util.Array.contains(KEYS, line)) {
				wikiProperties.put(key, values);
				
				key = line;
				values = new ArrayList<String>();
			} else {
				values.add(line);
			}
		}
		wikiProperties.put(key, values);
		
		wikiProperties = cleanup(wikiProperties);
		if(wikiProperties == null) return null;
		
		return new Item(wikiProperties);
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
