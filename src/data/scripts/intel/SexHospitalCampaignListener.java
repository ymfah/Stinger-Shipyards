package data.scripts.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import java.util.logging.Logger;

public class SexHospitalCampaignListener extends BaseCampaignEventListener {
    public SexHospitalCampaignListener(boolean permaRegister) {
        super(permaRegister);
    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        Logger l = Logger.getLogger(this.getClass().getName());
        super.reportShownInteractionDialog(dialog);
        float totalFleetHospitalShipBonus = 1f;
        for(FleetMemberAPI s:Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()){
            if(s.getVariant().hasHullMod("sex_hospital")) {
                totalFleetHospitalShipBonus = totalFleetHospitalShipBonus * s.getStats().getDynamic().getStat("sex_global_crew_loss").getModifiedValue();
            }
        }
        Global.getSector().getPlayerFleet().getStats().getDynamic().getStat("overall_crew_loss_mult").modifyMult(this.getClass().getName(), totalFleetHospitalShipBonus);
        //l.info("crew loss mult ="+totalFleetHospitalShipBonus);
    }
}
