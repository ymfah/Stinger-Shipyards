package data.scripts.world;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.campaign.RepLevel;

public class sex_Relations implements SectorGeneratorPlugin {
	@Override
    public void generate(SectorAPI sector) {
		initFactionRelationships(sector);
    }
	public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI sex_co = sector.getFaction("sex_co");

		sex_co.setRelationship(Factions.HEGEMONY, RepLevel.COOPERATIVE);
		sex_co.setRelationship(Factions.PERSEAN, RepLevel.SUSPICIOUS);
		sex_co.setRelationship(Factions.INDEPENDENT, RepLevel.FRIENDLY);
		sex_co.setRelationship(Factions.TRITACHYON, RepLevel.HOSTILE);
		sex_co.setRelationship(Factions.LUDDIC_CHURCH, RepLevel.FAVORABLE);
		sex_co.setRelationship(Factions.LUDDIC_PATH, RepLevel.VENGEFUL);
		sex_co.setRelationship(Factions.PIRATES, RepLevel.HOSTILE);
		sex_co.setRelationship(Factions.DIKTAT, RepLevel.SUSPICIOUS);
		sex_co.setRelationship(Factions.PLAYER, RepLevel.NEUTRAL);
    }
}
