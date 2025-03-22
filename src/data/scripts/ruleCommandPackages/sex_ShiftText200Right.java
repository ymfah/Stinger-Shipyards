package data.scripts.ruleCommandPackages;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * sex_ShowImageVisualBig <category> <key>
 */
public class sex_ShiftText200Right extends BaseCommandPlugin {

	public sex_ShiftText200Right() {

	}

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {

		if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null) {
			InteractionDialogAPI interactionDialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
			float xOffset = 0f;
			interactionDialog.setXOffset(xOffset);
		}
		return true;
	}
}


