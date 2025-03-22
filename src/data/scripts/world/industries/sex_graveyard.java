package data.scripts.world.industries;

import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Pair;


public class sex_graveyard extends BaseIndustry {

	public void apply() {
		super.apply(true);

		int size = market.getSize();

		supply(Commodities.SHIPS, 3);

		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.CREW, 1);

		Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY);
		applyDeficitToProduction(1, deficit, Commodities.SHIPS);
		if (!isFunctional()) {
			supply.clear();
		}
	}


	@Override
	public void unapply() {
		super.unapply();
	}

	@Override
	public boolean isAvailableToBuild() {return false;
	}

	public float getPatherInterest() {
		return 1f + super.getPatherInterest();
	}

	public boolean showWhenUnavailable() {
		return false;
	}

	@Override
	protected boolean canImproveToIncreaseProduction() {
		return true;
	}
}
