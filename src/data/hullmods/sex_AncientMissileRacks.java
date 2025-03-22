package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class sex_AncientMissileRacks extends BaseHullMod {

	public static final float ROF_PENALTY = 20f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//stats.getMissileRoFMult().modifyPercent(id, ROF_PENALTY);
		stats.getMissileRoFMult().modifyMult(id, 1f-ROF_PENALTY/100f);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ROF_PENALTY + "%";
		return null;
	}

}
