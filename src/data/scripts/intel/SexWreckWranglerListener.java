package data.scripts.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SexWreckWranglerListener extends BaseCampaignEventListener {
    private static final int MAX_WRECKS=80;
    private static final double SPAWN_CHANCE=.2;//chance of spawning per tick. There are ten ticks per month
    private static final float LOWER_RANGE =6500;
    private static final float UPPER_RANGE =7500;
    private static final float ORBIT_SPEED_LOWER =200;
    private static final float ORBIT_SPEED_UPPER =240;
    private static final double BATTERED_CHANCE=.25;//vs WRECKED

    private static final double WRECKED_RECOVERY_CHANCE=.25;//between 0 and 1
    private static final double BATTERED_RECOVERY_CHANCE=.1;//between 0 and 1
    private static final int MIN_SINGLE=30;
    private static final int MIN_HAMMER=30;
    private static final int MIN_JACKHAMMER=20;
    private static final int MIN_RACK=10;

    private static final int MAX_SINGLE=50;
    private static final int MAX_HAMMER=40;
    private static final int MAX_JACKHAMMER=30;
    private static final int MAX_RACK=20;

    private static final String[] VARIANTS_LIST = {
            "sex_perigee_Balanced",
            "sex_perigee_Strike",
            "sex_furtive_Expanded",
            "sex_furtive_Standard",
            "sex_mora_original_Support",
            "sex_mora_original_Siege",
            "sex_brynhildr_Strike",
            "sex_brizo_Standard",
            "sex_whiskeypete_Mining",
            "sex_whiskeypete_Support",
            "sex_morrigan_Mining",
            "sex_morrigan_Support",
            "kite_Stock",
            "drover_Strike",
            "sex_drover_original_Stock",
            "sex_starliner_original_Stock",
            "apogee_Balanced",
            "colossus_Standard",
            "dominator_Assault",
            "eagle_Assault",
            "eradicator_Assault",
            "hermes_Standard",
            "hermes_FS",
            "hound_Standard",
            "enforcer_Escort",
            "mule_Standard",
            "mudskipper_Standard",
            "nebula_Standard",
            "vanguard_Outdated",
            "tarsus_Standard",
            "venture_Exploration",
            "venture_Balanced",
            "buffalo_Standard",
            "phaeton_Standard",
            "dram_Light",
            "shepherd_Frontier",
            "wayfarer_Standard",
            "cerberus_Standard",
            "cerberus_Shielded",
            "mercury_Standard",
            "mercury_Attack",
            "mercury_PD",
            "hammerhead_Balanced",
            "valkyrie_Elite"
            };
    private static Logger logger;
    public SexWreckWranglerListener(boolean permaRegister) {
        super(permaRegister);
        logger =Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void reportEconomyTick(int iterIndex) {
        logger.info("economy tick!");
        //interindex is the index of the tick for this month. There are ten ticks per month
        //this runs once a month
        if(iterIndex==0){
            if(Global.getSector().getEntityById("sex_wreckwangler")==null){
                logger.info("Entity is null, script cannot run!");
                return;
            }
        }
        int wrecks = 0;
        //if there are over 100 wrecks in system do not add, otherwise take % chance and add wreck in $customType = wreck
        SectorEntityToken star = Global.getSector().getEntityById("sex_Marmara");
        if(star==null){
            logger.info("Star is null, script cannot run!");
            return;
        }
        StarSystemAPI system = star.getStarSystem();
        List<SectorEntityToken> tokenList = system.getAllEntities();
        if (tokenList != null) {
            for (SectorEntityToken t : tokenList) {
                if (t.getMemoryWithoutUpdate().contains("$customType") && t.getMemoryWithoutUpdate().get("$customType").equals("wreck")) {
                    wrecks++;
                }
            }
            if (MAX_WRECKS > wrecks && Math.random() < SPAWN_CHANCE) {
                generateWreck(star, system);
            }
        }
    }
    public static void generateWreck(SectorEntityToken star,StarSystemAPI system){
        //spawn wreck
        //random variant, random distance, random recovery, orbit speed based on range
        int variant = (int) (Math.random()*VARIANTS_LIST.length);
        double orbitRandom =Math.random();
        float orbit = (float) ((orbitRandom*(UPPER_RANGE-LOWER_RANGE))+LOWER_RANGE);
        float orbitDays = (float) ((orbitRandom*(ORBIT_SPEED_UPPER-ORBIT_SPEED_LOWER))+ORBIT_SPEED_LOWER);
        if(Math.random()<BATTERED_CHANCE){
            boolean recoverable =Math.random()<BATTERED_RECOVERY_CHANCE;
            addDerelict(system,star,VARIANTS_LIST[variant], ShipRecoverySpecial.ShipCondition.BATTERED,orbit,recoverable,orbitDays);
        }else{
            boolean recoverable =Math.random()<WRECKED_RECOVERY_CHANCE;
            addDerelict(system,star,VARIANTS_LIST[variant], ShipRecoverySpecial.ShipCondition.WRECKED,orbit,recoverable,orbitDays);
        }
    }
    protected static SectorEntityToken addDerelict(StarSystemAPI system, SectorEntityToken focus, String variantId,
                                                   ShipRecoverySpecial.ShipCondition condition, float orbitRadius, boolean recoverable, float orbitDays) {
        DerelictShipEntityPlugin.DerelictShipData params = new DerelictShipEntityPlugin.DerelictShipData(new ShipRecoverySpecial.PerShipData(variantId, condition, 0f), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setDiscoverable(true);
        //float orbitDays = orbitRadius / (10f + (float) Math.random() * 5f);
        ship.setCircularOrbit(focus, (float) Math.random() * 360f, orbitRadius, orbitDays);
        if (recoverable) {
            SalvageSpecialAssigner.ShipRecoverySpecialCreator creator = new SalvageSpecialAssigner.ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }
        return ship;
    }
}
