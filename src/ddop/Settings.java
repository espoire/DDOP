package ddop;

public class Settings {
	public static final String PROJECT_ROOT = "X:\\src\\DDOP";

	public static final String WORK_DIRECTORY = PROJECT_ROOT + "\\DDOP Loader";
	public static final String SOURCE_DIRECTORY  = WORK_DIRECTORY + "\\src";
	public static final String OUTPUT_DIRECTORY  = WORK_DIRECTORY + "\\out";
	public static final String ITEM_DIRECTORY    = WORK_DIRECTORY + "\\out\\items";
	public static final String ITEM_JSON = PROJECT_ROOT + "\\itemDefinitions\\items.json";

	public static final boolean IGNORE_SET_BONUSES = false;
	public static final boolean IGNORE_DAMAGE_TYPES = true;
	public static final boolean IGNORE_RUNEARM_SHOT = true;
	public static final boolean IGNORE_CLICKIES = true;
	
	public static final boolean SHOW_FULL_STATLIST_DEBUG = false;
	public static final boolean SHOW_ITEM_FILEPATH_DEBUG = false;
    public static final boolean REAPER_BUILD_DEBUG = false;
}
