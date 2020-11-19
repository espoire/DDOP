package ddop.builds;

import ddop.stat.Stat;

public class ReaperEnhancementTree extends EnhancementTree {
    private static final Enhancement[][] REAPER_ENHANCEMENTS = new Enhancement[][] {
        {
                new LinearEnhancement("Reaper's Blade", 3, "Melee Power", 2),
                new LinearEnhancement("Reaper's Tactics", 1, "Build Combat Mastery", 1),
                new LinearEnhancement("Reaper's Charge", 1, "Reaper Charge", 1),
                new LinearEnhancement("Reaper's Physical Ability", 1, new Stat("Strength", "Stacking", 1), new Stat("Dexterity", "Stacking", 1)),
                new LinearEnhancement("Reaper's Shot", 3, "Ranged Power", 2),
        },
        {
                new LinearEnhancement("Reaper's Blade", 3, "Melee Power", 2),
                new LinearEnhancement("Reaper's Imbuement", 1),
                new LinearEnhancement("Reaper's Deadly Strikes", 1),
                new LinearEnhancement("Reaper's Shot", 3, "Ranged Power", 2),
        },
        {
                new LinearEnhancement("Reaper's Blade", 3, "Melee Power", 1),
                new LinearEnhancement("Reaper's Tactics", 1, "Build Combat Mastery", 1),
                new LinearEnhancement("Reaper's Physical Ability", 1, new Stat("Strength", "Stacking", 1), new Stat("Dexterity", "Stacking", 1)),
                new LinearEnhancement("Reaper's Shot", 3, "Ranged Power", 1),
        },
        {
                new LinearEnhancement("Reaper's Blade", 3, "Melee Power", 1),
                new LinearEnhancement("Reaper's Tactics", 1, "Build Combat Mastery", 1),
                new LinearEnhancement("Reaper's Physical Ability", 1, new Stat("Strength", "Stacking", 1), new Stat("Dexterity", "Stacking", 1)),
                new LinearEnhancement("Reaper's Shot", 3, "Ranged Power", 1),
        },
        {
                new LinearEnhancement("Reaper's Blade", 3, "Melee Power", 1),
                new LinearEnhancement("Reaper's Tactics", 1, "Build Combat Mastery", 1),
                new LinearEnhancement("Reaper's Strike", 3),
                new LinearEnhancement("Reaper's Physical Ability", 1, new Stat("Strength", "Stacking", 1), new Stat("Dexterity", "Stacking", 1)),
                new LinearEnhancement("Reaper's Shot", 3, "Ranged Power", 1),
        },
        {
                new LinearEnhancement("Reaper's Offense I", 1, new Stat("Accuracy", "Stacking", 1), new Stat("Deadly", "Stacking", 1)),
                new LinearEnhancement("Reaper's Offense II", 1, new Stat("Accuracy", "Stacking", 1), new Stat("Deadly", "Stacking", 1)),
                new LinearEnhancement("Reaper's Offense III", 1, new Stat("Strength", "Stacking", 1), new Stat("Dexterity", "Stacking", 1)),
                new LinearEnhancement("Reaper's Offense IV", 1, new Stat("Accuracy", "Stacking", 2), new Stat("Deadly", "Stacking", 2)),
                new LinearEnhancement("Reaper's Offense V", 1,  new Stat("Melee Power", "Stacking", 1), new Stat("Ranged Power", "Stacking", 1)),
                new LinearEnhancement("Reaper's Offense VI", 1, new Stat("Melee Power", "Stacking", 2), new Stat("Ranged Power", "Stacking", 2)),
        },
        {
            new LinearEnhancement("Reaper's Arcana", 3, "Potency", 4),
            new LinearEnhancement("Reaper's Focus", 1, "Spell Focus Mastery", 1),
            new LinearEnhancement("Reaper's Charge", 1, "Reaper Charges", 1),
            new LinearEnhancement("Reaper's Mental Ability", 1, new Stat("Intelligence", "Stacking", 1), new Stat("Wisdom", "Stacking", 1), new Stat("Charisma", "Stacking", 1)),
            new LinearEnhancement("Reaper's Deep Magic", 1, "Spell Penetration", 1),
        },
        {
            new LinearEnhancement("Reaper's Arcana", 3, "Potency", 4),
            new LinearEnhancement("Reaper's Potency", 1),
            new LinearEnhancement("Reaper's Efficiency", 1),
            new LinearEnhancement("Reaper's Deep Magic", 1, "Spell Penetration", 1),
        },
        {
            new LinearEnhancement("Reaper's Arcana", 3, "Potency", 2),
            new LinearEnhancement("Reaper's Focus", 1, "Spell Focus Mastery", 1),
            new LinearEnhancement("Reaper's Mental Ability", 1, new Stat("Intelligence", "Stacking", 1), new Stat("Wisdom", "Stacking", 1), new Stat("Charisma", "Stacking", 1)),
            new LinearEnhancement("Reaper's Deep Magic", 1, "Spell Penetration", 1),
        },
        {
            new LinearEnhancement("Reaper's Arcana", 3, "Potency", 2),
            new LinearEnhancement("Reaper's Focus", 1, "Spell Focus Mastery", 1),
            new LinearEnhancement("Reaper's Mental Ability", 1, new Stat("Intelligence", "Stacking", 1), new Stat("Wisdom", "Stacking", 1), new Stat("Charisma", "Stacking", 1)),
            new LinearEnhancement("Reaper's Deep Magic", 1, "Spell Penetration", 1),
        },
        {
            new LinearEnhancement("Reaper's Arcana", 3, "Potency", 2),
            new LinearEnhancement("Reaper's Focus", 1, "Spell Focus Mastery", 1),
            new LinearEnhancement("Reaper's Power", 1),
            new LinearEnhancement("Reaper's Mental Ability", 1, new Stat("Intelligence", "Stacking", 1), new Stat("Wisdom", "Stacking", 1), new Stat("Charisma", "Stacking", 1)),
            new LinearEnhancement("Reaper's Deep Magic", 1, "Spell Penetration", 1),
        },
        {
            new LinearEnhancement("Reaper's Arcanum I", 1, "Wizardry", 50),
            new LinearEnhancement("Reaper's Arcanum II", 1, "Wizardry", 50),
            new LinearEnhancement("Reaper's Arcanum III", 1, new Stat("Intelligence", "Stacking", 1), new Stat("Wisdom", "Stacking", 1), new Stat("Charisma", "Stacking", 1)),
            new LinearEnhancement("Reaper's Arcanum IV", 1, "Wizardry", 100),
            new LinearEnhancement("Reaper's Arcanum V", 1, "Wizardry", 150),
            new LinearEnhancement("Reaper's Arcanum VI", 1, "Wizardry", 200),
        },
        {
            new LinearEnhancement("Reaper's Sheltering", 3, new Stat("Physical Sheltering", "Stacking", 2), new Stat("magical sheltering", "Stacking", 2)),
            new LinearEnhancement("Reaper's Saves", 3, new Stat("Resistance", "Stacking", 1)),
            new LinearEnhancement("Reaper's Charge", 1, "Reaper Charges", 1),
            new LinearEnhancement("Reaper's Constitution", 1, "Constitution", 1),
            new LinearEnhancement("Reaper's Heightened Evasion", 1, "Maximum Dodge", 1),
        },
        {
            new LinearEnhancement("Reaper's Sheltering", 3, new Stat("Physical Sheltering", "Stacking", 2), new Stat("magical sheltering", "Stacking", 2)),
            new LinearEnhancement("Reaper's Resistance", 1),
            new LinearEnhancement("Reaper's Steadfastness", 1),
            new LinearEnhancement("Reaper's Heightened Evasion", 1, "Maximum Dodge", 1),
        },
        {
            new LinearEnhancement("Reaper's Sheltering", 3, new Stat("Physical Sheltering", "Stacking", 1), new Stat("magical sheltering", "Stacking", 1)),
            new LinearEnhancement("Reaper's Saves", 3, new Stat("Resistance", "Stacking", 1)),
            new LinearEnhancement("Reaper's Constitution", 1, "Constitution", 1),
            new LinearEnhancement("Reaper's Evasion", 3, "Dodge", 1),
        },
        {
            new LinearEnhancement("Reaper's Sheltering", 3, new Stat("Physical Sheltering", "Stacking", 1), new Stat("magical sheltering", "Stacking", 1)),
            new LinearEnhancement("Reaper's Saves", 3, new Stat("Resistance", "Stacking", 1)),
            new LinearEnhancement("Reaper's Constitution", 1, "Constitution", 1),
            new LinearEnhancement("Reaper's Evasion", 3, "Dodge", 1),
        },
        {
            new LinearEnhancement("Reaper's Sheltering", 3, new Stat("Physical Sheltering", "Stacking", 1), new Stat("magical sheltering", "Stacking", 1)),
            new LinearEnhancement("Reaper's Saves", 3, new Stat("Resistance", "Stacking", 1)),
            new LinearEnhancement("Reaper's Luck", 1),
            new LinearEnhancement("Reaper's Constitution", 1, "Constitution", 1),
            new LinearEnhancement("Reaper's Evasion", 3, "Dodge", 1),
        },
        {
            new LinearEnhancement("Reaper's Defense I", 1, "HP", 10),
            new LinearEnhancement("Reaper's Defense II", 1, new Stat("Resistance", "Stacking", 1), new Stat("HP", "Stacking", 20)),
            new LinearEnhancement("Reaper's Defense III", 1, "Constitution", 1),
            new LinearEnhancement("Reaper's Defense IV", 1, new Stat("Resistance", "Stacking", 1), new Stat("HP", "Stacking", 100)),
            new LinearEnhancement("Reaper's Defense V", 1, new Stat("Dodge", "Stacking", 1), new Stat("Physical Sheltering", "Stacking", 1), new Stat("magical sheltering", "Stacking", 1)),
            new LinearEnhancement("Reaper's Defense VI", 1, new Stat("Maximum Dodge", "Stacking", 1),new Stat("Physical Sheltering", "Stacking", 2), new Stat("magical sheltering", "Stacking", 2)),
        }
    };
    
    public ReaperEnhancementTree() {
        super(REAPER_ENHANCEMENTS);
    }
}
