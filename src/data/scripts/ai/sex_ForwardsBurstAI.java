package data.scripts.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;


public class sex_ForwardsBurstAI implements ShipSystemAIScript {

	private int CHARGES_TO_BURN = 3;//adjust based on system, more charges to burn means more reckless spending while enemy isn't vulnerable

    private ShipAPI ship;
    private float shipRange;
    private ShipSystemAPI system;
   private final IntervalUtil tracker = new IntervalUtil(0.17f, 0.24f);



    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
        float shipRange = 200;
        for (WeaponAPI curW : ship.getUsableWeapons()){
            if(!curW.getType().equals(WeaponAPI.WeaponType.MISSILE) && !curW.getSpec().getAIHints().contains(WeaponAPI.AIHints.PD) && !curW.isDecorative()){
                if (curW.getRange() > shipRange) shipRange = curW.getRange();
            }
        }
        shipRange -= 150;//-150 so it jumps into comfortably close range instead of doing a burndrive and jumping into just range and then falling behind
        this.shipRange = shipRange;
    }

    //the single dumbest thing I've written to be quite honest but it looks nicer in the if
    private boolean Within(float toCheck,float from,float to){ if(toCheck<to&&toCheck>from){return true;} return false;}

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
        tracker.advance(amount);

        if (tracker.intervalElapsed()) {
            boolean shouldUseSystem = false;

			//just skip all the checking if no enemies in range, could add a check if velocity is forwards and charges are full so it also moves around the battlefield faster
            if(!ship.areAnyEnemiesInRange()) {
                shouldUseSystem = false;
            }else{
                ShipAPI sTarget = ship.getShipTarget();
                float targtR = 0;
                boolean targFuxed = false;
                if(sTarget != null) {
                    targtR = Misc.getDistance(ship.getLocation(),sTarget.getLocation());
                    targFuxed = sTarget.getFluxLevel() > 0.7f && sTarget.getFluxLevel() > ship.getFluxLevel();
                    if(!targFuxed)targFuxed = sTarget.getFluxTracker().isOverloadedOrVenting();//check if targets flux level is vulnerable
                }

                if(!ship.getAIFlags().hasFlag(ShipwideAIFlags.AIFlags.BACKING_OFF)){
                     if(shipRange < targtR &&//close in on target
                             (system.getAmmo() > Math.max(0,system.getMaxAmmo()-CHARGES_TO_BURN) || targFuxed)){//jump in freely if full on ammo or target is vulnerable
                        shouldUseSystem = true;
                    }

                }
            }

            //activate if we should, deactivate if not
            if (shouldUseSystem) {
                activateSystem();
            } else {
                deactivateSystem();
            }
        }
    }


    private void activateSystem() {
        if (ship.getPhaseCloak().isActive() == false) {
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);//rightclick system activated/deactivated by shield command
		}   
    }
    private void deactivateSystem() {
        if (ship.getPhaseCloak().isActive() == true) {
                ship.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
		}
    }
}
