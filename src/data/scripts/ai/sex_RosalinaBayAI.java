package data.scripts.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.utils.CollisionUtils;
import data.scripts.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class sex_RosalinaBayAI implements MissileAIPlugin, GuidedMissileAI {

    private final CombatEngineAPI engine;
    private final MissileAPI missile;
    private final ShipAPI launchingShip;

    // List of ship variants to choose from
    private final List<String> shipVariants = new ArrayList<>();

    public sex_RosalinaBayAI(MissileAPI missile, ShipAPI launchingShip) {
        this.engine = Global.getCombatEngine();
        this.missile = missile;
        this.launchingShip = launchingShip;

        // Adding ship variants to the list
        shipVariants.add("wolf_Assault");
        shipVariants.add("wolf_Overdriven");
        shipVariants.add("wayfarer_Standard");
        shipVariants.add("lasher_Strike");
        shipVariants.add("vigilance_FS");
        shipVariants.add("vanguard_Strike");
        shipVariants.add("kite_hegemony_Interceptor");
        shipVariants.add("tempest_Attack");
        shipVariants.add("hyperion_Strike");
        shipVariants.add("hound_Standard");
        shipVariants.add("shepherd_Frontier");
        shipVariants.add("shade_Assault");
        shipVariants.add("shade_d_pirates_Assault");
        shipVariants.add("scarab_Experimental");
        shipVariants.add("gremlin_Strike");
        shipVariants.add("gremlin_luddic_path_Strike");
        shipVariants.add("omen_PD");
        shipVariants.add("cerberus_Overdriven");
        shipVariants.add("cerberus_Standard");
        shipVariants.add("centurion_Assault");
        shipVariants.add("brawler_Assault");
        shipVariants.add("brawler_pather_Raider");
        shipVariants.add("hermes_Standard");
        shipVariants.add("mudskipper2_Hellbore");
        shipVariants.add("monitor_Escort");
        shipVariants.add("afflictor_Strike");
        shipVariants.add("afflictor_d_pirates_Strike");
        shipVariants.add("sex_tartarus_Strike");
        shipVariants.add("mercury_Attack");
        shipVariants.add("mercury_FS");
        shipVariants.add("dram_Light");


        missile.setArmingTime(missile.getArmingTime() - (float) (Math.random() / 4));
    }

    @Override
    public void advance(float amount) {
        // Skip the AI if the game is paused, the missile is engineless or fading
        if (engine.isPaused()) {
            return;
        }

        if(!CollisionUtils.isPointWithinCollisionCircle(missile.getLocation(), launchingShip))
        //for(ShipAPI potentialTarget: CombatUtils.getShipsWithinRange(missile.getLocation(),500f))
        {
            if(MathUtils.getDistance(missile,launchingShip) > 80f && !CollisionUtils.isPointWithinBounds(missile.getLocation(),launchingShip))
            {
                missile.setArmingTime(0f);
                CombatFleetManagerAPI cfm = engine.getFleetManager(missile.getOwner());
                cfm.setSuppressDeploymentMessages(true);

                // Randomly select a ship variant from the list
                String randomShipVariant = getRandomShipVariant();

                ShipAPI pod = cfm.spawnShipOrWing(randomShipVariant, missile.getLocation(), 0f);
                pod.setOwner(missile.getSource().getOriginalOwner());
                pod.setFacing(missile.getFacing());
                pod.getVelocity().set(missile.getVelocity());
                pod.getMutableStats().getFighterRefitTimeMult().modifyPercent(pod.getId(), 9999f);
                pod.setMediumDHullOverlay();

                cfm.setSuppressDeploymentMessages(false);
            }
        }

        if (missile.isArmed()) {
            engine.removeEntity(missile);
        }
    }

    @Override
    public CombatEntityAPI getTarget() {
        return null;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        // Do nothing
    }

    // Method to get a random ship variant from the list
    private String getRandomShipVariant() {
        Random random = new Random();
        int index = random.nextInt(shipVariants.size());
        return shipVariants.get(index);
    }
}
