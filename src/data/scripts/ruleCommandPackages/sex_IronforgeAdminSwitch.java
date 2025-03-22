package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.CommandPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Voices;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class sex_IronforgeAdminSwitch implements CommandPlugin {

	@Override
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
		ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
		MarketAPI sex_edirnemarket = Global.getSector().getEconomy().getMarket("sex_edirnemarket");
		if (sex_edirnemarket == null) {
			out.println("IRONFORGE MARKET WAS NULL");
		} else if (Factions.HEGEMONY.equals(sex_edirnemarket.getFactionId())) {
			out.println("Ironforge Market was NOT null but getfactionid does NOT equal \"sex_co\"");
		}

		if (sex_edirnemarket != null && Factions.HEGEMONY.equals(sex_edirnemarket.getFactionId())) {

			PersonAPI sex_violet = ip.getPerson("sex_violet");

			PersonAPI sex_asuka = ip.getPerson("sex_asuka");
			sex_edirnemarket.getCommDirectory().removePerson(sex_asuka);
			sex_edirnemarket.removePerson(sex_asuka);

			if (sex_violet == null) {
				sex_violet = Global.getFactory().createPerson();
				sex_violet.setId("sex_violet");
				sex_violet.setFaction(Factions.HEGEMONY);
				sex_violet.setGender(FullName.Gender.FEMALE);
				sex_violet.setPostId(Ranks.POST_ADMINISTRATOR);
				sex_violet.setRankId(Ranks.SPACE_ADMIRAL);
				sex_violet.setImportance(PersonImportance.HIGH);
				sex_violet.getName().setFirst("Amelia");
				sex_violet.getName().setLast("Evergreen");
				sex_violet.setPortraitSprite("graphics/portraits/characters/sex_violet.png");
				sex_violet.getStats().setSkillLevel("industrial_planning", 1.0F);
				sex_violet.getStats().setSkillLevel("space_operations", 1.0F);
				sex_violet.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
				sex_violet.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
				sex_violet.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
				sex_violet.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
				sex_violet.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 1);
				sex_violet.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
				sex_violet.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
				sex_violet.getStats().setLevel(7);
				sex_violet.setVoice(Voices.BUSINESS);
				ip.addPerson(sex_violet);
				sex_edirnemarket.setAdmin(sex_violet);
				sex_edirnemarket.getCommDirectory().addPerson(sex_violet, 1);
				sex_edirnemarket.addPerson(sex_violet);
				out.println("Executing secondary admin setup for Ironforge Market");
			}
		}
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
