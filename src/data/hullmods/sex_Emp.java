package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.ui.Alignment;

import java.awt.*;

public class sex_Emp extends BaseHullMod {

	private static final float WHEN_FUN_BEGINS = 5f; // Time in seconds when the fun begins
	private static final float EMP_ARC_RANGE = 5000f; // Max range of EMP arcs

	private float overloadTimer = 0f;
	private boolean isInCooldown = false;
	private boolean hasSpawnedExplosion = false;
	private boolean hasPlayedSound = false;
	private static final float PROFILE_INCREASE = 200f;
	private static final float EMP_INCREASE = 5f;

	Color empColor = new Color(164, 220, 255, 100);


	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getSensorProfile().modifyPercent(id, PROFILE_INCREASE);
		stats.getDynamic().getMod(Stats.ELECTRONIC_WARFARE_FLAT).modifyFlat(id, EMP_INCREASE);
		}

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);

		if (ship.getSystem().isActive()) {
			if (!isInCooldown) {
				overloadTimer += amount;
				if (overloadTimer >= WHEN_FUN_BEGINS - 1 && !hasPlayedSound) {
					Global.getSoundPlayer().playSound("sex_emp_spool", 1f, 4f, ship.getLocation(), ship.getVelocity());
					hasPlayedSound = true;
				}

				if (overloadTimer >= WHEN_FUN_BEGINS - 0.5 && !hasSpawnedExplosion) {
					Global.getCombatEngine().spawnExplosion(ship.getLocation(), ship.getVelocity(), empColor, 2000, 1f);
					hasSpawnedExplosion = true;
					for (ShipAPI allShips : Global.getCombatEngine().getShips()) {
						if (allShips.isAlive()) {
							spawnRandomEmpArcs(allShips);
						}
					}
				}

				if (overloadTimer >= WHEN_FUN_BEGINS) {
					Global.getSoundPlayer().playSound("sex_emp_fire", 1f, 3f, ship.getLocation(), ship.getVelocity());
					//for (DamagingProjectileAPI missiles : Global.getCombatEngine().getProjectiles()) {
					//	missiles.setHitpoints(0);
					//}
					for (ShipAPI hostileShips : Global.getCombatEngine().getShips()) {
						if (hostileShips.isAlive() && hostileShips.getOwner() != ship.getOwner()) {
							hostileShips.getFluxTracker().forceOverload(4);
							hostileShips.getFluxTracker().increaseFlux(999999999, true);
							hostileShips.getEngineController().forceFlameout();
						}
					}
					for (ShipAPI friendlyShips : Global.getCombatEngine().getShips()) {
						if (friendlyShips.isAlive() && friendlyShips.getOwner() == ship.getOwner()) {
							friendlyShips.getFluxTracker().forceOverload(0f);
							friendlyShips.getFluxTracker().increaseFlux(999999999, true);
							friendlyShips.getEngineController().forceFlameout();
						}
					}
					isInCooldown = true;
				}
			}
		} else {
			// Reset overload timer, cooldown flag, and explosion flag
			overloadTimer = 0f;
			isInCooldown = false;
			hasSpawnedExplosion = false;
			hasPlayedSound = false;
		}
	}

	private void spawnRandomEmpArcs(ShipAPI ship) {
		int numArcs = 3; // Number of EMP arcs to spawn

		for (int i = 0; i < numArcs; i++) {
			ShipAPI targetShip = getRandomShip();
			if (targetShip != null) {
				Global.getCombatEngine().spawnEmpArc(ship, ship.getLocation(), null, targetShip,
						DamageType.ENERGY,
						100,
						500, // EMP strength
						EMP_ARC_RANGE, // Max range
						"tachyon_lance_emp_impact",
						20f, // Thickness
						new Color(25, 100, 155, 255),
						new Color(255, 255, 255, 255)
				);
			}
		}
	}

	private ShipAPI getRandomShip() {
		// Get a random ship from the combat engine's ship list
		if (!Global.getCombatEngine().getShips().isEmpty()) {
			int randomIndex = (int) (Math.random() * Global.getCombatEngine().getShips().size());
			return Global.getCombatEngine().getShips().get(randomIndex);
		}
		return null;
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

		LabelAPI label = tooltip.addPara("“The big... EMP stick, or whatever it is, would fry the whole ship if it was integrated into the hull, all the metal around would turn the ship into one massive conductor. The mad genius behind this design knew this, or realized it later, and mounted the whole thing on a ceramic exoframe. Heh, maintenance nightmare, I bet.”", opad, h, "“" + "”");
		label.italicize();

		label = tooltip.addPara(" - Verbal report from your chief engineer.", opad, tQ, " - Verbal report from your chief engineer.");

		tooltip.addSectionHeading("Practical effects", Alignment.MID, opad);

		tooltip.addPara("This ship's system releases a massive EMP burst %s. Hostile ships take longer to recover than friendlies by a margin of about a few seconds.", opad, h,
				"strong enough to take out every ship and fighter in the immediate area");

		tooltip.addPara("When deployed in combat, grants %s ECM rating.", opad, h,
				"" + (int) EMP_INCREASE + "%");
	}
}