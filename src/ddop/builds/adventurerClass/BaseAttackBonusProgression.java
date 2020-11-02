package ddop.builds.adventurerClass;

public class BaseAttackBonusProgression {
    public static final BaseAttackBonusProgression
            LOW    = new BaseAttackBonusProgression("Low",    0.5),
            MEDIUM = new BaseAttackBonusProgression("Medium", 0.75),
            HIGH   = new BaseAttackBonusProgression("High",   1.0);
    
    public  final String name;
    private final double progressionRate;
    
    private BaseAttackBonusProgression(String name, double progressionRate) {
        this.name = name;
        this.progressionRate = progressionRate;
    }
    
    public int getBABAtLevel(int level) {
        return (int) (level * this.progressionRate);
    }
}
