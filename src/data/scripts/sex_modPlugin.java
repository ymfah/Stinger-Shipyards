package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import data.scripts.ai.sex_GlimmerBayAI;
import data.scripts.ai.sex_LumenBayAI;
import data.scripts.ai.sex_RosalinaBayAI;
import data.scripts.intel.SexHospitalCampaignListener;
import data.scripts.intel.SexWreckWranglerListener;
import data.scripts.world.sex_WorldGen;

import java.util.logging.Logger;

import static java.lang.System.out;

public class sex_modPlugin extends BaseModPlugin {


    public static final String glimmer_ID = "sex_GlimmerMissile";
    public static final String lumen_ID = "sex_LumenMissile";
    public static final String rosalina_ID = "sex_Ship_Pod_Projectile";
    @Override
    public void onNewGame() {
        new sex_WorldGen().generate(Global.getSector());

    }
    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip)    {
        switch (missile.getProjectileSpecId()) {
            case glimmer_ID:
                return new PluginPick<MissileAIPlugin>(new sex_GlimmerBayAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case lumen_ID:
                return new PluginPick<MissileAIPlugin>(new sex_LumenBayAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case rosalina_ID:
                return new PluginPick<MissileAIPlugin>(new sex_RosalinaBayAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
        }
        return null;
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        MarketAPI nova = Global.getSector().getEconomy().getMarket("new_maxios");
        SectorEntityToken novaPlanet = (SectorEntityToken) Global.getSector().getEntityById("new_maxios");
        if (nova == null) {
            out.println("new_maxios MARKET WAS NULL");
        } else {
            Industry industry = nova.getIndustry("sex_maxios");
            if (industry == null) {
                nova.addIndustry("sex_maxios");
                novaPlanet.setInteractionImage("misc", "sex_maxios");
                out.println("sex_maxios industry added to new_maxios market.");
            } else {
                out.println("sex_maxios industry already exists on new_maxios market.");
            }
        }


        MarketAPI agreus = Global.getSector().getEconomy().getMarket("agreus");
        PlanetAPI agreusPlanet = (PlanetAPI) Global.getSector().getEntityById("agreus");
        if (agreus == null) {
            out.println("nomios MARKET WAS NULL");
        } else {
            Industry industry = agreus.getIndustry("sex_agreus");
            if (industry == null) {
                agreus.addIndustry("sex_agreus");
                agreusPlanet.setInteractionImage("misc", "sex_ko_combine");
                out.println("sex_agreus industry added to agreus market.");
            } else {
                out.println("sex_agreus industry already exists on agreus market.");
            }
        }

        MarketAPI orthrus = Global.getSector().getEconomy().getMarket("orthrus");
        StarSystemAPI system = Global.getSector().getStarSystem("Samarra");
        SectorEntityToken orthrusPlanet = Global.getSector().getEntityById("orthrus");
        if (orthrus == null) {
            out.println("orthrus MARKET WAS NULL");
        } else {
                orthrus.addIndustry(Industries.TECHMINING);
                orthrus.addIndustry(Industries.WAYSTATION);
                orthrus.addCondition(Conditions.RUINS_SCATTERED);
                orthrusPlanet.setCustomDescriptionId("orthrus");
                orthrusPlanet.setInteractionImage("misc", "sex_eridani");

                orthrus.reapplyIndustries();
                orthrus.reapplyConditions();

                SectorEntityToken sex_Hypersat = system.addCustomEntity("sex_Hypersat", null, "sex_Hypersat", "neutral");
                sex_Hypersat.setCircularOrbitPointingDown(orthrusPlanet, 360, 100, 12 );
                sex_Hypersat.setCustomDescriptionId("sex_hypersat");
                out.println("orthrus market reworked.");
            }



    MarketAPI nomios = Global.getSector().getEconomy().getMarket("nomios");
        PlanetAPI nomiosPlanet = (PlanetAPI) Global.getSector().getEntityById("nomios");
        if (nomios == null) {
        out.println("nomios MARKET WAS NULL");
    } else {
        Industry industry = nomios.getIndustry("sex_maxios");
        if (industry == null) {
            nomios.addIndustry("sex_nomios");
            nomios.addIndustry("sex_graveyard");
            nomios.addCondition("sex_graveyard_condition");
            nomiosPlanet.setInteractionImage("misc", "sex_mbaye");
            nomios.reapplyConditions();
            nomios.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
            out.println("sex_nomios industry added to nomios market.");
        } else {
            out.println("sex_nomios industry already exists on nomios market.");
        }
    }
}



    @Override
    public void onApplicationLoad() {
        Logger sex_logger2 = Logger.getLogger(this.getClass().getName());
        sex_logger2.info("Pre-Initializing sex_Menu plugin...");
        Logger sex_logger = Logger.getLogger(this.getClass().getName());
        sex_logger.info("Initializing sex_Menu plugin...");
        if (Global.getCurrentState() == GameState.TITLE) {
            out.println("Adding sex_Menu plugin...");
            Global.getCombatEngine().addPlugin(new sex_Menu());
        }
    }

    @Override
    public void onGameLoad(boolean newGame) {
        Global.getSector().addTransientListener(new SexHospitalCampaignListener(false));
        Global.getSector().addTransientListener(new SexWreckWranglerListener(false));

        if (Global.getSettings().getModManager().isModEnabled("timid_xiv")) {
            MarketAPI eis_chitagupta = Global.getSector().getEconomy().getMarket("eis_chitagupta");
        if (eis_chitagupta == null) {
            out.println("eis_chitagupta MARKET WAS NULL");
        } else {
            Industry industry = eis_chitagupta.getIndustry("sex_branch");
            if (industry == null) {
                eis_chitagupta.addIndustry("sex_branch");
                out.println("sex_branch industry added to eis_chitagupta market.");
            } else {
                out.println("sex_branch industry already exists on eis_chitagupta market.");
            }
        }
        }

        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        MarketAPI sex_wreckwanglermarket = Global.getSector().getEconomy().getMarket("sex_wreckwanglermarket");
        if (sex_wreckwanglermarket == null) {
            out.println("WRECKWANGLER MARKET WAS NULL");
        } else if ("sex_co".equals(sex_wreckwanglermarket.getFactionId())) {
            out.println("Wreckwangler Market was NOT null but getfactionid does NOT equal \"sex_co\"");
        }

        if (sex_wreckwanglermarket != null && "sex_co".equals(sex_wreckwanglermarket.getFactionId())) {

            PersonAPI sex_tia = ip.getPerson("sex_tia");

            if (sex_tia == null) {
                sex_tia = Global.getFactory().createPerson();
            sex_tia.setId("sex_tia");
            sex_tia.setFaction("sex_co");
            sex_tia.setGender(FullName.Gender.FEMALE);
            sex_tia.setPostId(Ranks.POST_STATION_COMMANDER);
            sex_tia.setRankId(Ranks.POST_ADMINISTRATOR);
            sex_tia.setImportance(PersonImportance.HIGH);
            sex_tia.getName().setFirst("Tia");
            sex_tia.getName().setLast("Tsai");
            sex_tia.setPortraitSprite(Global.getSettings().getSpriteName("characters", "sex_mari"));
            sex_tia.getStats().setSkillLevel("industrial_planning", 1.0F);
            sex_tia.getStats().setLevel(4);
            sex_tia.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
            sex_tia.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "sex_co");
            sex_tia.setVoice(Voices.OFFICIAL);
            ip.addPerson(sex_tia);
            sex_wreckwanglermarket.setAdmin(sex_tia);
            sex_wreckwanglermarket.getCommDirectory().addPerson(sex_tia, 1);
            sex_wreckwanglermarket.addPerson(sex_tia);
            out.println("Executing admin setup for Wreckwangler Market");
        } else {
            out.println("sex_euterpe already exists, skipping creation...");
        }
    }

        MarketAPI sex_ankaramarket = Global.getSector().getEconomy().getMarket("sex_ankaramarket");
        if (sex_ankaramarket == null) {
            out.println("ANKARA MARKET WAS NULL");
        } else if ("sex_co".equals(sex_ankaramarket.getFactionId())) {
            out.println("Ankara Market was NOT null but getfactionid does NOT equal \"sex_co\"");
        }

        if (sex_ankaramarket != null && "sex_co".equals(sex_ankaramarket.getFactionId())) {

            PersonAPI sex_euterpe = ip.getPerson("sex_euterpe");

            if (sex_euterpe == null) {
                sex_euterpe = Global.getFactory().createPerson();
                sex_euterpe.setId("sex_euterpe");
                sex_euterpe.setFaction("sex_co");
                sex_euterpe.setGender(FullName.Gender.FEMALE);
                sex_euterpe.setPostId(Ranks.POST_FACTION_LEADER);
                sex_euterpe.setRankId(Ranks.FACTION_LEADER);
                sex_euterpe.setImportance(PersonImportance.VERY_HIGH);
                sex_euterpe.getName().setFirst("Euterpe");
                sex_euterpe.getName().setLast("Cybele");
                sex_euterpe.setPortraitSprite(Global.getSettings().getSpriteName("characters", "sex_euterpe"));
                sex_euterpe.getStats().setSkillLevel("industrial_planning", 1.0F);
                sex_euterpe.getStats().setSkillLevel("space_operations", 1.0F);
                sex_euterpe.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
                sex_euterpe.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
                sex_euterpe.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
                sex_euterpe.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
                sex_euterpe.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 1);
                sex_euterpe.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
                sex_euterpe.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
                sex_euterpe.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
                sex_euterpe.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
                sex_euterpe.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
                sex_euterpe.getStats().setLevel(9);
                sex_euterpe.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
                sex_euterpe.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "sex_co");
                sex_euterpe.setVoice(Voices.OFFICIAL);
                sex_euterpe.addTag(Tags.CONTACT_MILITARY);
                sex_euterpe.addTag(Tags.CONTACT_UNDERWORLD);
                ip.addPerson(sex_euterpe);
                sex_ankaramarket.setAdmin(sex_euterpe);
                sex_ankaramarket.getCommDirectory().addPerson(sex_euterpe, 1);
                sex_ankaramarket.addPerson(sex_euterpe);
                out.println("Executing admin setup for Ankara Market");
            } else {
                out.println("sex_euterpe already exists, skipping creation...");
            }
        }

        MarketAPI sex_akderemarket = Global.getSector().getEconomy().getMarket("sex_akderemarket");
        if (sex_akderemarket == null) {
            out.println("SEASTAR MARKET WAS NULL");
        } else if ("sex_co".equals(sex_akderemarket.getFactionId())) {
            out.println("Seastar Market was NOT null but getfactionid does NOT equal \"sex_co\"");
        }

        if (sex_akderemarket != null && "sex_co".equals(sex_akderemarket.getFactionId())) {

            PersonAPI sex_rosalina = ip.getPerson("sex_rosalina");

            if (sex_rosalina == null) {
                sex_rosalina = Global.getFactory().createPerson();
            sex_rosalina.setId("sex_rosalina");
            sex_rosalina.setFaction("sex_co");
            sex_rosalina.setGender(FullName.Gender.FEMALE);
            sex_rosalina.setPostId(Ranks.POST_NOVICE);
            sex_rosalina.setRankId(Ranks.POST_ADMINISTRATOR);
            sex_rosalina.setImportance(PersonImportance.VERY_HIGH);
            sex_rosalina.getName().setFirst("Rosalina");
            sex_rosalina.getName().setLast("Cybele");
            sex_rosalina.setPortraitSprite(Global.getSettings().getSpriteName("characters", "sex_rosalina"));
            sex_rosalina.getStats().setSkillLevel("industrial_planning", 1.0F);
            sex_rosalina.getStats().setSkillLevel("space_operations", 1.0F);
            sex_rosalina.getStats().setSkillLevel(Skills.HELMSMANSHIP, 1);
            sex_rosalina.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1);
            sex_rosalina.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
            sex_rosalina.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 1);
            sex_rosalina.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 2);
            sex_rosalina.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
            sex_rosalina.getStats().setSkillLevel(Skills.CREW_TRAINING, 1);
            sex_rosalina.getStats().setSkillLevel(Skills.SUPPORT_DOCTRINE, 1);
            sex_rosalina.getStats().setLevel(3);
            sex_rosalina.setVoice(Voices.SPACER);
            sex_rosalina.addTag(Tags.CONTACT_TRADE);
            sex_rosalina.addTag(Tags.CONTACT_SCIENCE);
            ip.addPerson(sex_rosalina);
            sex_akderemarket.setAdmin(sex_rosalina);
            sex_akderemarket.getCommDirectory().addPerson(sex_rosalina, 1);
            sex_akderemarket.addPerson(sex_rosalina);
            out.println("Executing admin setup for Seastar Market");
            } else {
                out.println("sex_rosalina already exists, skipping creation...");
            }
        }

        MarketAPI sex_edirnemarket = Global.getSector().getEconomy().getMarket("sex_edirnemarket");
        if (sex_edirnemarket == null) {
            out.println("IRONFORGE MARKET WAS NULL");
        } else if (Factions.HEGEMONY.equals(sex_edirnemarket.getFactionId())) {
            out.println("Ironforge Market was NOT null but getfactionid does NOT equal \"sex_co\"");
        }

        if (sex_edirnemarket != null && Factions.HEGEMONY.equals(sex_edirnemarket.getFactionId())) {

            PersonAPI sex_asuka = ip.getPerson("sex_asuka");

            if (sex_asuka == null) {
                sex_asuka = Global.getFactory().createPerson();
            sex_asuka.setId("sex_asuka");
            sex_asuka.setFaction(Factions.HEGEMONY);
            sex_asuka.setGender(FullName.Gender.FEMALE);
            sex_asuka.setPostId(Ranks.POST_ADMINISTRATOR);
            sex_asuka.setRankId(Ranks.SPACE_ADMIRAL);
            sex_asuka.setImportance(PersonImportance.HIGH);
            sex_asuka.getName().setFirst("Asuka");
            sex_asuka.getName().setLast("Langley");
            sex_asuka.setPortraitSprite(Global.getSettings().getSpriteName("characters", "sex_asuka"));
            sex_asuka.getStats().setSkillLevel("industrial_planning", 1.0F);
            sex_asuka.getStats().setSkillLevel("space_operations", 1.0F);
                sex_asuka.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
                sex_asuka.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 2);
                sex_asuka.getStats().setSkillLevel(Skills.IMPACT_MITIGATION, 1);
                sex_asuka.getStats().setSkillLevel(Skills.DAMAGE_CONTROL, 2);
                sex_asuka.getStats().setSkillLevel(Skills.BALLISTIC_MASTERY, 1);
                sex_asuka.getStats().setSkillLevel(Skills.SYSTEMS_EXPERTISE, 2);
                sex_asuka.getStats().setSkillLevel(Skills.TACTICAL_DRILLS, 1);
                sex_asuka.getStats().setLevel(7);
            sex_asuka.setVoice(Voices.BUSINESS);
            ip.addPerson(sex_asuka);
            sex_edirnemarket.setAdmin(sex_asuka);
            sex_edirnemarket.getCommDirectory().addPerson(sex_asuka, 1);
            sex_edirnemarket.addPerson(sex_asuka);

                PersonAPI sex_barker = ip.getPerson("sex_barker");

                if (sex_barker == null) {
                    sex_barker = Global.getFactory().createPerson();
                    sex_barker.setId("sex_barker");
                    sex_barker.setFaction(Factions.HEGEMONY);
                    sex_barker.setGender(FullName.Gender.FEMALE);
                    sex_barker.setPostId(Ranks.POST_ADMINISTRATOR);
                    sex_barker.setRankId(Ranks.POST_ARISTOCRAT);
                    sex_barker.setImportance(PersonImportance.MEDIUM);
                    sex_barker.getName().setFirst("Will");
                    sex_barker.getName().setLast("Barker");
                    sex_barker.setPortraitSprite(Global.getSettings().getSpriteName("characters", "sex_barker"));
                    sex_barker.getStats().setSkillLevel("space_operations", 1.0F);
                    sex_barker.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
                    sex_barker.getStats().setSkillLevel(Skills.COMBAT_ENDURANCE, 1);
                    sex_barker.getStats().setLevel(4);
                    sex_barker.setVoice(Voices.BUSINESS);
                    ip.addPerson(sex_barker);
                }
            out.println("Executing setup for will barker");
            } else {
                out.println("sex_barker already exists, skipping creation...");
            }
        }

        //initialise branch offices
        //MarketAPI sex_ankaramarket = Global.getSector().getEconomy().getMarket("sex_ankaramarket");
        //if (sex_ankaramarket == null) {
        //    out.println("ANKARA MARKET WAS NULL");
        //} else if ("sex_co".equals(sex_ankaramarket.getFactionId())) {
        //    out.println("Ankara Market was NOT null but getfactionid does NOT equal \"sex_co\"");
        //}
        //sex_ankaramarket.addSubmarket("sex_market");

        //MarketAPI sex_wreckwanglermarket = Global.getSector().getEconomy().getMarket("sex_wreckwanglermarket");
        //if (sex_wreckwanglermarket == null) {
        //    out.println("WRECKWANGLER MARKET WAS NULL");
        //} else if ("sex_co".equals(sex_wreckwanglermarket.getFactionId())) {
        //    out.println("Wreckwangler market was NOT null but getfactionid does NOT equal \"sex_co\"");
        //}
        sex_wreckwanglermarket.addSubmarket("sex_market");

        MarketAPI nortia = Global.getSector().getEconomy().getMarket("nortia");
        if (nortia == null) {
            out.println("nortia MARKET WAS NULL");
        }
        nortia.addIndustry("sex_branch");

        MarketAPI jangala = Global.getSector().getEconomy().getMarket("jangala");
        if (jangala == null) {
            out.println("jangala MARKET WAS NULL");
        }
        jangala.addIndustry("sex_branch");
        jangala.addIndustry("sex_market_inserter");

        MarketAPI nachiketa = Global.getSector().getEconomy().getMarket("nachiketa");
        if (nachiketa == null) {
            out.println("nachiketa MARKET WAS NULL");
        }
        nachiketa.addIndustry("sex_market_inserter");

        MarketAPI eventide  = Global.getSector().getEconomy().getMarket("eventide");
        if (eventide == null) {
            out.println("eventide MARKET WAS NULL");
        }
        eventide.addIndustry("sex_market_inserter");

        MarketAPI sindria  = Global.getSector().getEconomy().getMarket("sindria");
        if (sindria == null) {
            out.println("sindria MARKET WAS NULL");
        }
        sindria.addIndustry("sex_market_inserter");
    }
}