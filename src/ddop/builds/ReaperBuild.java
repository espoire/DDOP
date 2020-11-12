package ddop.builds;

import ddop.Settings;
import ddop.stat.Stat;
import ddop.stat.StatSource;
import file.Reader;

import java.util.Collection;
import java.util.Set;

public class ReaperBuild extends EnhancementBuild implements StatSource {
    public static final String reaperBuildsDirectory = "X:\\src\\DDOP\\saved builds\\";

    private int scalingHPBonus;
    private final Set<String> filter;
    
    public ReaperBuild(String filePath, ReaperBuildOptions options, Set<String> filter) {
        super(ReaperBuild.loadFileToRankMap(filePath));
        this.countScalingHP();
        this.filter = filter;
    }
    
    private void countScalingHP() {
        this.scalingHPBonus = 0;
        for(int i = 0; i < FORMAT_LINE_COUNT; i++) {
            int HPPerPoint = (i < 2 * FORMAT_LINE_COUNT / 3 ? 4 : 8);
            for(int j = 0; j < FORMAT_LINE_LENGTHS[i]; j++) {
                int points = this.getRanks(i, j);
                this.scalingHPBonus += points * HPPerPoint;
            }
        }
    }
    
    private static int[][] loadFileToRankMap(String filePath) {
        String fileContent = Reader.getEntireFile(filePath);
        String[] fileLines = fileContent.replace(" ", "").split("\n");
    
        if(ReaperBuild.isValidFormat(fileLines)) {
            return ReaperBuild.toRankMap(fileLines);
        } else {
            System.err.println("Invalid reaper build format from file \"" + filePath + "\"");
            return null;
        }
    }
    
    private static int[][] toRankMap(String[] fileLines) {
        int[][] ret = new int[fileLines.length][];
        
        for(int i = 0; i < FORMAT_LINE_COUNT; i++) {
            ret[i] = new int[FORMAT_LINE_LENGTHS[i]];
            for(int j = 0; j < FORMAT_LINE_LENGTHS[i]; j++) {
                ret[i][j] = Character.getNumericValue(fileLines[i].charAt(j));
                if(Settings.DEBUG_REAPER_BUILD) System.out.print(ret[i][j]);
            }
            if(Settings.DEBUG_REAPER_BUILD) System.out.println();
        }
        
        return ret;
    }

    public Collection<Stat> getStats() {
        Collection<Stat> ret = new ReaperEnhancementTree().getStatsFromBuild(this);
        if(this.scalingHPBonus > 0) ret.add(new Stat("HP", "In Reaper", this.scalingHPBonus));

        ret.removeIf(stat -> ! this.filter.contains(stat.category));

        return ret;
    }
    
    private static final int[] FORMAT_LINE_LENGTHS = new int[] {5, 4, 4, 4, 5, 6, 5, 4, 4, 4, 5, 6, 5, 4, 4, 4, 5, 6};
    private static final int   FORMAT_LINE_COUNT   = FORMAT_LINE_LENGTHS.length;
    
    /**Example format:
     *
     * Dread     Dire        Grim
     * Adversary Thaumaturge Barricade
     *
     * 00000     00000       30011
     * 00 00     00 00       30 01
     * 01 00     00 00       30 12
     * 31 00     00 00       30 12
     * 31000     00010       33013
     * 110000    100000      111110
     *
     * All spaces are ignored.
     * Prerequisites are not checked.
     */
    private static boolean isValidFormat(String[] fileLines) {
        if(fileLines.length != FORMAT_LINE_COUNT) return false;
        for(int i = 0; i < FORMAT_LINE_COUNT; i++) {
            if(fileLines[i].length() != FORMAT_LINE_LENGTHS[i]) return false;
        }
        return true;
    }
    
    public static class ReaperBuildOptions {
        public enum PhysicalAbility {
            STRENGTH,
            DEXTERITY
        }
        
        public enum MentalAbility {
            INTELLIGENCE,
            WISDOM,
            CHARISMA
        }
    
        public enum ResistanceRating {
            PRR,
            MRR
        }
    
        public enum SavingThrow {
            FORTITUDE,
            REFLEX,
            WILL
        }
        
        public PhysicalAbility DAabilityCore, DAabilityLow, DAabilityHigh;
        public MentalAbility DTabilityCore, DTabilityLow, DTabilityHigh;
        public ResistanceRating GBresistanceRating;
        public SavingThrow GBsaveLow, GBsaveHigh;
        
        public ReaperBuildOptions setAllPhysicalAbilities(PhysicalAbility pa) {
            this.DAabilityCore = this.DAabilityLow = this.DAabilityHigh = pa;
            return this;
        }
        
        public ReaperBuildOptions setAllMentalAbilities(MentalAbility ma) {
            this.DTabilityCore = this.DTabilityLow = this.DTabilityHigh = ma;
            return this;
        }
        
        public ReaperBuildOptions setResistanceRating(ResistanceRating rr) {
            this.GBresistanceRating = rr;
            return this;
        }
        
        public ReaperBuildOptions setAllSavingThrows(SavingThrow st) {
            this.GBsaveLow = this.GBsaveHigh = st;
            return this;
        }
        
        public static ReaperBuildOptions getNoircereOptions() {
            return new ReaperBuildOptions()
                    .setAllPhysicalAbilities(PhysicalAbility.DEXTERITY)
                    .setAllMentalAbilities(MentalAbility.WISDOM)
                    .setResistanceRating(ResistanceRating.PRR)
                    .setAllSavingThrows(SavingThrow.REFLEX);
        }
    }
}
