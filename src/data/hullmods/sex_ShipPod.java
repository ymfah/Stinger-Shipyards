package data.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class sex_ShipPod extends BaseHullMod {

	public static final float AMMO_BONUS = 100f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getMissileAmmoBonus().modifyPercent(id, AMMO_BONUS);
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

		LabelAPI label = tooltip.addPara("“It could work without an interface, yes, but there's no telling what comes out of that autofab. It's got a mind, almost. Like a child. That's the only way I can describe it. It's got a child's mind. We tried to fix some of its quirks, but it just, stopped every time. I think it knows.”", opad, h, "“" + "”");
		label.italicize();

		label = tooltip.addPara(" - Verbal report from your chief engineer.", opad, tQ, " - Verbal report from your chief engineer.");

		tooltip.addSectionHeading("Practical effects", Alignment.MID, opad);

		tooltip.addPara("The ship's system deploys %s when used.", opad, h,
				"a random frigate");
		tooltip.addPara("The system is highly esoteric and may produce unexpected results during combat.", opad, h);
	}
}