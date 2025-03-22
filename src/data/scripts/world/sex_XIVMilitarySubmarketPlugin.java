package data.scripts.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;

public class sex_XIVMilitarySubmarketPlugin extends BaseSubmarketPlugin {
	public static float ECON_UNIT_MULT_EXTRA = 1.0F;
	public static float ECON_UNIT_MULT_PRODUCTION = 0.4F;
	public static float ECON_UNIT_MULT_IMPORTS = 0.1F;
	public static float ECON_UNIT_MULT_DEFICIT = -0.2F;
	public static Set<String> SPECIAL_COMMODITIES = new HashSet();

	static {
		SPECIAL_COMMODITIES.add("supplies");
		SPECIAL_COMMODITIES.add("fuel");
		SPECIAL_COMMODITIES.add("crew");
		SPECIAL_COMMODITIES.add("marines");
		SPECIAL_COMMODITIES.add("heavy_machinery");
	}

	public sex_XIVMilitarySubmarketPlugin() {
	}

	public void init(SubmarketAPI submarket) {
		super.init(submarket);
	}

	public void updateCargoPrePlayerInteraction() {
		float seconds = Global.getSector().getClock().convertToSeconds(this.sinceLastCargoUpdate);
		//this.addAndRemoveStockpiledResources(seconds, false, true, true);

		int additionalMarines = (int) Math.floor(9 + Math.random() * 26);
		this.getCargo().addCommodity(Commodities.MARINES, additionalMarines);

		int additionalWeapons = (int) Math.floor(9 + Math.random() * 26);
		this.getCargo().addCommodity(Commodities.HAND_WEAPONS, additionalWeapons);

		this.sinceLastCargoUpdate = 0.0F;
		if (this.okToUpdateShipsAndWeapons()) {
			this.sinceSWUpdate = 0.0F;
			boolean military = Misc.isMilitary(this.market);
			boolean hiddenBase = this.market.getMemoryWithoutUpdate().getBoolean(MemFlags.HIDDEN_BASE_MEM_FLAG);
			//This is the number of fps that the market sells
			float ships = 35F*market.getSize();
			if (military && hiddenBase && !this.market.hasSubmarket("generic_military")) {
				ships += 100.0F;
			}

			this.pruneWeapons(0.0F);
			int weapons = 90 + Math.max(0, this.market.getSize() - 1) + (Misc.isMilitary(this.market) ? 5 : 0);
			int fighters = 1 + Math.max(0, (this.market.getSize() - 3) / 2) + (Misc.isMilitary(this.market) ? 2 : 0);
			//this.addWeapons(weapons, weapons + 2, 0, this.market.getFactionId());
			//this.addFighters(fighters, fighters + 2, 0, this.market.getFactionId());

			//acceptable weapons/fighters
			ArrayList<String> stingerWeps= new ArrayList<>();
			ArrayList<String> stingerFighters= new ArrayList<>();
			ArrayList<String> stingerShips= new ArrayList<>();
			try {
				JSONObject stingerData =Global.getSettings().getMergedJSONForMod("data/config/sex_xiv_market.json","hakkari2");
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
		//stinger does not care from where the explosives flow, only that it does
		return true;
	}

	public int getStockpileLimit(CommodityOnMarketAPI com) {
		float limit = getBaseStockpileLimit(com);
		Random random = new Random((long)(this.market.getId().hashCode() + this.submarket.getSpecId().hashCode() + Global.getSector().getClock().getMonth() * 170000));
		limit *= 0.9F + 0.2F * random.nextFloat();
		float sm = this.market.getStabilityValue() / 10.0F;
		limit *= 0.25F + 0.75F * sm;
		if (limit < 0.0F) {
			limit = 0.0F;
		}

		return (int)limit;
	}

	public static float getBaseStockpileLimit(CommodityOnMarketAPI com) {
		int shippingGlobal = Global.getSettings().getShippingCapacity(com.getMarket(), false);
		int available = com.getAvailable();
		int production = com.getMaxSupply();
		production = Math.min(production, available);
		int demand = com.getMaxDemand();
		int export = Math.min(production, shippingGlobal);
		int extra = available - Math.max(export, demand);
		if (extra < 0) {
			extra = 0;
		}

		int deficit = Math.max(0, demand - available);
		float unit = com.getCommodity().getEconUnit();
		int imports = available - production;
		if (imports < 0) {
			imports = 0;
		}

		float limit = 0.0F;
		limit += (float)imports * unit * ECON_UNIT_MULT_IMPORTS;
		limit += (float)production * unit * ECON_UNIT_MULT_PRODUCTION;
		limit += (float)extra * unit * ECON_UNIT_MULT_EXTRA;
		limit -= (float)deficit * unit * ECON_UNIT_MULT_DEFICIT;
		if (limit < 0.0F) {
			limit = 0.0F;
		}

		return (float)((int)limit);
	}

	public static int getApproximateStockpileLimit(CommodityOnMarketAPI com) {
		float limit = getBaseStockpileLimit(com);
		return (int)limit;
	}

	public SubmarketPlugin.PlayerEconomyImpactMode getPlayerEconomyImpactMode() {
		return PlayerEconomyImpactMode.PLAYER_SELL_ONLY;
	}

	public boolean isOpenMarket() {
		return false;
	}

	public boolean isEnabled(CoreUIAPI ui) {
		// Check if the player has a valid transponder
		boolean hasValidTransponder = Global.getSector().getPlayerFleet().isTransponderOn();

		if (!hasValidTransponder) {
			return false; // Player doesn't have a valid transponder, cannot use the market
		}

		// Check if the player has the required commission with the specified faction
		String commissionFactionId = Misc.getCommissionFactionId();

		if (commissionFactionId != null && commissionFactionId.equals(Factions.HEGEMONY)) {
			// Player has the required commission and a valid transponder, check standing
			RepLevel level = submarket.getFaction().getRelationshipLevel(Global.getSector().getFaction(Factions.PLAYER));
			return level.isAtWorst(RepLevel.NEUTRAL);
		} else {
			return false; // The player does not meet the requirements
		}
	}

	public OnClickAction getOnClickAction(CoreUIAPI ui) {
		return OnClickAction.OPEN_SUBMARKET;
	}

	public String getTooltipAppendix(CoreUIAPI ui) {
		// Check if the player has the required commission with the specified faction
		boolean hasRequiredCommission = "hegemony".equals(Misc.getCommissionFactionId());

		if (!hasRequiredCommission) {
			return "Requires a commission with the Hegemony.";
		}

		if (!isEnabled(ui)) {
			return "Requires a commission with the Hegemony.";
		}

		if (ui.getTradeMode() == CampaignUIAPI.CoreUITradeMode.SNEAK) {
			return "Requires proper docking authorization.";
		}

		return null;
	}
	public void addAndRemoveStockpiledResources(float amount, boolean withShortageCountering, boolean withDecreaseToLimit, boolean withCargoUpdate) {
		Iterator var6 = this.market.getCommoditiesCopy().iterator();

		while(var6.hasNext()) {
			CommodityOnMarketAPI com = (CommodityOnMarketAPI)var6.next();
			if (!com.isNonEcon() && !com.getCommodity().isMeta()) {
				this.addAndRemoveStockpiledResources(com, amount, withShortageCountering, withDecreaseToLimit, withCargoUpdate);
			}
		}

	}
	//                CargoAPI cargo = this.getCargo();
	//                                        cargo.addCommodity(com.getId(), addAmount);
	//                                cargo.removeCommodity(com.getId(), addAmount);

	//STINGER NO SELL
	public boolean isIllegalOnSubmarket(String commodityId, SubmarketPlugin.TransferAction action) {
		return action == TransferAction.PLAYER_SELL;
	}

	public boolean isIllegalOnSubmarket(CargoStackAPI stack, SubmarketPlugin.TransferAction action) {
		return action == TransferAction.PLAYER_SELL;
	}


	public String getIllegalTransferText(CargoStackAPI stack, SubmarketPlugin.TransferAction action) {
		return "The Surplus Branch does not do buybacks or refunds.";
	}

	public boolean isIllegalOnSubmarket(FleetMemberAPI member, SubmarketPlugin.TransferAction action) {
		return action == TransferAction.PLAYER_SELL;
	}

	public String getIllegalTransferText(FleetMemberAPI member, SubmarketPlugin.TransferAction action) {
		return "The Surplus Branch does not do buybacks or refunds.";
	}
	public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
		// Check if the player has a valid transponder
		boolean hasValidTransponder = Global.getSector().getPlayerFleet().isTransponderOn();
		boolean hasRequiredCommission = "hegemony".equals(Misc.getCommissionFactionId());

		if (!hasValidTransponder) {
			Highlights h = new Highlights();
			h.setText("Requires proper docking authorization.");
			h.setColors(Misc.getNegativeHighlightColor()); // Set the text color to red
			return h;}

		if (!hasRequiredCommission) {
			Highlights p = new Highlights();
			p.setText("Requires a commission with the Hegemony.");
			p.setColors(Misc.getNegativeHighlightColor()); // Set the text color to red
			return p;
		}

		if (!isEnabled(ui)) {
			Highlights j = new Highlights();
			j.setText("Requires a commission with the Hegemony.");
			j.setColors(Misc.getNegativeHighlightColor()); // Set the text color to red
			return j;
		}

		return null;
	}
}
