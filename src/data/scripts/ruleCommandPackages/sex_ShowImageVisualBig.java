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

import static java.util.logging.Logger.global;

/**
 * sex_ShowImageVisualBig <category> <key>
 */
public class sex_ShowImageVisualBig extends BaseCommandPlugin {

	public sex_ShowImageVisualBig() {
		
	}
	
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {


		String category = "illustrations";
		String key = null;
		
		if (params.size() <= 1) {
			key = params.get(0).string;
		} else {
			category = params.get(0).string;
			key = params.get(1).string;
		}
		
		SpriteAPI sprite = Global.getSettings().getSprite(category, key);
		//dialog.getVisualPanel().showImagePortion(category, key, sprite.getWidth(), sprite.getHeight(), 20, 220, 1300, 1000);
		dialog.getVisualPanel().showImagePortion(category, key, sprite.getWidth(), sprite.getHeight(), 120, 220, 1200, 1000);
		
		return true;
	}
}


