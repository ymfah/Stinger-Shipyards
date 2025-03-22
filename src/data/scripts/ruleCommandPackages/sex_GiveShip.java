package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.impl.campaign.rulecmd.UpdateMemory;
import com.fs.starfarer.api.util.Misc.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class sex_GiveShip extends BaseCommandPlugin {

    private List<FleetMemberAPI> FilteredPoolFunction(List<FleetMemberAPI> unfiltered_pool, String filter_hullID){
        List<FleetMemberAPI> filtered_pool = new ArrayList<>();
        for (FleetMemberAPI member : unfiltered_pool){
            if(Objects.equals(member.getHullId(), filter_hullID)) {
                filtered_pool.add(member);
            }
        }

        return filtered_pool;
    }

    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;

    protected float valueMult;
    protected float repMult;

    public boolean execute(String ruleId, final InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {

        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        valueMult = 1.5f;
        repMult = 0;

        if (command.equals("Vanguard") && Global.getSector().getPlayerFleet().getNumShips() > 1) {
            System.out.println("running ;new data.scripts.world.sex_ShipPicker().init(dialog);");
            dialog.showFleetMemberPickerDialog("Hand over a Vanguard to Rosalina.",
                    "Confirm",
                    "Back",
                    5,
                    5,
                    100,
                    true,
                    false,
                     FilteredPoolFunction(Global.getSector().getPlayerFleet().getMembersWithFightersCopy(), "vanguard"),
                    new FleetMemberPickerListener() {
                        @Override
                        public void pickedFleetMembers(List<FleetMemberAPI> members) {

                            if (members.isEmpty()){
                                return;
                            }
                            Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(members.get(0));
                            if (Objects.equals(members.get(0).getHullId(), "vanguard")){
                                Global.getSector().getMemoryWithoutUpdate().set("$sex_ProtOne_One", true);
                                ((RuleBasedDialog) dialog.getPlugin()).updateMemory();
                                FireBest.fire(null, dialog, memoryMap, "sex_ProtOne");
                            } else {
                                System.out.println("did NOT give vanguard");
                            }

                        }

                        @Override
                        public void cancelledFleetMemberPicking() {

                        }
                    });

        }

        if (command.equals("Scarab") && Global.getSector().getPlayerFleet().getNumShips() > 1) {
            System.out.println("running ;new data.scripts.world.sex_ShipPicker().init(dialog);");
            dialog.showFleetMemberPickerDialog("Hand over a Scarab to Rosalina.",
                    "Confirm",
                    "Back",
                    5,
                    5,
                    100,
                    true,
                    false,
                    FilteredPoolFunction(Global.getSector().getPlayerFleet().getMembersWithFightersCopy(), "scarab"),
                    new FleetMemberPickerListener() {
                        @Override
                        public void pickedFleetMembers(List<FleetMemberAPI> members) {

                            if (members.isEmpty()){
                                return;
                            }
                            Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(members.get(0));
                            if (Objects.equals(members.get(0).getHullId(), "scarab")){
                                Global.getSector().getMemoryWithoutUpdate().set("$sex_ProtOne_Two", true);
                                ((RuleBasedDialog) dialog.getPlugin()).updateMemory();
                                FireBest.fire(null, dialog, memoryMap, "sex_ProtOne");
                            } else {
                                System.out.println("did NOT give scarab");
                            }

                        }

                        @Override
                        public void cancelledFleetMemberPicking() {

                        }
                    });

        }

        return true;
    }

}
