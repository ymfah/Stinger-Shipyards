package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.CommandPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class sex_EuterpeRosalinaCaretakerTooltipTrigger implements CommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		SectorEntityToken sex_akdere = Global.getSector().getEntityById("sex_akdere");
	SectorEntityToken sex_ankara = Global.getSector().getEntityById("sex_ankara");
        Global.getSector().getIntelManager().addIntel(new data.scripts.sex_EuterpeRosalinaCaretakerTooltip(sex_ankara,sex_akdere));
		return false;
	}
	@Override
	public boolean doesCommandAddOptions() {
		return false;
	}

	@Override
	public int getOptionOrder(List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		return 0;
	}
}