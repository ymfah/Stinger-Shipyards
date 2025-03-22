package data.scripts.world.industries;

import java.awt.Color;
import java.util.Random;

import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener.FleetDespawnReason;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory.PatrolType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.PatrolAssignmentAIV4;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.OptionalFleetData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteData;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteFleetSpawner;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager.RouteSegment;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;



public class sex_maxios extends BaseIndustry implements RouteFleetSpawner, FleetEventListener {

	@Override
	public boolean isHidden() {
		return !market.getFactionId().equals(Factions.INDEPENDENT);
	}

	@Override
	public boolean isFunctional() {
		return market.getFactionId().equals(Factions.INDEPENDENT);
	}

	public static float OFFICER_PROB_MOD_PATROL_HQ = 0.1f;
	public static float OFFICER_PROB_MOD_MILITARY_BASE = 0.2f;
	public static float OFFICER_PROB_MOD_HIGH_COMMAND = 0.3f;


	public static float DEFENSE_BONUS_PATROL = 0.1f;
	public static float DEFENSE_BONUS_MILITARY = 0.2f;
	public static float DEFENSE_BONUS_COMMAND = 0.3f;

	public static int IMPROVE_NUM_PATROLS_BONUS = 1;

	public void apply() {
		super.apply(true);

		int size = market.getSize();

		boolean patrol = true;
		boolean militaryBase = false;
		boolean command = false;

		super.apply(!patrol);
		if (patrol) {
			applyIncomeAndUpkeep(3);
		}

		int extraDemand = 0;

		int light = 1;
		int medium = 0;
		int heavy = 0;

		if (patrol) {
			extraDemand = 0;
		}

		if (patrol) {
			light = 0;
			medium = 0;
			heavy = 1;
		}

//		if (market.getId().equals("jangala")) {
//			System.out.println("wefwefwe");
//		}

//		light += 5;
//		medium += 3;
//		heavy += 2;

//		float spawnRateMultStability = getStabilitySpawnRateMult();
//		if (spawnRateMultStability != 1) {
//			market.getStats().getDynamic().getStat(Stats.COMBAT_FLEET_SPAWN_RATE_MULT).modifyMult(getModId(), spawnRateMultStability);
//		}


		market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).modifyFlat(getModId(), light);
		market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).modifyFlat(getModId(), medium);
		market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(getModId(), heavy);


		demand(Commodities.SUPPLIES, size - 2 + extraDemand);
		demand(Commodities.FUEL, size - 1 + extraDemand);
		demand(Commodities.SHIPS, size - 1 + extraDemand);

		supply(Commodities.CREW, size);

		if (!patrol) {
			//demand(Commodities.HAND_WEAPONS, size);
			supply(Commodities.MARINES, size);

//			Pair<String, Integer> deficit = getMaxDeficit(Commodities.HAND_WEAPONS);
//			applyDeficitToProduction(1, deficit, Commodities.MARINES);
		}


		modifyStabilityWithBaseMod();

		float mult = getDeficitMult(Commodities.SUPPLIES);
		String extra = "";
		if (mult != 1) {
			String com = getMaxDeficit(Commodities.SUPPLIES).one;
			extra = " (" + getDeficitText(com).toLowerCase() + ")";
		}
		float bonus = DEFENSE_BONUS_MILITARY;
		if (patrol) bonus = DEFENSE_BONUS_PATROL;
		else if (command) bonus = DEFENSE_BONUS_COMMAND;
		market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
				.modifyMult(getModId(), 1f + bonus * mult, "Nova Auxilia presence");


		MemoryAPI memory = market.getMemoryWithoutUpdate();
		Misc.setFlagWithReason(memory, MemFlags.MARKET_PATROL, getModId(), true, -1);

		if (militaryBase || command) {
			Misc.setFlagWithReason(memory, MemFlags.MARKET_MILITARY, getModId(), true, -1);
		}

		float officerProb = OFFICER_PROB_MOD_PATROL_HQ;
		if (militaryBase) officerProb = OFFICER_PROB_MOD_MILITARY_BASE;
		else if (command) officerProb = OFFICER_PROB_MOD_HIGH_COMMAND;
		market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).modifyFlat(getModId(0), officerProb);


		if (!isFunctional()) {
			supply.clear();
			unapply();
		}

	}

	@Override
	public void unapply() {
		super.unapply();

		MemoryAPI memory = market.getMemoryWithoutUpdate();
		Misc.setFlagWithReason(memory, MemFlags.MARKET_PATROL, getModId(), false, -1);
		Misc.setFlagWithReason(memory, MemFlags.MARKET_MILITARY, getModId(), false, -1);

		unmodifyStabilityWithBaseMod();

		//market.getStats().getDynamic().getStat(Stats.COMBAT_FLEET_SPAWN_RATE_MULT).unmodifyMult(getModId());
		//market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MULT).unmodifyFlat(getModId());

		market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).unmodifyFlat(getModId());
		market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).unmodifyFlat(getModId());
		market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodifyFlat(getModId());

		market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId());

		market.getStats().getDynamic().getMod(Stats.OFFICER_PROB_MOD).unmodifyFlat(getModId(0));

		if (!market.getFactionId().equals(Factions.INDEPENDENT) && !Global.getSector().getMemoryWithoutUpdate().getBoolean("$sex_MaxiosRemoved")) {
			market.addIndustry(Industries.MILITARYBASE);
			Global.getSector().getMemoryWithoutUpdate().set("$sex_MaxiosRemoved", true);
		}
	}

	protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
		return mode != IndustryTooltipMode.NORMAL || isFunctional();
	}

	@Override
	protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
		if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
			addStabilityPostDemandSection(tooltip, hasDemand, mode);

			boolean patrol = getSpec().hasTag(Industries.TAG_PATROL);
			boolean command = getSpec().hasTag(Industries.TAG_COMMAND);
			float bonus = DEFENSE_BONUS_MILITARY;
			if (patrol) bonus = DEFENSE_BONUS_PATROL;
			if (command) bonus = DEFENSE_BONUS_COMMAND;
			addGroundDefensesImpactSection(tooltip, bonus, Commodities.SUPPLIES);
		}
	}

	@Override
	protected int getBaseStabilityMod() {
		boolean patrol = getSpec().hasTag(Industries.TAG_PATROL);
		boolean militaryBase = getSpec().hasTag(Industries.TAG_MILITARY);
		boolean command = getSpec().hasTag(Industries.TAG_COMMAND);
		int stabilityMod = 1;
		if (patrol) {
			stabilityMod = 1;
		} else if (militaryBase) {
			stabilityMod = 2;
		} else if (command) {
			stabilityMod = 2;
		}
		return stabilityMod;
	}

	public String getNameForModifier() {
//		boolean patrol = Industries.PATROLHQ.equals(getId());
//		if (patrol) return getSpec().getName();
		if (getSpec().getName().contains("HQ")) {
			return getSpec().getName();
		}

		return Misc.ucFirst(getSpec().getName().toLowerCase());
	}


//	protected float getStabilitySpawnRateMult() {
//		return Math.max(0.2f, market.getStabilityValue() / 10f);
//	}

	@Override
	protected Pair<String, Integer> getStabilityAffectingDeficit() {
		boolean patrol = getSpec().hasTag(Industries.TAG_PATROL);
		if (patrol) {
			return getMaxDeficit(Commodities.SUPPLIES, Commodities.FUEL, Commodities.SHIPS);
		}
		//return getMaxDeficit(Commodities.SUPPLIES, Commodities.FUEL, Commodities.SHIPS, Commodities.HAND_WEAPONS);
		return getMaxDeficit(Commodities.SUPPLIES, Commodities.FUEL, Commodities.SHIPS);
	}


	public boolean isDemandLegal(CommodityOnMarketAPI com) {
		return true;
	}

	public boolean isSupplyLegal(CommodityOnMarketAPI com) {
		return true;
	}

	public boolean isAvailableToBuild() {
		return false;
	}

	public boolean showWhenUnavailable() {
		return false;
	}


	//protected IntervalUtil tracker = new IntervalUtil(5f, 9f);
	protected IntervalUtil tracker = new IntervalUtil(Global.getSettings().getFloat("averagePatrolSpawnInterval") * 0.7f,
			Global.getSettings().getFloat("averagePatrolSpawnInterval") * 1.3f);

	protected float returningPatrolValue = 0f;

	@Override
	protected void buildingFinished() {
		super.buildingFinished();

		tracker.forceIntervalElapsed();
	}

	@Override
	protected void upgradeFinished(Industry previous) {
		super.upgradeFinished(previous);

		tracker.forceIntervalElapsed();
	}

	@Override
	public void advance(float amount) {
		super.advance(amount);

		if (Global.getSector().getEconomy().isSimMode()) return;

		if (!isFunctional()) return;

		float days = Global.getSector().getClock().convertToDays(amount);

//		float stability = market.getPrevStability();
//		float spawnRate = 1f + (stability - 5) * 0.2f;
//		if (spawnRate < 0.5f) spawnRate = 0.5f;

		float spawnRate = 1f;
		float rateMult = market.getStats().getDynamic().getStat(Stats.COMBAT_FLEET_SPAWN_RATE_MULT).getModifiedValue();
		spawnRate *= rateMult;

		if (Global.getSector().isInNewGameAdvance()) {
			spawnRate *= 3f;
		}

		float extraTime = 0f;
		if (returningPatrolValue > 0) {
			// apply "returned patrols" to spawn rate, at a maximum rate of 1 interval per day
			float interval = tracker.getIntervalDuration();
			extraTime = interval * days;
			returningPatrolValue -= days;
			if (returningPatrolValue < 0) returningPatrolValue = 0;
		}
		tracker.advance(days * spawnRate + extraTime);

		//DebugFlags.FAST_PATROL_SPAWN = true;
		if (DebugFlags.FAST_PATROL_SPAWN) {
			tracker.advance(days * spawnRate * 100f);
		}

		if (tracker.intervalElapsed()) {
//			if (market.isPlayerOwned()) {
//				System.out.println("ewfwefew");
//			}
//			if (market.getName().equals("Jangala")) {
//				System.out.println("wefwefe");
//			}
			String sid = getRouteSourceId();

			int light = getCount(PatrolType.FAST);
			int medium = getCount(PatrolType.COMBAT);
			int heavy = getCount(PatrolType.HEAVY);

			int maxLight = 0;
			int maxMedium = 0;
			int maxHeavy = 1;

			WeightedRandomPicker<PatrolType> picker = new WeightedRandomPicker<PatrolType>();
			picker.add(PatrolType.HEAVY, maxHeavy - heavy);
			picker.add(PatrolType.COMBAT, maxMedium - medium);
			picker.add(PatrolType.FAST, maxLight - light);

			if (picker.isEmpty()) return;

			PatrolType type = picker.pick();
			MilitaryBase.PatrolFleetData custom = new MilitaryBase.PatrolFleetData(type);

			OptionalFleetData extra = new OptionalFleetData(market);
			extra.fleetType = type.getFleetType();

			RouteData route = RouteManager.getInstance().addRoute(sid, market, Misc.genRandomSeed(), extra, this, custom);
			extra.strength = (float) getPatrolCombatFP(type, route.getRandom());
			extra.strength = Misc.getAdjustedStrength(extra.strength, market);


			float patrolDays = 35f + (float) Math.random() * 10f;
			route.addSegment(new RouteSegment(patrolDays, market.getPrimaryEntity()));
		}
	}

	public void reportAboutToBeDespawnedByRouteManager(RouteData route) {
//		if (route.getActiveFleet() == null) return;
//		PatrolFleetData custom = (PatrolFleetData) route.getCustom();
//		custom.despawnFP = route.getActiveFleet().getFleetPoints();
	}

	public boolean shouldRepeat(RouteData route) {
//		PatrolFleetData custom = (PatrolFleetData) route.getCustom();
////		return custom.spawnFP == custom.despawnFP ||
////				(route.getActiveFleet() != null && route.getActiveFleet().getFleetPoints() >= custom.spawnFP * 0.6f);
//		return route.getActiveFleet() != null && route.getActiveFleet().getFleetPoints() >= custom.spawnFP * 0.6f;
		return false;
	}

	public int getCount(PatrolType ... types) {
		int count = 0;
		for (RouteData data : RouteManager.getInstance().getRoutesForSource(getRouteSourceId())) {
			if (data.getCustom() instanceof MilitaryBase.PatrolFleetData) {
				MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) data.getCustom();
				for (PatrolType type : types) {
					if (type == custom.type) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	public int getMaxPatrols(PatrolType type) {
		if (type == PatrolType.FAST) {
			return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_LIGHT_MOD).computeEffective(0);
		}
		if (type == PatrolType.COMBAT) {
			return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).computeEffective(0);
		}
		if (type == PatrolType.HEAVY) {
			return (int) market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).computeEffective(0);
		}
		return 0;
	}

	public String getRouteSourceId() {
		return getMarket().getId() + "_" + "military";
	}

	public boolean shouldCancelRouteAfterDelayCheck(RouteData route) {
		return false;
	}


	public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

	}

	public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, FleetDespawnReason reason, Object param) {
		if (!isFunctional()) return;

		if (reason == FleetDespawnReason.REACHED_DESTINATION) {
			RouteData route = RouteManager.getInstance().getRoute(getRouteSourceId(), fleet);
			if (route.getCustom() instanceof MilitaryBase.PatrolFleetData) {
				MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) route.getCustom();
				if (custom.spawnFP > 0) {
					float fraction  = fleet.getFleetPoints() / custom.spawnFP;
					returningPatrolValue += fraction;
				}
			}
		}
	}

	public static int getPatrolCombatFP(PatrolType type, Random random) {
		float combat = 0;
		switch (type) {
			case FAST:
				combat = Math.round(3f + (float) random.nextFloat() * 3f) * 5f;
				break;
			case COMBAT:
				combat = Math.round(7f + (float) random.nextFloat() * 4f) * 6f;
				break;
			case HEAVY:
				combat = Math.round(16f + (float) random.nextFloat() * 6f) * 6f;
				break;
		}
		return (int) Math.round(combat);
	}

	public CampaignFleetAPI spawnFleet(RouteData route) {

		MilitaryBase.PatrolFleetData custom = (MilitaryBase.PatrolFleetData) route.getCustom();
		PatrolType type = custom.type;

		Random random = route.getRandom();

		CampaignFleetAPI fleet = createPatrol(type, market.getFactionId(), route, market, null, random);

		if (fleet == null || fleet.isEmpty()) return null;

		fleet.addEventListener(this);

		market.getContainingLocation().addEntity(fleet);
		fleet.setFacing((float) Math.random() * 360f);
		// this will get overridden by the patrol assignment AI, depending on route-time elapsed etc
		fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);

		fleet.addScript(new PatrolAssignmentAIV4(fleet, route));

		fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, true, 0.3f);

		//market.getContainingLocation().addEntity(fleet);
		//fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);

		if (custom.spawnFP <= 0) {
			custom.spawnFP = fleet.getFleetPoints();
		}

		return fleet;
	}

	public static CampaignFleetAPI createPatrol(PatrolType type, String factionId, RouteData route, MarketAPI market, Vector2f locInHyper, Random random) {
		if (random == null) random = new Random();


		float combat = getPatrolCombatFP(type, random);
		float tanker = 0f;
		float freighter = 0f;
		String fleetType = type.getFleetType();
		switch (type) {
			case FAST:
				break;
			case COMBAT:
				tanker = Math.round((float) random.nextFloat() * 5f);
				break;
			case HEAVY:
				tanker = Math.round((float) random.nextFloat() * 10f);
				freighter = Math.round((float) random.nextFloat() * 10f);
				break;
		}

		FleetParamsV3 params = new FleetParamsV3(
				market,
				locInHyper,
				"sex_maxios_dummy",
				route == null ? null : route.getQualityOverride(),
				fleetType,
				combat, // combatPts
				freighter, // freighterPts
				tanker, // tankerPts
				0f, // transportPts
				0f, // linerPts
				0f, // utilityPts
				0f // qualityMod
		);
		if (route != null) {
			params.timestamp = route.getTimestamp();
		}
		params.random = random;
		CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

		if (fleet == null || fleet.isEmpty()) return null;

		fleet.setFaction(Factions.INDEPENDENT, true);
		fleet.setName("Nova Auxilia");

		if (!fleet.getFaction().getCustomBoolean(Factions.CUSTOM_PATROLS_HAVE_NO_PATROL_MEMORY_KEY)) {
			fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PATROL_FLEET, true);
			if (type == PatrolType.FAST || type == PatrolType.COMBAT) {
				fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_CUSTOMS_INSPECTOR, true);
			}
		} else if (fleet.getFaction().getCustomBoolean(Factions.CUSTOM_PIRATE_BEHAVIOR)) {
			fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);

			// hidden pather and pirate bases
			// make them raid so there's some consequence to just having a colony in a system with one of those
			if (market != null && market.isHidden()) {
				fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_RAIDER, true);
			}
		}

		String postId = Ranks.POST_PATROL_COMMANDER;
		String rankId = Ranks.SPACE_COMMANDER;
		switch (type) {
			case FAST:
				rankId = Ranks.SPACE_LIEUTENANT;
				break;
			case COMBAT:
				rankId = Ranks.SPACE_COMMANDER;
				break;
			case HEAVY:
				rankId = Ranks.SPACE_ADMIRAL;
				break;
		}

		fleet.getCommander().setPostId(postId);
		fleet.getCommander().setRankId(rankId);

		return fleet;
	}



	public static float ALPHA_CORE_BONUS = 0.25f;
	@Override
	protected void applyAlphaCoreModifiers() {
		market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyMult(
				getModId(), 1f + ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
	}

	@Override
	protected void applyNoAICoreModifiers() {
		market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).unmodifyMult(getModId());
	}

	@Override
	protected void applyAlphaCoreSupplyAndDemandModifiers() {
		demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
	}

	protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
		float opad = 10f;
		Color highlight = Misc.getHighlightColor();

		String pre = "Alpha-level AI core currently assigned. ";
		if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
			pre = "Alpha-level AI core. ";
		}
		float a = ALPHA_CORE_BONUS;
		//String str = "" + (int)Math.round(a * 100f) + "%";
		String str = Strings.X + (1f + a);

		if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
			CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
			TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
			text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
							"Increases fleet size by %s.", 0f, highlight,
					"" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
					str);
			tooltip.addImageWithText(opad);
			return;
		}

		tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
						"Increases fleet size by %s.", opad, highlight,
				"" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
				str);

	}


	@Override
	public boolean canImprove() {
		return true;
	}

	protected void applyImproveModifiers() {

		String key = "mil_base_improve";
		if (isImproved()) {
			boolean patrol = getSpec().hasTag(Industries.TAG_PATROL);
//			boolean militaryBase = getSpec().hasTag(Industries.TAG_MILITARY);
//			boolean command = getSpec().hasTag(Industries.TAG_COMMAND);

			if (patrol) {
				market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).modifyFlat(key, IMPROVE_NUM_PATROLS_BONUS);
			} else {
				market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).modifyFlat(key, IMPROVE_NUM_PATROLS_BONUS);
			}
		} else {
			market.getStats().getDynamic().getMod(Stats.PATROL_NUM_MEDIUM_MOD).unmodifyFlat(key);
			market.getStats().getDynamic().getMod(Stats.PATROL_NUM_HEAVY_MOD).unmodifyFlat(key);
		}
	}

	public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
		float opad = 10f;
		Color highlight = Misc.getHighlightColor();

		String str = "" + (int) IMPROVE_NUM_PATROLS_BONUS;

		boolean patrol = getSpec().hasTag(Industries.TAG_PATROL);
		String type = "medium patrols";
		if (!patrol) type = "heavy patrols";

		if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
			info.addPara("Number of " + type + " launched increased by %s.", 0f, highlight, str);
		} else {
			info.addPara("Increases the number of " + type + " launched by %s.", 0f, highlight, str);
		}

		info.addSpacer(opad);
		super.addImproveDesc(info, mode);
	}


	@Override
	public RaidDangerLevel adjustCommodityDangerLevel(String commodityId, RaidDangerLevel level) {
		return level.next();
	}

	@Override
	public RaidDangerLevel adjustItemDangerLevel(String itemId, String data, RaidDangerLevel level) {
		return level.next();
	}


}