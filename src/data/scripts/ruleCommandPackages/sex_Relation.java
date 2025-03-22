package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class sex_Relation extends BaseCommandPlugin {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null)
            return false;
        int min = ((Misc.Token)params.get(0)).getInt(memoryMap);
        int max = ((Misc.Token)params.get(1)).getInt(memoryMap);
        SectorEntityToken entity = dialog.getInteractionTarget();
        if (entity.getActivePerson() == null)
            return false;
        if (max == 100)
            return (entity.getActivePerson().getRelToPlayer().getRepInt() >= min && entity.getActivePerson().getRelToPlayer().getRepInt() <= max);
        return (entity.getActivePerson().getRelToPlayer().getRepInt() >= min && entity.getActivePerson().getRelToPlayer().getRepInt() < max);
    }
}
