package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.CommandPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.world.ZigLeashAssignmentAI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.BaseFIDDelegate;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfigGen;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public class sex_SpawnOfficerEncounter implements CommandPlugin {

	public static String DEFEATED_LANGLEY = "$defeatedLangley";

	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		SectorEntityToken sex_target_planet = Global.getSector().getEntityById("sex_orhangazi");
		if (sex_target_planet != null) {
			addFleet(sex_target_planet);
		}
		return false;
	}

	public static class Sex_LagFIDConfig implements FleetInteractionDialogPluginImpl.FIDConfigGen {
		public FIDConfig createConfig() {
			FIDConfig config = new FIDConfig();

//			config.alwaysAttackVsAttack = true;
//			config.leaveAlwaysAvailable = true;
//			config.showFleetAttitude = false;
			config.showTransponderStatus = false;
			config.showEngageText = true;
			config.dismissOnLeave = true;
			//config.lootCredits = true;

			config.delegate = new BaseFIDDelegate() {
				public void postPlayerSalvageGeneration(InteractionDialogAPI dialog, FleetEncounterContext context, CargoAPI salvage) {
					new RemnantSeededFleetManager.RemnantFleetInteractionConfigGen().createConfig().delegate.
							postPlayerSalvageGeneration(dialog, context, salvage);
				}
				public void notifyLeave(InteractionDialogAPI dialog) {

					SectorEntityToken other = dialog.getInteractionTarget();
					if (!(other instanceof CampaignFleetAPI)) {
						dialog.dismiss();
						return;
					}
					CampaignFleetAPI fleet = (CampaignFleetAPI) other;

					if (!fleet.isEmpty()) {
						dialog.dismiss();
						return;
					}

					Global.getSector().getMemoryWithoutUpdate().set(DEFEATED_LANGLEY, true);
				}

				public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
					bcc.aiRetreatAllowed = false;
					bcc.objectivesAllowed = true;
					bcc.fightToTheLast = true;
					bcc.enemyDeployAll = true;
				}
			};
			return config;
		}
	}
	private void addFleet(SectorEntityToken sex_target_planet) {
		CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet(Factions.HEGEMONY, FleetTypes.PATROL_LARGE, null);
		fleet.setName("Strike Force Langley");
		fleet.setNoFactionInName(true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_REP_IMPACT, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_LOW_REP_IMPACT, true);
		fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);


		fleet.getFleetData().addFleetMember("onslaught_xiv_Elite");
		fleet.getFleetData().ensureHasFlagship();
		fleet.getFleetData().addFleetMember("dominator_XIV_Elite");
		fleet.getFleetData().addFleetMember("eagle_xiv_Elite");
		fleet.getFleetData().addFleetMember("sex_martinet_xiv_Elite");
		fleet.getFleetData().addFleetMember("sex_martinet_xiv_Elite");
		fleet.getFleetData().addFleetMember("enforcer_XIV_Elite");
		fleet.getFleetData().addFleetMember("enforcer_XIV_Elite");
		fleet.getFleetData().addFleetMember("enforcer_XIV_Elite");
		fleet.getFleetData().addFleetMember("enforcer_XIV_Elite");
		fleet.getFleetData().addFleetMember("sex_brawler_xiv_Elite");
		fleet.getFleetData().addFleetMember("sex_brawler_xiv_Elite");
		fleet.getMemoryWithoutUpdate().set("$sex_langley", true);

		ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
		PersonAPI sex_asuka = ip.getPerson("sex_asuka");
		fleet.setCommander(sex_asuka);

		FleetMemberAPI flagship = fleet.getFlagship();
		flagship.setCaptain(sex_asuka);
		flagship.updateStats();
		flagship.getRepairTracker().setCR(flagship.getRepairTracker().getMaxCR());
		flagship.setShipName("HSS Evangelion");

		Vector2f loc = new Vector2f(sex_target_planet.getLocation().x + 300 * ((float) Math.random() - 0.5f),
				sex_target_planet.getLocation().y + 300 * ((float) Math.random() - 0.5f));
		fleet.setLocation(loc.x, loc.y);
		sex_target_planet.getContainingLocation().addEntity(fleet);
		fleet.addScript(new ZigLeashAssignmentAI(fleet, sex_target_planet));
		fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_INTERACTION_DIALOG_CONFIG_OVERRIDE_GEN,
				new Sex_LagFIDConfig());
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
