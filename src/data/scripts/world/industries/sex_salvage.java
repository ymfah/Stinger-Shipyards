package data.scripts.world.industries;

import java.awt.Color;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.ResourceDepositsCondition;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

public class sex_salvage extends BaseIndustry implements MarketImmigrationModifier{

	public void apply() {
		super.apply(true);
		
		int size = market.getSize();

		supply(Commodities.METALS, size + 2);
		supply(Commodities.RARE_METALS, size + 1);

		demand(Commodities.HEAVY_MACHINERY, size - 1);
		demand(Commodities.DRUGS, size - 2);
		}

	public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
		Pair<String, Integer> deficit = getMaxDeficit(Commodities.DRUGS);
		if (deficit.two > 0) {
			incoming.getWeight().modifyFlat(getModId(), -deficit.two, "Ship Breaking: drug shortage");
		}
	}

	@Override
	public boolean isAvailableToBuild() {
		return false;
	}

	public boolean showWhenUnavailable() {
		return false;
	}
	
	public float getPatherInterest() {
		return 1f + super.getPatherInterest();
	}
	
	@Override
	protected boolean canImproveToIncreaseProduction() {
		return true;
	}


}





