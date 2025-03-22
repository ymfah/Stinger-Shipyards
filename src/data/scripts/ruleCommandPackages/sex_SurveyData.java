package data.scripts.ruleCommandPackages;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.CustomRepImpact;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActionEnvelope;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin.RepActions;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.FireBest;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

/**
 * NotifyEvent $eventHandle <params> 
 * 
 */
public class sex_SurveyData extends BaseCommandPlugin {
	
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
	protected PersonAPI person;
	protected FactionAPI faction;
	protected FactionAPI faction2;

	protected boolean buysAICores;
	protected float valueMult;
	protected float repMult;
	
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		
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

		faction = Global.getSector().getFaction("independent");
		faction2 = Global.getSector().getFaction("sex_agreus_dummy");

		valueMult = 1.5f;
		repMult = 0;
		
		if (command.equals("selectSurveyData")) {
			selectSurveyData();
		} else if (command.equals("playerHasSurvey")) {
			return playerHasSurvey();
		}
		
		return true;
	}
	protected void selectSurveyData() {
		CargoAPI copy = Global.getFactory().createCargo(false);
		//copy.addAll(cargo);
		//copy.setOrigSource(playerCargo);
		for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.SURVEY_DATA)) {
				copy.addFromStack(stack);
			}
		}
		copy.sort();
		
		final float width = 310f;
		dialog.showCargoPickerDialog("Select survey data pads to turn in", "Confirm", "Cancel", true, width, copy, new CargoPickerListener() {
			public void pickedCargo(CargoAPI cargo) {
				if (cargo.isEmpty()) {
					cancelledCargoSelection();
					return;
				}
				
				cargo.sort();
				for (CargoStackAPI stack : cargo.getStacksCopy()) {
					playerCargo.removeItems(stack.getType(), stack.getData(), stack.getSize());
					if (stack.isCommodityStack()) { // should be always, but just in case
						int num = (int) stack.getSize();
						AddRemoveCommodity.addCommodityLossText(stack.getCommodityId(), num, text);
					}
				}
				
				float bounty = computeSurveyCreditValue(cargo);
				float repChange = 0;
				//computeSurveyReputationValue(cargo);

				if (bounty > 0) {
					playerCargo.getCredits().add(bounty);
					AddRemoveCommodity.addCreditsGainText((int)bounty, text);
				}
				
				if (repChange >= 1f) {
					CustomRepImpact impact = new CustomRepImpact();
					impact.delta = repChange * 0.01f;
					Global.getSector().adjustPlayerReputation(
							new RepActionEnvelope(RepActions.CUSTOM, impact,
												  null, text, true), 
												  faction.getId());
					
					impact.delta *= 0.25f;
					if (impact.delta >= 0.01f) {
						Global.getSector().adjustPlayerReputation(
								new RepActionEnvelope(RepActions.CUSTOM, impact,
													  null, text, true), 
													  person);
					}
				}
				
				FireBest.fire(null, dialog, memoryMap, "SurveyDataTurnedIn");
			}
			public void cancelledCargoSelection() {
			}
			public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {
			
				float bounty = computeSurveyCreditValue(combined);
				float repChange = 0;
				//computeSurveyReputationValue(combined)
				
				float pad = 3f;
				float small = 5f;
				float opad = 10f;

				panel.setParaFontOrbitron();
				panel.addPara(Misc.ucFirst("The Eridani-Utopia company"), faction.getBaseUIColor(), 1f);
				//panel.addTitle(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor());
				//panel.addPara(faction.getDisplayNameLong(), faction.getBaseUIColor(), opad);
				//panel.addPara(faction.getDisplayName() + " (" + entity.getMarket().getName() + ")", faction.getBaseUIColor(), opad);
				panel.setParaFontDefault();
				
				panel.addImage(faction2.getLogo(), width * 1f, 3f);

				
				panel.addPara("The Eridani Utopia cartographers' guild offers above-market rates for survey data. They offer you %s for this survey data.",
						opad * 1f, Misc.getHighlightColor(),
						Misc.getWithDGS(bounty) + Strings.C,
						"" + (int) repChange);
				
				//+ faction.getDisplayNameWithArticle()
				//panel.addPara("Bounty: %s", opad, Misc.getHighlightColor(), Misc.getWithDGS(bounty) + Strings.C);
				//panel.addPara("Reputation: %s", pad, Misc.getHighlightColor(), "+12");
			}
		});
	}

	protected float computeSurveyCreditValue(CargoAPI cargo) {
		float bounty = 0;
		for (CargoStackAPI stack : cargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.SURVEY_DATA)) {
				bounty += spec.getBasePrice() * stack.getSize();
			}
		}
		bounty *= valueMult;
		return bounty;
	}
	
	protected float computeSurveyReputationValue(CargoAPI cargo) {
		float rep = 0;
		for (CargoStackAPI stack : cargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.SURVEY_DATA)) {
				rep += getBaseRepValue(spec.getId()) * stack.getSize();
			}
		}
		rep *= repMult;
		//if (rep < 1f) rep = 1f;
		return rep;
	}
	
	public static float getBaseRepValue(String coreType) {
		if (Commodities.SURVEY_DATA_5.equals(coreType)) {
			return 0.5f;
		}
		if (Commodities.SURVEY_DATA_4.equals(coreType)) {
			return 0.4f;
		}
		if (Commodities.SURVEY_DATA_3.equals(coreType)) {
			return 0.3f;
		}
		if (Commodities.SURVEY_DATA_2.equals(coreType)) {
			return 0.2f;
		}
		if (Commodities.SURVEY_DATA_1.equals(coreType)) {
			return 0.1f;
		}
		return 1f;
	}
	
	
	protected boolean playerHasSurvey() {
		for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
			CommoditySpecAPI spec = stack.getResourceIfResource();
			if (spec != null && spec.getDemandClass().equals(Commodities.SURVEY_DATA)) {
				return true;
			}
		}
		return false;
	}

	
	
}















