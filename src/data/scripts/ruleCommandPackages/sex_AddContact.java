package data.scripts.ruleCommandPackages;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

public class sex_AddContact extends BaseCommandPlugin {
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        SectorEntityToken entity = dialog.getInteractionTarget();
        if (entity == null)
            return false;
        PersonAPI person = null;
        if (params.size() > 0) {
            String personId = ((Misc.Token)params.get(0)).getString(memoryMap);
            ImportantPeopleAPI.PersonDataAPI data = Global.getSector().getImportantPeople().getData(personId);
            if (data != null)
                person = data.getPerson();
        }
        if (person == null)
            person = entity.getActivePerson();
        if (person == null)
            return false;
        int count = 0;
        for (IntelInfoPlugin intel : Global.getSector().getIntelManager().getIntel(ContactIntel.class)) {
            if (intel.isEnding() || intel.isEnded() || (
                    (ContactIntel)intel).getState() == ContactIntel.ContactState.POTENTIAL || (
                    (ContactIntel)intel).getState() == ContactIntel.ContactState.SUSPENDED || (
                    (ContactIntel)intel).getState() == ContactIntel.ContactState.LOST_CONTACT_DECIV)
                continue;
            count++;
        }
        if (count >= (int)Global.getSector().getPlayerStats().getDynamic().getMod("num_max_contacts_mod").computeEffective(Global.getSettings().getInt("maxContacts"))) {
            ContactIntel.addPotentialContact(1.0F, person, entity.getMarket(), dialog.getTextPanel());
        } else {
            ContactIntel intel2 = new ContactIntel(person, entity.getMarket());
            Global.getSector().getIntelManager().addIntel((IntelInfoPlugin)intel2, true, dialog.getTextPanel());
            intel2.develop(null);
            Global.getSoundPlayer().playUISound("ui_contact_developed", 1.0F, 1.0F);
            intel2.sendUpdate(null, dialog.getTextPanel());
        }
        return true;
    }
}