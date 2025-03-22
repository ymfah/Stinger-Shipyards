package data.scripts.world.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;


public class sex_market_inserter extends BaseIndustry {

	public boolean isHidden() {
		return true;
	}

	public boolean isFunctional() {
		return true;
	}

	public void apply() {
		super.apply(true);

		if (!market.hasSubmarket("sex_xiv_market") && market.getFactionId().equals(Factions.HEGEMONY) && !market.isPlayerOwned()) {
			market.addSubmarket("sex_xiv_market");
			SubmarketAPI sub = market.getSubmarket("sex_xiv_market");
			sub.setFaction(Global.getSector().getFaction(Factions.HEGEMONY));
			Global.getSector().getEconomy().forceStockpileUpdate(market);
		} else {
		market.removeSubmarket("sex_xiv_market");
	}

		if (market.getFaction().getId().equals(Factions.DIKTAT)) {
			if (!market.hasSubmarket("sex_diktat_market") && market.getFactionId().equals(Factions.DIKTAT) && !market.isPlayerOwned()) {
				market.addSubmarket("sex_diktat_market");
				SubmarketAPI sub = market.getSubmarket("sex_diktat_market");
				sub.setFaction(Global.getSector().getFaction(Factions.DIKTAT));
				Global.getSector().getEconomy().forceStockpileUpdate(market);
			}
		} else {
			market.removeSubmarket("sex_diktat_market");
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
