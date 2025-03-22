package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class sex_PhaseJitters extends BaseHullMod {

	private boolean lisMade = false;
	private ShipAPI pod; // Reference to the spawned ship
	private static float disappearDuration = 8f; // Duration before the spawned ship disappears (in seconds)
	private final List<String> ghostVariants = new ArrayList<>();


	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		super.advanceInCombat(ship, amount);
		CombatEngineAPI engine = Global.getCombatEngine();

		if (!lisMade) {
			ghostVariants.add("sex_prot_seven_Exotic");
			ghostVariants.add("sex_prot_seven_Temporal");
			ghostVariants.add("sex_prot_seven_Artillery");
			ghostVariants.add("sex_prot_seven_Hammer");
			ghostVariants.add("sex_prot_seven_Beam");
			ghostVariants.add("sex_prot_seven_EMP");
			ghostVariants.add("sex_prot_seven_PD");
			ghostVariants.add("sex_prot_seven_Charge");
			ghostVariants.add("sex_prot_seven_Mining");
			ghostVariants.add("sex_prot_seven_Shielbreaker");
			lisMade = true;
		}
		String GhostSpawnedKey = ship.getId() + "sex_PhaseJitters";
		Boolean spawning = (Boolean) engine.getCustomData().get(GhostSpawnedKey);
		if(spawning == null) {
			engine.getCustomData().put(GhostSpawnedKey, false);
			spawning = (Boolean) engine.getCustomData().get(GhostSpawnedKey);
		}

		if (!spawning && ship.getSystem().isChargeup()) {
			spawnShip(ship, engine);
			engine.getCustomData().put(GhostSpawnedKey, true);

		} else if (ship.getSystem().isChargedown()) {
			engine.getCustomData().put(GhostSpawnedKey, false);
		}


	}

	private void spawnShip(ShipAPI ship, CombatEngineAPI engine) {
		CombatFleetManagerAPI cfm = engine.getFleetManager(ship.getOwner());

		cfm.setSuppressDeploymentMessages(true);
		String randomShipVariant = getRandomShipVariant();

		pod = cfm.spawnShipOrWing(randomShipVariant, ship.getLocation(), 0f);
		pod.addListener(new GhostListener((CombatEntityAPI)pod));
		pod.setShipSystemDisabled(true);
		pod.getMutableStats().getHullDamageTakenMult().modifyMult("sex_PhaseJitters", 0f);
		pod.getMutableStats().getArmorDamageTakenMult().modifyMult("sex_PhaseJitters", 0f);
		pod.getMutableStats().getEmpDamageTakenMult().modifyMult("sex_PhaseJitters", 0f);
		pod.setOwner(ship.getOwner());
		pod.setFacing(ship.getFacing());
		pod.getVelocity().set(ship.getVelocity());
		pod.setJitter(this, new Color(255, 0, 223, (200)), 1f, 10, 1f, 220);
		pod.setCircularJitter(true);
		pod.setExplosionScale(0.2f);
		pod.setExplosionFlashColorOverride(new Color(91, 6, 252, 255));
		pod.setDrone(true);
		pod.setForceHideFFOverlay(true);

		cfm.setSuppressDeploymentMessages(false);
	}


	public static class GhostListener implements AdvanceableListener{
		public final CombatEntityAPI pod;
		public float elapsedTime = 0f; // Time elapsed since the ship was spawned
		public GhostListener(CombatEntityAPI pod) {
			this.pod = pod;
		}
		@Override
		public void advance(float amount) {
			elapsedTime += Global.getCombatEngine().getElapsedInLastFrame();
			if (elapsedTime >= disappearDuration) {
				CombatEngineAPI engine = Global.getCombatEngine();
				engine.removeEntity(this.pod);
				engine.getFleetManager(this.pod.getOwner()).removeDeployed((ShipAPI)this.pod, false);
				((ShipAPI)this.pod).removeListener(this);
			} else {
				// Gradually fade out the ship
				((ShipAPI)this.pod).setAlphaMult((disappearDuration - elapsedTime)/disappearDuration);
			}

		}
	}

	private String getRandomShipVariant() {
		Random random = new Random();
		int index = random.nextInt(ghostVariants.size());
		return ghostVariants.get(index);
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

		LabelAPI label = tooltip.addPara("“Superposition displacement, I'd wager, though I haven't seen anything like it outside of academic theory. I'd call it impossible if I wasn't looking at the hardware right now. Makes funny noises when you skim it with the sensor suite. Sounds like it's humming a song, almost.”", opad, h,"“" + "”");
		label.italicize();

		label = tooltip.addPara(" - Verbal report from your chief engineer.",opad,tQ,
				" - Verbal report from your chief engineer.");
	}
}