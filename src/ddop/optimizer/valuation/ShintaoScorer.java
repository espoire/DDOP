package ddop.optimizer.valuation;

import ddop.builds.CharBuild;
import ddop.builds.ReaperBuild;
import ddop.builds.adventurerClass.BaseAttackBonusProgression;
import ddop.optimizer.valuation.damage.weapon.CenteredUnarmedFalconer;
import ddop.stat.AbilityScore;
import util.NumberFormat;
import util.StatTotals;

@SuppressWarnings("unused")
public class ShintaoScorer extends SpecScorer {
	public ShintaoScorer(int simCharacterLevel) {
		super(simCharacterLevel);
		this.hitDie = 6;
		this.addDamageSource(new CenteredUnarmedFalconer());
	}
	
	private static final boolean MASS_FROG = false; // TODO - -2 all DCs, plus MF valuation
	private static final boolean GLOBE_OF_TRUE_IMPERIAL_BLOOD = true,
								 DIAMOND_OF_FESTIVE_WISDOM    = true,
								 ELDRITCH_RESISTANCE_RITUAL   = true,
								 SAPPHIRE_OF_GOOD_LUCK_2      = true,
								 YUGO_POT_WIS                 = true;

	private static final boolean DUALITY_THE_MORAL_COMPASS = true;
	
	@Override
	BaseAttackBonusProgression getBABProgression() {
		return BaseAttackBonusProgression.MEDIUM;
	}
	
	@Override
	CharBuild getBuild() {
		CharBuild build = new CharBuild();
		
		build.addStat("strength",     11 +8    +2  + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("dexterity",    13 +8    +2  + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("constitution", 16 +8    +2  + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("intelligence", 10 +8    +4  + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		build.addStat("wisdom",       18 +8 +7 +19 + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0) + (DIAMOND_OF_FESTIVE_WISDOM ? 2 : 0) +2 + (YUGO_POT_WIS ? 2 : 0)); // TODO Deadly Instinct deadly, +2 is filigree
		build.addStat("charisma",      8 +8    +4  + (GLOBE_OF_TRUE_IMPERIAL_BLOOD ? 1 : 0));
		
		build.addStat("striding", 32, "default");
		build.addStat("striding", 30, "monk");

		build.addStat("percent to-hit",         25, "proficiency");
		build.addStat("percent to-hit",          5, "precision");
		build.addStat("accuracy",               48);
		build.addStat("deadly",                 16);
		build.addStat("deadly",                 23, "insight"); // Falconer deadly instinct
		build.addStat("sneak attack damage",     3);
		build.addStat("melee power",           126);
		build.addStat("melee alacrity",         15, "default");
		build.addStat("melee alacrity",          6, "action boost"); // 6 * 20sec / 10min, @ 30% alacrity.
		build.addStat("damage vs helpless",     77.45);
		build.addStat("critical confirmation",   1);
		build.addStat("critical damage",         6);
		build.addStat("critical threat range",   2);
		build.addStat("critical multiplier",     1);
		build.addStat("overwhelming critical",   2);
		build.addStat("armor-piercing",         30);
		build.addStat("offhand attack chance",  90);
		build.addStat("doublestrike",           34);
		build.addStat("offhand doublestrike",   16);
		build.addStat("weapon enhancement bonus", 15, "default");  // TODO: weapon stats
		build.addStat("weapon enhancement bonus",  3, "enchant weapon past life");
		build.addStat("weapon enhancement bonus",  2, "arborea past life");
		build.addStat("bonus w",				 6.75);
		build.addStat("bonus w", 				 7, "default"); // TODO: weapon stats
		build.addStat("[w]",                     6.5, "default"); // TODO: weapon stats
		build.addStat("hp",                    545);
		build.addStat("percent hp",             30);
		build.addStat("unconsciousness range",   15, "guild");
		build.addStat("dr",                     10, "default");
		build.addStat("physical sheltering",    94);
		build.addStat("magical sheltering",     39);
		build.addStat("mrr cap",                40);
		build.addStat("healing amplification", 120);
		build.addStat("dodge",                  34);
		build.addStat("maximum dodge",          44);
		build.addStat("ac",                     74);
		build.addStat("fortification",          10, "bladeforged past life");
		build.addStat("fortification",          15);
		if(ELDRITCH_RESISTANCE_RITUAL) build.addStat("resistance", 1, "competence");
		if(SAPPHIRE_OF_GOOD_LUCK_2)    build.addStat("resistance", 2, "luck");
		build.addStat("epic fortitude",			 1);
		build.addStat("slippery mind",			 1);
		build.addStat("fortitude saves",        44);
		build.addStat("reflex saves",           42 + (YUGO_POT_WIS ? -4 : 0));
		build.addStat("will saves",             35);
		build.addStat("poison saves",           11);
		build.addStat("disease saves",           1);
		build.addStat("trap saves",              6);
		build.addStat("enchantment saves",       6);
		build.addStat("illusion saves",          2);
		build.addStat("spell resistance",       42);
		build.addStat("qp dc",                  21);
		build.addStat("combat mastery",         21);
		build.addStat("stunning",                1, "build 2");
//		build.addStat("offhand doublestrike",   16);
//		build.addStat("offhand doublestrike",   16);
//		build.addStat("offhand doublestrike",   16);
		
		if(DUALITY_THE_MORAL_COMPASS)
			build.addStat("ki", 3, "enhancement");
		
		return build;
	}
	
	@Override
	ReaperBuild getReaperBuild() {
		return new ReaperBuild(ReaperBuild.reaperBuildsDirectory + "noircere reaper build.txt", ReaperBuild.ReaperBuildOptions.getNoircereOptions());
	}
								
	private static final double BASE_FIRE_ABSORB_MULT = 0.85 * 0.97,
								BASE_COLD_ABSORB_MULT = 0.85 * 0.97,
								BASE_ACID_ABSORB_MULT = 0.85 * 0.97,
								BASE_ELEC_ABSORB_MULT = 0.85 * 0.97,
								BASE_SONIC_ABSORB_MULT = 0.85,
								BASE_NEG_ABSORB_MULT  = 0.75;
								
	private static final int    BASE_SPELL_POINTS = 65,
								BASE_HEAL_POWER = 166,
								
								BASE_KI_GENERATION = 3,
								BASE_KI_ON_HIT  = 1,
								BASE_KI_ON_CRIT = 4,
								BASE_CONCENTRATION = 56;

	private static final int 	KI_QUIVERING_PALM = 30,
								KI_STUNNING_FIST  = 15,
								KI_SMITE_TAINT    = 15,
								KI_JADE_STRIKE    = 10,
								KI_JADE_TOMB      = 25,
								KI_KUKANDO        = 25,
								KI_FISTS_LIGHT    = 10,
								KI_VOID_DRAGON    = 30,
								KI_KNOCK_ON_SKY   = 5,
								KI_EVERYTHING_NOTHING = 50,
								KI_SCATTERING_PETALS  = 30,
								KI_DRIFTING_LOTUS     = 25;

	private static final int    COOLDOWN_QUIVERING_PALM = 6,
								COOLDOWN_STUNNING_FIST  = 6,
								COOLDOWN_DIRE_CHARGE    = 12,
								COOLDOWN_SMITE_TAINT    = 15,
								COOLDOWN_JADE_STRIKE    = 15,
								COOLDOWN_JADE_TOMB      = 60,
								COOLDOWN_KUKANDO        = 15,
								COOLDOWN_FISTS_LIGHT = 3,
								COOLDOWN_VOID_DRAGON    = 180,
								COOLDOWN_KNOCK_ON_SKY   = 3,
								COOLDOWN_EVERYTHING_NOTHING = 180,
								COOLDOWN_SCATTERING_PETALS  = 90,
								COOLDOWN_DRIFTING_LOTUS     = 15;
	
	private static final double TARGETS_QUIVERING_PALM = 0.98, 
								TARGETS_STUNNING_FIST  = 0.98,
								TARGETS_DIRE_CHARGE    = 3.42,
								TARGETS_SMITE_TAINT    = 0.13,
								TARGETS_JADE_STRIKE    = 0.29,
								TARGETS_JADE_TOMB      = 0.98,
								TARGETS_KUKANDO        = 1;
	
	@Override
	protected double scoreDCs(StatTotals stats) {
		int wisdom			= this.getAbilityScore(AbilityScore.WISDOM, stats);
		int wisMod			= this.getAbilityMod(AbilityScore.WISDOM, stats);
		int deadlyInstinct	= wisMod / 2;
		
		int tacticsDC	= stats.getInt("qp dc")														+ wisMod + deadlyInstinct;
		int stunDC		= Math.max((int) (stats.getInt("dazing") * 1.5), stats.getInt("stunning"))	+ wisMod + deadlyInstinct;
		
		int quiveringPalmDC = 10 + this.getClassLevel() / 2 + tacticsDC;
		int stunningFistDC  = 10 + this.characterLevel  / 2 + stunDC;
		int direChargeDC    = 30                            + stunDC;
		int smiteTaintDC    = 10 + this.getClassLevel()     + stunDC;
		int jadeStrikeDC    = 10 + this.getClassLevel()     + stunDC;
		int jadeTombDC      = 10 + this.getClassLevel()     + stunDC;
		int kukandoDC       = 10 + this.getClassLevel()     + stunDC;
		
		double quiveringPalmDCRate	= SIM_ENEMY_SAVES_LOW_PORTION  * QPlookup  (quiveringPalmDC	- SIM_ENEMY_SAVES_FORT_LOW)
									+ SIM_ENEMY_SAVES_MED_PORTION  * QPlookup  (quiveringPalmDC	- SIM_ENEMY_SAVES_FORT_MED)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * QPlookup  (quiveringPalmDC	- SIM_ENEMY_SAVES_FORT_HIGH);
		double stunningFistDCRate	= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (stunningFistDC	- SIM_ENEMY_SAVES_FORT_LOW)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (stunningFistDC	- SIM_ENEMY_SAVES_FORT_MED)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (stunningFistDC	- SIM_ENEMY_SAVES_FORT_HIGH) * 0.05, 1);
		double direChargeDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (direChargeDC	- SIM_ENEMY_SAVES_FORT_LOW)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (direChargeDC	- SIM_ENEMY_SAVES_FORT_MED)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (direChargeDC	- SIM_ENEMY_SAVES_FORT_HIGH) * 0.05, 1);
		double smiteTaintDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (smiteTaintDC	- SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (smiteTaintDC	- SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (smiteTaintDC	- SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 1);
		double jadeStrikeDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (jadeStrikeDC	- SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (jadeStrikeDC	- SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (jadeStrikeDC	- SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 1);
		double jadeTombDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (jadeTombDC	- SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (jadeTombDC	- SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 1)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (jadeTombDC	- SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 1);
		double kukandoDCRate		= SIM_ENEMY_SAVES_LOW_PORTION  * cap(0.05, (kukandoDC	- SIM_ENEMY_SAVES_WILL_LOW)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_MED_PORTION  * cap(0.05, (kukandoDC	- SIM_ENEMY_SAVES_WILL_MED)  * 0.05, 0.95)
									+ SIM_ENEMY_SAVES_HIGH_PORTION * cap(0.05, (kukandoDC	- SIM_ENEMY_SAVES_WILL_HIGH) * 0.05, 0.95);
		
		double quiveringPalmScore = VALUATION_DEATH * SIM_DEATHABLE_PORTION   * quiveringPalmDCRate * TARGETS_QUIVERING_PALM / COOLDOWN_QUIVERING_PALM;
		double stunningFistScore  = VALUATION_STUN  * SIM_STUNNABLE_PORTION   * stunningFistDCRate  * TARGETS_STUNNING_FIST  / COOLDOWN_STUNNING_FIST;
		double direChargeScore    = VALUATION_STUN  * SIM_STUNNABLE_PORTION   * direChargeDCRate    * TARGETS_DIRE_CHARGE    / COOLDOWN_DIRE_CHARGE;
		double smiteTaintScore    = VALUATION_JADE  * SIM_JADEABLE_PORTION    * smiteTaintDCRate    * TARGETS_SMITE_TAINT    / COOLDOWN_SMITE_TAINT;
		double jadeStrikeScore    = VALUATION_JADE  * SIM_JADEABLE_PORTION    * jadeStrikeDCRate    * TARGETS_JADE_STRIKE    / COOLDOWN_JADE_STRIKE;
		double jadeTombScore      = VALUATION_JADE  * SIM_JADEABLE_PORTION    * jadeTombDCRate      * TARGETS_JADE_TOMB      / COOLDOWN_JADE_TOMB;
		double kukandoScore       = VALUATION_STUN  * SIM_KUKANDOABLE_PORTION * kukandoDCRate       * TARGETS_KUKANDO        / COOLDOWN_KUKANDO;
		
		int concentration = BASE_CONCENTRATION + this.getAbilityMod(AbilityScore.CONSTITUTION, stats) + stats.getInt("concentration");
		double kiPerHit   = BASE_KI_ON_HIT     + BASE_KI_ON_CRIT * (1 + stats.getInt("critical threat range")) / 20.0 * 0.91 + stats.get("ki");
		int averageKiAmount = 150;
		double kiDrain = (double) averageKiAmount / concentration;
		double passiveKi = BASE_KI_GENERATION - kiDrain;
		double kiIncome   = kiPerHit * 5.59 * 0.7 + passiveKi / 6.0;

		double quiveringPalmKiRate = KI_QUIVERING_PALM / COOLDOWN_QUIVERING_PALM;
		double stunningFistKiRate  = KI_STUNNING_FIST  / COOLDOWN_STUNNING_FIST;
		double smiteTaintKiRate    = KI_SMITE_TAINT    / COOLDOWN_SMITE_TAINT;
		double jadeStrikeKiRate    = KI_JADE_STRIKE    / COOLDOWN_JADE_STRIKE;
		double jadeTombKiRate      = KI_JADE_TOMB      / COOLDOWN_JADE_TOMB;
		double kukandoKiRate       = KI_KUKANDO        / COOLDOWN_KUKANDO;
		double fistsLightKiRate    = KI_FISTS_LIGHT    / COOLDOWN_FISTS_LIGHT;
		double voidDragonKiRate    = KI_VOID_DRAGON    / COOLDOWN_VOID_DRAGON;
		double knockOnSkyKiRate    = KI_KNOCK_ON_SKY   / COOLDOWN_KNOCK_ON_SKY;
		double everythingNothingKiRate = KI_EVERYTHING_NOTHING / COOLDOWN_EVERYTHING_NOTHING;
		double scatteringPetalsKiRate  = KI_SCATTERING_PETALS  / COOLDOWN_SCATTERING_PETALS;
		double driftingLotusKiRate     = KI_DRIFTING_LOTUS     / COOLDOWN_DRIFTING_LOTUS;

		double kiSpend = quiveringPalmKiRate + stunningFistKiRate +
				smiteTaintKiRate + jadeStrikeKiRate + jadeTombKiRate + kukandoKiRate +
				fistsLightKiRate + voidDragonKiRate + knockOnSkyKiRate +
				everythingNothingKiRate + scatteringPetalsKiRate + driftingLotusKiRate;

		double kiScore    = cap(0, kiIncome / kiSpend, 1);
		
		double killsPerSecondEquivalent = quiveringPalmScore + stunningFistScore + direChargeScore + smiteTaintScore + jadeStrikeScore + jadeTombScore + kukandoScore;
		double dcsScore = killsPerSecondEquivalent * SIM_ENEMY_HP * VALUATION_DCS * kiScore;
		
		if(this.verbose) {
			System.out.println("ShintaoScorer DCs Debug Log\n"
					+ "\n+- Ki Income: " + NumberFormat.readableLargeNumber(kiPerHit) + "/hit + " + NumberFormat.readableLargeNumber(passiveKi) + "/tick"
					+ "\n+- Ki Satis.: " + NumberFormat.percent(kiScore) + " (" + NumberFormat.readableLargeNumber(kiIncome) + " in / " + NumberFormat.readableLargeNumber(kiSpend) + " out)"
					+ "\n+- Wisdom:    " + wisdom          + " (+" + wisMod										+ ")"
					+ "\n+- QP:        " + quiveringPalmDC + " ("  + NumberFormat.percent(quiveringPalmDCRate)	+ ")"
					+ "\n+- Stun Fist: " + stunningFistDC  + " ("  + NumberFormat.percent(stunningFistDCRate)	+ ")"
					+ "\n+- Dire Chrg: " + direChargeDC    + " ("  + NumberFormat.percent(direChargeDCRate)		+ ")"
					+ "\n+- Jade:      " + jadeTombDC      + " ("  + NumberFormat.percent(jadeTombDCRate)		+ ")"
				    + "\n+- Kukan-Do:  " + kukandoDC       + " ("  + NumberFormat.percent(kukandoDCRate)		+ ")");
		}
		
		return dcsScore;
	}
	
	private static final double[] QP_LOOKUP = new double[] {
		0.06477284,
		0.06498409,
		0.06519397,
		0.06549316,
		0.06575064,
		0.06601677,
		0.06625897,
		0.06658504,
		0.06684239,
		0.06714607,
		0.06739969,
		0.06775007,
		0.06807302,
		0.06839468,
		0.06861613,
		0.06905665,
		0.06940093,
		0.06970183,
		0.07000873,
		0.07042513,
		0.07083341,
		0.07122289,
		0.07152349,
		0.07202907,
		0.07244339,
		0.07283825,
		0.07318586,
		0.07373493,
		0.07421843,
		0.07464755,
		0.07500717,
		0.0755913,
		0.07613732,
		0.07658713,
		0.07702438,
		0.07769566,
		0.07827858,
		0.07881401,
		0.07929226,
		0.08000501,
		0.08067112,
		0.08127384,
		0.0818236,
		0.08264502,
		0.08338539,
		0.08404868,
		0.08463141,
		0.08554879,
		0.08642152,
		0.08717115,
		0.08785405,
		0.08889771,
		0.08986966,
		0.0907059,
		0.09149704,
		0.09272197,
		0.09379339,
		0.09477989,
		0.09566752,
		0.09706781,
		0.098369,
		0.0994558,
		0.10048952,
		0.10214916,
		0.10360608,
		0.1049206,
		0.10617799,
		0.10805408,
		0.10978117,
		0.11136682,
		0.11282928,
		0.11510163,
		0.11717717,
		0.11906923,
		0.12080339,
		0.123559,
		0.12610523,
		0.12840537,
		0.13057007,
		0.13398428,
		0.1370524,
		0.13995504,
		0.14263406,
		0.14690351,
		0.15090995,
		0.15457974,
		0.15808378,
		0.16361364,
		0.16886143,
		0.17372699,
		0.17834414,
		0.18582666,
		0.19299207,
		0.19973787,
		0.20619943,
		0.21684217,
		0.22720625,
		0.23708722,
		0.24675515,
		0.26302759,
		0.2792528,
		0.29523425,
		0.31121779,
		0.33903933,
		0.36807695,
		0.39800977,
		0.42922345,
		0.46163985,
		0.49516683,
		0.52891411,
		0.56404525,
		0.60034559,
		0.63759824,
		0.67376258,
		0.71163976,
		0.75096969,
		0.79159979,
		0.82604313,
		0.86364506,
		0.90475338,
		0.95
	};
	
	private static double QPlookup(int dcDelta) {
		if(dcDelta >= 20)  return 0.95;
		if(dcDelta < -100) return 0.0645;
		return QP_LOOKUP[dcDelta + 100];
	}

	@Override
	protected boolean hasDCs() { return true; }
	
	@Override
	protected boolean valuesHealing() { return false; }
}
