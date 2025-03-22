package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class sex_RosalinaAmmoFeeder extends BaseHullMod {
	public static final float ROF_BONUS = 1f;
	public static final float FLUX_REDUCTION = 70f;
	public static Object STATUS_KEY = new Object();

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float mult = 1f + ROF_BONUS * 1;
		stats.getBallisticRoFMult().modifyMult(id, mult);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));
		stats.getEnergyRoFMult().modifyMult(id, mult);
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * 0.01f));
		stats.getWeaponDamageTakenMult().modifyMult(id, 0.5f);
		stats.getEmpDamageTakenMult().modifyMult(id, 0.5f);
		stats.getWeaponDamageTakenMult().modifyMult(id, 0.85f);
		stats.getWeaponMalfunctionChance().modifyFlat(id, 0.03f);
	}

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);

		if (!ship.isAlive()) return;

		CombatEngineAPI engine = Global.getCombatEngine();
		boolean playerShip = ship == Global.getCombatEngine().getPlayerShip();


		String icon = Global.getSettings().getSpriteName("ui", "sex_rosalina_feeder");
		Global.getCombatEngine().maintainStatusForPlayerShip(
				STATUS_KEY, icon, "Integrated Ammo Feeders", "Active", false);
		}

	@Override
	public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();
		Color tQ = Misc.interpolateColor(Misc.getGrayColor(), Misc.getTextColor(), 0.05f);

		LabelAPI label = tooltip.addPara("“There's a reason the ol' brawlers have these things on a switch, yeah? The feeders practically cook themselves if you leave them running for too long. They're supposed to cycle, you know. Turn off. Rest. Not this.”", opad, h, "“" + "”");
		label.italicize();

		label = tooltip.addPara(" - Verbal report from your chief engineer.", opad, tQ, " - Verbal report from your chief engineer.");

		tooltip.addSectionHeading("Practical effects", Alignment.MID, opad);

		tooltip.addPara("Reduces ballistic and energy weapon flux use by %s.", opad, h,
				"" + (int) FLUX_REDUCTION + "%");
		tooltip.addPara("%s the fire rate of ballistic and energy weapons.", opad, h,
				"Hypothetically doubles");
		tooltip.addPara("%s the effects of EMP damage against ship systems.", opad, h,
				"Halves");
		tooltip.addPara("The system is highly unstable and may malfunction in combat.", opad, h);
	}
}