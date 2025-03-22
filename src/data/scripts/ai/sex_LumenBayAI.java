package data.scripts.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.utils.CollisionUtils;
import data.scripts.utils.MathUtils;

public class sex_LumenBayAI implements MissileAIPlugin, GuidedMissileAI {

    private CombatEngineAPI engine;
    private final MissileAPI missile;
    private CombatEntityAPI target;
    private ShipAPI launchingShip;

    public sex_LumenBayAI(MissileAPI missile, ShipAPI launchingShip) {
        if (engine != Global.getCombatEngine()) {
            this.engine = Global.getCombatEngine();
        }
        this.missile = missile;
        this.launchingShip = launchingShip;
        missile.setArmingTime(missile.getArmingTime()-(float)(Math.random()/4));
    }

    @Override
    public void advance(float amount) {
        //skip the AI if the game is paused, the missile is engineless or fading
        if (engine.isPaused()){return;}

        if(!CollisionUtils.isPointWithinCollisionCircle(missile.getLocation(), launchingShip))
            //for(ShipAPI potentialTarget: CombatUtils.getShipsWithinRange(missile.getLocation(),500f))
            {
                if(MathUtils.getDistance(missile,launchingShip) > 80f && !CollisionUtils.isPointWithinBounds(missile.getLocation(),launchingShip))
                {
                    missile.setArmingTime(0f);
                    CombatFleetManagerAPI cfm = engine.getFleetManager(missile.getOwner());
                    cfm.setSuppressDeploymentMessages(true);
                    ShipAPI pod = cfm.spawnShipOrWing("lumen_Standard",missile.getLocation(),0f);
                    pod.setOwner(missile.getSource().getOriginalOwner());
                    pod.setFacing(missile.getFacing());
                    pod.getVelocity().set(missile.getVelocity());
                    pod.getMutableStats().getFighterRefitTimeMult().modifyPercent(pod.getId(),9999f);

                    cfm.setSuppressDeploymentMessages(false);
                }
            }

        if(missile.isArmed())
        {
            engine.removeEntity(missile);
        }
    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target = target;
    }
}