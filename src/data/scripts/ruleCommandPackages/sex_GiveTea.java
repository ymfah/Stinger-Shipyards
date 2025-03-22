package data.scripts.ruleCommandPackages;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;

public class sex_GiveTea extends BaseCommandPlugin {

	public sex_GiveTea() {

	}

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		// Assuming "playerFleet" is the player's fleet
		CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

		// Add the special item to the player's inventory
		addSpecialToPlayerInventory("industry_bp", "sex_tea", 1);

		return true;
	}

	private void addSpecialToPlayerInventory(String itemType, String itemId, int quantity) {
		CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();

		// Check if the player fleet is not null
		if (playerFleet != null) {
			// Add the special item to the player's inventory
			playerFleet.getCargo().addSpecial(new SpecialItemData(itemType, itemId), quantity);
		}
	}
}
