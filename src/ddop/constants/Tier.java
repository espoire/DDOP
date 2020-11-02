package ddop.constants;

public enum Tier {
    Heroic,
    Paragon,
    Epic;

    public static Tier getTierByLevel(int level) {
        if(level <= 10) return Heroic;
        if(level <= 20) return Paragon;
        return Epic;
    }

    public int getRank() {
        switch (this) {
            case Heroic:
                return 1;
            case Paragon:
                return 2;
            case Epic:
                return 3;
            default:
                throw new RuntimeException("Unrecognised Tier type has no rank implemented.");
        }
    }
}
