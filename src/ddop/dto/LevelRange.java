package ddop.dto;

public class LevelRange {
    public final int minimum, maximum;

    public LevelRange(int maximum) { this(0, maximum); }
    public LevelRange(int minimum, int maximum) {
        if(maximum < minimum) throw new RuntimeException("Attempted to create an inverted LevelRange [" + minimum + " .. " + maximum + "]");

        this.minimum = minimum;
        this.maximum = maximum;
    }

    public boolean includes(int level) {
        return this.minimum <= level && level <= this.maximum;
    }
}
