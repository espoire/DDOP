package ddop;

public class Settings {
	public static final String PROJECT_ROOT = "X:\\src\\DDOP";

	public static final String WORK_DIRECTORY    = PROJECT_ROOT + "\\DDOP Loader";
	public static final String JSON_DIRECTORY    = PROJECT_ROOT + "\\itemDefinitions";
	public static final String SOURCE_DIRECTORY  = WORK_DIRECTORY + "\\src";
	public static final String OUTPUT_DIRECTORY  = WORK_DIRECTORY + "\\out";
	public static final String WIKI_ITEMS_JSON                         = JSON_DIRECTORY + "\\wiki.json";
	public static final String MANUAL_ITEMS_JSON                       = JSON_DIRECTORY + "\\manual.json";
	public static final String NEARLY_FINISHED_BASE_ITEMS_JSON         = JSON_DIRECTORY + "\\nearlyFinishedBaseItems.json";
	public static final String NEARLY_FINISHED_RECIPE_DEFINITIONS_JSON = JSON_DIRECTORY + "\\nearlyFinishedRecipeDefinitions.json";
	public static final String NEARLY_FINISHED_COMPLETED_ITEMS_JSON    = JSON_DIRECTORY + "\\nearlyFinishedCompletedItems.json";

	public static final String[] ITEM_SOURCES_JSON = new String[] {
			WIKI_ITEMS_JSON,
			MANUAL_ITEMS_JSON,
			NEARLY_FINISHED_COMPLETED_ITEMS_JSON
	};

	public static final boolean IGNORE_SET_BONUSES = false;
	public static final boolean IGNORE_DAMAGE_TYPES = true;
	public static final boolean IGNORE_RUNEARM_SHOT = true;
	public static final boolean IGNORE_CLICKIES = true;
	
	public static final boolean SHOW_FULL_STATLIST_DEBUG = false;
	public static final boolean SHOW_ITEM_FILEPATH_DEBUG = false;
    public static final boolean REAPER_BUILD_DEBUG = false;
}
