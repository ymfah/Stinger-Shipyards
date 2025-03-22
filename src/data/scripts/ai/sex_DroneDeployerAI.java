package data.scripts.ai;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;


public class sex_DroneDeployerAI implements ShipSystemAIScript {


    private ShipAPI ship;
    private float shipRange;
    private ShipSystemAPI system;
   private final IntervalUtil tracker = new IntervalUtil(0.17f, 0.24f);



    @Override
    public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
        this.ship = ship;
        this.system = system;
    }
    private boolean Within(float toCheck,float from,float to){ if(toCheck<to&&toCheck>from){return true;} return false;}

    @Override
    public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
            if(ship.areAnyEnemiesInRange()) {
                if (ship.getFluxTracker().getFluxLevel() < 0.92f){
                    if(!ship.getSystem().isCoolingDown() && !ship.getSystem().isOutOfAmmo() && !ship.getSystem().isChargedown() && !ship.getSystem().isActive()){
                    ship.giveCommand(ShipCommand.USE_SYSTEM, null, 0);
                    }
                }
            }
        }
    }