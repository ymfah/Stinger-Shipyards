package data.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.util.Misc;

public class sex_NoVent extends BaseHullMod {

	private static final float FLUX_DISSIPATION_MULT = 2f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyMult(id, FLUX_DISSIPATION_MULT);

		stats.getVentRateMult().modifyMult(id, 0f);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return Misc.getRoundedValue(FLUX_DISSIPATION_MULT);
		if (index == 1) return "prevent the ship from actively venting flux";
		return null;
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
//		return !ship.getVariant().getHullMods().contains("unstable_injector") &&
//			   !ship.getVariant().getHullMods().contains("augmented_engines");
		if (ship.getVariant().getHullSize() == HullSize.CAPITAL_SHIP) return false;
		if (ship.getVariant().hasHullMod(HullMods.CIVGRADE) && !ship.getVariant().hasHullMod(HullMods.MILITARIZED_SUBSYSTEMS)) return false;
		if (ship.getVariant().hasHullMod(HullMods.FLUX_SHUNT)) return false;


		return true;
	}
}
