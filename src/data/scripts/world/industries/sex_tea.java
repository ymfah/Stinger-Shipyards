package data.scripts.world.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class sex_tea extends BaseIndustry implements MarketImmigrationModifier {

	public void apply() {
		super.apply(true);

		// Do nothing here or handle initial setup if needed
	}

	@Override
	public void advance(float amount) {
		super.advance(amount);

		int size = market.getSize();

		demand(Commodities.FOOD, size - 2);

		float foodProduction = market.getCommodityData(Commodities.FOOD).getMaxSupply();
		float incomeMultiplier = foodProduction * 8f; // 8% for every unit of organics produced
		market.getIncomeMult().modifyPercent("sex_tea", incomeMultiplier, "Effect of local tea culture");

		if (!isFunctional()) {
			supply.clear();
		}
	}

	@Override
	public void unapply() {
		super.unapply();
	}

	public boolean isAvailableToBuild() {
		if (!Global.getSector().getPlayerFaction().knowsIndustry(getId())) {
			return false;
		}
		return market.getPlanetEntity() != null && market.getPlanetEntity().hasCondition(Conditions.HABITABLE);
	}

	public boolean showWhenUnavailable() {
		return Global.getSector().getPlayerFaction().knowsIndustry(getId());
	}

	@Override
	public String getUnavailableReason() {
		if (!super.isAvailableToBuild()) return super.getUnavailableReason();
		return "Requires a habitable planet.";
	}

	@Override
	public void createTooltip(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltip(mode, tooltip, expanded);
	}

	public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
		incoming.add(Factions.LUDDIC_CHURCH, 10f);
	}

	@Override
	protected boolean canImproveToIncreaseProduction() {
		return true;
	}
}
