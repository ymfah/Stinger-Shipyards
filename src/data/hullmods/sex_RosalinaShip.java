package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.CompromisedStructure;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class sex_RosalinaShip extends BaseHullMod {

	public static float CASUALTIES_PERCENT = 50f;
	public static float SUPPLY_USE_MULT = 2f;
	public static float MAX_CR_PENALTY = 0.1f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float effect = stats.getDynamic().getValue(Stats.DMOD_EFFECT_MULT);
		stats.getSuppliesPerMonth().modifyPercent(id, Math.round((SUPPLY_USE_MULT - 1f) * effect * 100f));
		stats.getCrewLossMult().modifyPercent(id, CASUALTIES_PERCENT);
		stats.getMaxCombatReadiness().modifyFlat(id, -Math.round(MAX_CR_PENALTY * effect * 100f) * 0.01f, "Arcane Engineering");
		CompromisedStructure.modifyCost(hullSize, stats, id);
	}

	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) { return null; }

	@Override
	public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();
		Color tQ = Misc.interpolateColor(Misc.getGrayColor(),Misc.getTextColor(),0.05f);

		LabelAPI label = tooltip.addPara("“...fuckin' ridiculous, look at this. It's a bunch of sketches and half-finished diagrams. Some vague warning about 'potential catastrophic failures' written with purple crayon at the end. Some components from Ludd-knows-where grafted onto the superstructure. You say some little girl designed this? She must be a monster. A brilliant genius but a monster nonetheless.”", opad, h,"“" + "”");
		label.italicize();

		label = tooltip.addPara(" - Verbal report from your chief engineer.",opad,tQ,
				" - Verbal report from your chief engineer.");

		tooltip.addSectionHeading("Practical effects", Alignment.MID, opad);

		tooltip.addPara("Monthly maintenance supply cost is increased by %s.", opad, h,
				"100%");
		tooltip.addPara("Crew casualties in combat are increased by %s.", opad, h,
				"50%");
		tooltip.addPara("Maximum combat readiness is reduced by %s.", opad, h,
				"10%");
	}
}