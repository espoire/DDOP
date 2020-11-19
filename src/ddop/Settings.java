package ddop;

import ddop.dto.LevelRange;
import ddop.item.ItemSlot;

public class Settings {
	public static final String PROJECT_ROOT = "X:\\src\\DDOP";

	public static final String REFINEMENT_OUTPUT_DIRECTORY = PROJECT_ROOT + "\\logs";
	public static final String WORK_DIRECTORY    = PROJECT_ROOT + "\\DDOP Loader";
	public static final String SOURCE_DIRECTORY  = WORK_DIRECTORY + "\\src";
	public static final String OUTPUT_DIRECTORY  = WORK_DIRECTORY + "\\out";
	public static final String JSON_DIRECTORY    = PROJECT_ROOT + "\\json";

	public static final String WIKI_ITEMS_JSON                         = JSON_DIRECTORY + "\\wiki\\items.json";
	public static final String MANUAL_ITEMS_JSON                       = JSON_DIRECTORY + "\\manual.json";
	public static final String SET_BONUS_DEFINITIONS_JSON_DIRECTORY    = JSON_DIRECTORY + "\\setBonus";

	public static final String NEARLY_FINISHED_JSON_DIRECTORY          = JSON_DIRECTORY + "\\nearlyFinished";
	public static final String NEARLY_FINISHED_BASE_ITEMS_JSON         = NEARLY_FINISHED_JSON_DIRECTORY + "\\baseItems.json";
	public static final String NEARLY_FINISHED_COMPLETED_ITEMS_JSON    = NEARLY_FINISHED_JSON_DIRECTORY + "\\completedItems.json";
	public static final String NEARLY_FINISHED_RECIPE_DEFINITIONS_JSON = NEARLY_FINISHED_JSON_DIRECTORY + "\\recipeDefinitions.json";

	public static final String SLAVERS_RECIPE_DEFINITIONS_JSON         = JSON_DIRECTORY + "\\slavers\\recipeDefinitions.json";
	public static final String CANNITH_RECIPE_DEFINITIONS_JSON         = JSON_DIRECTORY + "\\cannith\\recipeDefinitions.json";

	public static final String[] ITEM_SOURCES_JSON = new String[] {
			WIKI_ITEMS_JSON,
			MANUAL_ITEMS_JSON,
			NEARLY_FINISHED_COMPLETED_ITEMS_JSON
	};

	public static final boolean IGNORE_SET_BONUSES = false;
	public static final boolean IGNORE_DAMAGE_TYPES = true;
	public static final boolean IGNORE_RUNEARM_SHOT = true;
	public static final boolean IGNORE_CLICKIES = true;
	
	public static final boolean DEBUG_SHOW_FULL_STATLIST = false;
    public static final boolean DEBUG_REAPER_BUILD = false;

    public static final int LEVEL_CAP = 30;

	public static final ItemSlot[] IGNORED_SLOTS = new ItemSlot[] {
			ItemSlot.MAIN_HAND, ItemSlot.OFF_HAND,
			ItemSlot.QUIVER
	};

    private static final int TARGET_ITEMS_MIN_LEVEL = 26, TARGET_ITEMS_MAX_LEVEL = 30;
	public static final LevelRange TARGET_ITEMS_LEVEL_RANGE = new LevelRange(TARGET_ITEMS_MIN_LEVEL, TARGET_ITEMS_MAX_LEVEL);
}
