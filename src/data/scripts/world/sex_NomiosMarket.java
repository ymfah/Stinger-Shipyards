package data.scripts.world;

import java.io.IOException;
import java.util.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI.CoreUITradeMode;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.impl.campaign.submarkets.OpenMarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class sex_NomiosMarket extends BaseSubmarketPlugin {

    public void init(SubmarketAPI submarket) {
        super.init(submarket);
    }


    public void updateCargoPrePlayerInteraction() {
        float seconds = Global.getSector().getClock().convertToSeconds(this.sinceLastCargoUpdate);
        //addAndRemoveStockpiledResources(seconds, false, true, true);

        int additionalHeavyMachinery = (int) Math.floor(10 + Math.random() * 26);
        this.getCargo().addCommodity(Commodities.HEAVY_MACHINERY, additionalHeavyMachinery);

        int additionalSupplies = (int) Math.floor(20 + Math.random() * 26);
        this.getCargo().addCommodity(Commodities.SUPPLIES, additionalSupplies);


        this.sinceLastCargoUpdate = 0.0F;
        if (this.okToUpdateShipsAndWeapons()) {
            this.sinceSWUpdate = 0.0F;
            boolean military = Misc.isMilitary(this.market);
            boolean hiddenBase = this.market.getMemoryWithoutUpdate().getBoolean(MemFlags.HIDDEN_BASE_MEM_FLAG);
            //This is the number of fps that the market sells
            float ships = 40F*market.getSize();
            if (military && hiddenBase && !this.market.hasSubmarket("generic_military")) {
                ships += 100.0F;
            }

            this.pruneWeapons(0.0F);
            int weapons = 40 + Math.max(0, this.market.getSize() - 1) + (Misc.isMilitary(this.market) ? 5 : 0);
            int fighters = 1 + Math.max(0, (this.market.getSize() - 3) / 2) + (Misc.isMilitary(this.market) ? 2 : 0);
            //this.addWeapons(weapons, weapons + 2, 0, this.market.getFactionId());
            //this.addFighters(fighters, fighters + 2, 0, this.market.getFactionId());

            //acceptable weapons/fighters
            ArrayList<String> stingerWeps= new ArrayList<>();
            ArrayList<String> stingerFighters= new ArrayList<>();
            ArrayList<String> stingerShips= new ArrayList<>();
            try {
                JSONObject stingerData =Global.getSettings().getMergedJSONForMod("data/config/sex_nomios_market.json","hakkari2");
                JSONObject stingerMarketData;
                if(stingerData.has(this.market.getId())) {
                    stingerMarketData = stingerData.getJSONObject(this.market.getId());
                }else{
                    stingerMarketData = stingerData.getJSONObject("default");
                }
                JSONArray jsWeapons =stingerMarketData.getJSONArray("weapons");
                for(int i=0;i<jsWeapons.length();i++){
                    stingerWeps.add((String) jsWeapons.get(i));
                }
                JSONArray jsFighters = stingerMarketData.getJSONArray("fighters");
                for(int i=0;i<jsFighters.length();i++){
                    stingerFighters.add((String) jsFighters.get(i));
                }
                JSONArray jsHulls = stingerMarketData.getJSONArray("hulls");
                for(int i=0;i<jsHulls.length();i++){
                    stingerShips.add((String) jsHulls.get(i));
                }

            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
            WeightedRandomPicker<WeaponSpecAPI> weaponPicker = new WeightedRandomPicker<>();
            WeightedRandomPicker<FighterWingSpecAPI> fighterPicker = new WeightedRandomPicker<>();
            WeightedRandomPicker<ShipHullSpecAPI> hullPicker = new WeightedRandomPicker<>();
            for(String s:stingerWeps){
                WeaponSpecAPI weaponSpecAPI=Global.getSettings().getWeaponSpec(s);
                if(weaponSpecAPI!=null){
                    weaponPicker.add(weaponSpecAPI);
                }
            }
            for(String s:stingerFighters){
                FighterWingSpecAPI wingSpecAPI=Global.getSettings().getFighterWingSpec(s);
                fighterPicker.add(wingSpecAPI);
            }
            for(String s:stingerShips){
                ShipHullSpecAPI shipHullSpecAPI=Global.getSettings().getHullSpec(s);
                if(shipHullSpecAPI!=null){
                    hullPicker.add(shipHullSpecAPI);
                }
            }
            for(int i=0;i<weapons;i++){
                this.pickAndAddWeapons(weaponPicker);
            }

            float randomReduction = new Random().nextFloat() * 0.5f;

            this.getCargo().getMothballedShips().clear();
            for(int i=0;i<ships;i++){
                ShipHullSpecAPI hull=hullPicker.pick();
                if(hull!=null){
                    FleetMemberAPI ship =this.addShip(hullPicker.pick().getHullId()+"_Hull", true, 0.9f - randomReduction);
                    i+=ship.getFleetPointCost();
                }else{
                    i++;
                }

            }
            for(int i=0;i<fighters;i++){
                int count = this.itemGenRandom.nextInt(5)+1;//number between 1 and 5
                FighterWingSpecAPI fighterWingSpecAPI =fighterPicker.pick();
                if(fighterWingSpecAPI!=null) {
                    this.cargo.addItems(CargoAPI.CargoItemType.FIGHTER_CHIP, fighterWingSpecAPI.getId(), (float) count);
                }
            }
        }

        this.getCargo().sort();
    }

    protected Object writeReplace() {
        if (this.okToUpdateShipsAndWeapons()) {
            this.pruneWeapons(0.0F);
            this.getCargo().getMothballedShips().clear();
        }

        return this;
    }

    public boolean shouldHaveCommodity(CommodityOnMarketAPI com) {
        return !market.isIllegal(com);
    }

    @Override
    public int getStockpileLimit(CommodityOnMarketAPI com) {
//		int demand = com.getMaxDemand();
//		int available = com.getAvailable();
//
//		float limit = BaseIndustry.getSizeMult(available) - BaseIndustry.getSizeMult(Math.max(0, demand - 2));
//		limit *= com.getCommodity().getEconUnit();

        //limit *= com.getMarket().getStockpileMult().getModifiedValue();

        float limit = OpenMarketPlugin.getBaseStockpileLimit(com);

        Random random = new Random(market.getId().hashCode() + submarket.getSpecId().hashCode() + Global.getSector().getClock().getMonth() * 170000);
        limit *= 0.9f + 0.2f * random.nextFloat();

        float sm = market.getStabilityValue() / 10f;
        limit *= (0.25f + 0.75f * sm);

        if (limit < 0) limit = 0;

        return (int) limit;
    }

    public static float ECON_UNIT_MULT_EXTRA = 1f;
    public static float ECON_UNIT_MULT_PRODUCTION = 0.4f;
    public static float ECON_UNIT_MULT_IMPORTS = 0.1f;
    public static float ECON_UNIT_MULT_DEFICIT = -0.2f;

    public static Set<String> SPECIAL_COMMODITIES = new HashSet<String>();
    static {
        SPECIAL_COMMODITIES.add(Commodities.SUPPLIES);
        SPECIAL_COMMODITIES.add(Commodities.FUEL);
        SPECIAL_COMMODITIES.add(Commodities.CREW);
        SPECIAL_COMMODITIES.add(Commodities.MARINES);
        SPECIAL_COMMODITIES.add(Commodities.HEAVY_MACHINERY);
    }

    public static float getBaseStockpileLimit(CommodityOnMarketAPI com) {
//		if (com.getCommodity().getId().equals(Commodities.LUXURY_GOODS)) {
//			System.out.println("wefwefwef");
//		}
        int shippingGlobal = Global.getSettings().getShippingCapacity(com.getMarket(), false);
        int available = com.getAvailable();
        int production = com.getMaxSupply();
        production = Math.min(production, available);

        int demand = com.getMaxDemand();
        int export = (int) Math.min(production, shippingGlobal);

        int extra = available - Math.max(export, demand);
        if (extra < 0) extra = 0;

        //int inDemand = Math.min(available, demand);
        //int normal = Math.max(0, available - inDemand - extra);
        int deficit = Math.max(0, demand - available);

        float unit = com.getCommodity().getEconUnit();

        int imports = available - production;
        if (imports < 0) imports = 0;

        float limit = 0f;
        limit += imports * unit * ECON_UNIT_MULT_IMPORTS;
        limit += production * unit * ECON_UNIT_MULT_PRODUCTION;
        limit += extra * unit * ECON_UNIT_MULT_EXTRA;
        limit -= deficit * unit * ECON_UNIT_MULT_DEFICIT;


        //limit += inDemand * unit * ECON_UNIT_MULT_IN_DEMAND;
        //limit += normal * unit * ECON_UNIT_MULT_NORMAL;

        if (limit < 0) limit = 0;
        return (int) limit;
    }


    public static int getApproximateStockpileLimit(CommodityOnMarketAPI com) {
//		int demand = com.getMaxDemand();
//		int available = com.getAvailable();
//
//		float limit = BaseIndustry.getSizeMult(available) - BaseIndustry.getSizeMult(Math.max(0, demand - 2));
//		limit *= com.getCommodity().getEconUnit();
//		//limit *= 0.5f;
//
//		if (limit < 0) limit = 0;
//		return (int) limit;

        float limit = OpenMarketPlugin.getBaseStockpileLimit(com);
        return (int) limit;
    }




    @Override
    public PlayerEconomyImpactMode getPlayerEconomyImpactMode() {
        return PlayerEconomyImpactMode.PLAYER_SELL_ONLY;
    }


    @Override
    public boolean isOpenMarket() {
        return true;
    }

    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        return true;
    }

    @Override
    public String getTooltipAppendix(CoreUIAPI ui) {
        if (ui.getTradeMode() == CoreUITradeMode.SNEAK) {
            return "Requires: proper docking authorization (transponder on)";
        }
        return super.getTooltipAppendix(ui);
    }


    public float getTariff() {
        return -.05f;
    }
    public String getTariffTextOverride() {
        return "Discount (5%)";
    }
    //String getTariffValueOverride(){}//TODO number display + very hard bad me don't want to do
    public void createTooltip(CoreUIAPI ui, TooltipMakerAPI tooltip, boolean expanded) {
        float opad = 10.0F;
        tooltip.addTitle(this.submarket.getNameOneLine());
        String desc = this.submarket.getSpec().getDesc();
        desc = Global.getSector().getRules().performTokenReplacement((String)null, desc, this.market.getPrimaryEntity(), (Map)null);
        String appendix = this.submarket.getPlugin().getTooltipAppendix(ui);
        if (appendix != null) {
            desc = desc + "\n\n" + appendix;
        }

        if (desc != null && !desc.isEmpty()) {
            LabelAPI body = tooltip.addPara(desc, opad);
            if (this.getTooltipAppendixHighlights(ui) != null) {
                Highlights h = this.submarket.getPlugin().getTooltipAppendixHighlights(ui);
                if (h != null) {
                    body.setHighlightColors(h.getColors());
                    body.setHighlight(h.getText());
                }
            }
        }

        this.createTooltipAfterDescription(tooltip, expanded);
    }


}
