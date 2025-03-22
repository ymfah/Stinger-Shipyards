package data.scripts.ruleCommandPackages;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;
import com.fs.starfarer.api.util.Misc.VarAndMemory;
import com.fs.starfarer.api.campaign.FleetDataAPI;

/**
 *	AddShip <fleet member reference>
 */
public class sex_AddShip extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;

		FleetMemberAPI ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "sex_prot_two_Standard");
		ship.setShipName("Fuck You Mom");
		FleetDataAPI playerFleet = Global.getSector().getPlayerFleet().getFleetData();
		playerFleet.addFleetMember(ship);

		addShipGainText(ship, dialog.getTextPanel());
		return true;
	}

	public static void addShipGainText(FleetMemberAPI ship, TextPanelAPI text) {
		text.setFontSmallInsignia();
		text.addParagraph("Gained " + ship.getVariant().getFullDesignationWithHullNameForShip(), Misc.getPositiveHighlightColor());
		text.highlightInLastPara(Misc.getHighlightColor(), ship.getVariant().getFullDesignationWithHullNameForShip());
		text.setFontInsignia();
	}
}
