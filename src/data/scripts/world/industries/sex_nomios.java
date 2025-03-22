package data.scripts.world.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;


public class sex_nomios extends BaseIndustry {

	public boolean isHidden() {
		return true;
	}

	public boolean isFunctional() {
		if (!super.isFunctional() ||
				!market.getFactionId().equals(Factions.INDEPENDENT) ||
				!Global.getSector().getMemoryWithoutUpdate().getBoolean("$sex_MaxiosRemoved")) {
			return false;
		}
		boolean hasSalvage = market.hasIndustry("sex_graveyard");
		return hasSalvage;
	}


	public void apply() {
		super.apply(true);


		if (!isFunctional()) {
			supply.clear();
			unapply();
		}

		if (isFunctional()) {
			if (!market.hasSubmarket("sex_nomios_market") && market.getFaction().getId().equals(Factions.INDEPENDENT) && !market.isPlayerOwned()) {
					market.addSubmarket("sex_nomios_market");
					SubmarketAPI sub = market.getSubmarket("sex_nomios_market");
					sub.setFaction(Global.getSector().getFaction(Factions.INDEPENDENT));
					Global.getSector().getEconomy().forceStockpileUpdate(market);
				}
			} else {
				market.removeSubmarket("sex_nomios_market");
			}
		}



	@Override
	public void unapply() {
		super.unapply();
	}

	@Override
	protected boolean canImproveToIncreaseProduction() {
		return false;
	}

	@Override
	public boolean isAvailableToBuild() {
		return false;
	}

	public boolean showWhenUnavailable() {
		return false;
	}

	@Override
	public boolean canImprove() {
		return false;
	}
}
