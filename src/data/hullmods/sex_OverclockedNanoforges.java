package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class sex_OverclockedNanoforges extends BaseHullMod {
	public static final float REFIT_TIME_PERCENT = 0.50f;
	public static float DAMAGE_INCREASE = 0.5f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFighterRefitTimeMult().modifyMult(id, 1f - REFIT_TIME_PERCENT);
	}

	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {

		MutableShipStatsAPI stats = fighter.getMutableStats();

		stats.getArmorDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f );
		stats.getShieldDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f );
		stats.getHullDamageTakenMult().modifyPercent(id, DAMAGE_INCREASE * 100f);
	}

	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		float effect = 1f;

		if (index == 0) return "" + (int) Math.round((1f - REFIT_TIME_PERCENT) * 100f) + "%";
		if (index == 1) return "" + (int) Math.round(DAMAGE_INCREASE * 100f * effect) + "%";
		return null;
	}


}