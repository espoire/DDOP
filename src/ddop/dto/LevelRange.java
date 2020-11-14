package ddop.dto;

import ddop.Settings;

public class LevelRange {
    public static final LevelRange ANY = new LevelRange(0, Settings.LEVEL_CAP);

    public final int minimum, maximum;

    public LevelRange(int minimum, int maximum) {
        if(maximum < minimum) throw new RuntimeException("Attempted to create an inverted LevelRange [" + minimum + " .. " + maximum + "]");

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public boolean includes(int level) {
        return this.minimum <= level && level <= this.maximum;
    }
}
