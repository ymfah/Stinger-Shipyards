package data.hullmods;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.campaign.fleet.MutableFleetStats;
import data.scripts.intel.SexHospitalShipListener;
import data.scripts.intel.SexHospitalCampaignListener;

import java.util.logging.Logger;

public class sex_HospitalShip extends BaseHullMod {
    private final Integer CREW_LOSS_REDUCTION = 40;
    private final Integer SUPPLY_INCREASE = 200;
@Override
public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
    //Logger.getLogger(this.getClass().getName()).info("ship creation");
    //modify stat
    stats.getDynamic().getStat("sex_global_crew_loss").modifyMult(id, 1.0F - CREW_LOSS_REDUCTION / 100.0F);
    stats.getSuppliesPerMonth().modifyMult(id,1.0F +SUPPLY_INCREASE/100);
    //crew loss in combat
    if (Global.getCurrentState()== GameState.COMBAT&&Global.getCombatEngine() != null && !Global.getCombatEngine().getListenerManager().hasListener(SexHospitalShipListener.class)) {
            Global.getCombatEngine().getListenerManager().addListener(new SexHospitalShipListener());
            //Logger.getLogger(this.getClass().getName()).info("listner live");
    }
}
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize){
        if(index==0){
            return ""+(int)CREW_LOSS_REDUCTION+"%";
        }else if(index==1){
       	    return ""+(int)SUPPLY_INCREASE+"%";
        }
        return null;
    }

}
