package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.CommandPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public class sex_TeleportFleetToSeastar implements CommandPlugin {

	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
			SectorEntityToken akdereEntity = Global.getSector().getEntityById("sex_akdere");
			if (akdereEntity != null) {
				teleportPlayerFleet(akdereEntity);
			}
		return false;
	}
	private void teleportPlayerFleet(SectorEntityToken destination) {
		SectorEntityToken playerFleet = Global.getSector().getPlayerFleet();
		playerFleet.setLocation(destination.getLocation().x, destination.getLocation().y);
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
