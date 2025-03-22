package data.scripts.world.econ;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;


public class sex_graveyard_condition extends BaseMarketConditionPlugin {

	@Override
	public void apply(String id) {
		Industry orbitalWorks = market.getIndustry(Industries.ORBITALWORKS);
		if (orbitalWorks != null && orbitalWorks.isFunctional()) {
			orbitalWorks.supply(market.getId() + "_0", Commodities.SHIPS, 2, "Ship graveyard reclamation");
		}
		Industry heavyIndustry = market.getIndustry(Industries.HEAVYINDUSTRY);
		if (heavyIndustry != null && heavyIndustry.isFunctional()) {
			heavyIndustry.supply(market.getId() + "_0", Commodities.SHIPS, 2, "Ship graveyard reclamation");
		}
	}

	@Override
	public void unapply(String id) {
		Industry orbitalWorks = market.getIndustry(Industries.ORBITALWORKS);
		if (orbitalWorks != null && !orbitalWorks.isFunctional()) {
			orbitalWorks.getSupply(Commodities.SHIPS).getQuantity().unmodifyFlat(market.getId() + "_0");
		}

		Industry heavyIndustry = market.getIndustry(Industries.HEAVYINDUSTRY);
		if (heavyIndustry != null && !heavyIndustry.isFunctional()) {
			heavyIndustry.getSupply(Commodities.SHIPS).getQuantity().unmodifyFlat(market.getId() + "_0");
		}
	}


	
	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);
		if (!market.isPlanetConditionMarketOnly()) {
			tooltip.addPara("%s",
					10f, Misc.getHighlightColor(),
					"+2");
		}
	}
}
