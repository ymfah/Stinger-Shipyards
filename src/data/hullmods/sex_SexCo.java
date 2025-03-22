package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class sex_SexCo extends BaseHullMod {

	public static final float AMMO_BONUS = 100f;
	public static float MAINTENANCE_MULT = 0.7f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMissileAmmoBonus().modifyPercent(id, AMMO_BONUS);
		stats.getSuppliesPerMonth().modifyMult(id, MAINTENANCE_MULT);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) AMMO_BONUS + "%";
		if (index == 1) return "" + (int) Math.round((1f - MAINTENANCE_MULT) * 100f) + "%";
		return null;
	}
}
