package data.scripts.world.econ;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class sex_Machinery extends BaseHazardCondition {

	@Override
	public void apply(String id) {
		Industry orbital = market.getIndustry(Industries.ORBITALWORKS);
		if (orbital != null && orbital.isFunctional()) {
			orbital.supply(market.getId() + "_0", Commodities.HAND_WEAPONS, 10, "Fabricators (Assisting Orbital Works)");
			orbital.supply(market.getId() + "_0", Commodities.SHIPS, 2, "Fabricators (Assisting Orbital Works)");
		}
		Industry heavy = market.getIndustry(Industries.HEAVYINDUSTRY);
		if (heavy != null && heavy.isFunctional()) {
			heavy.supply(market.getId() + "_0", Commodities.HAND_WEAPONS, 5, "Fabricators (Assisting Heavy Industry)");
			heavy.supply(market.getId() + "_0", Commodities.SHIPS, 1, "Fabricators (Assisting Heavy Industry)");
		}
	}

	@Override
	public void unapply(String id) {
		Industry industry = market.getIndustry(Industries.ORBITALWORKS);
		if (industry != null && !industry.isFunctional()) {
			industry.getSupply(Commodities.HAND_WEAPONS).getQuantity().unmodifyFlat(market.getId() + "_0");
		}
	}
	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);

		if (!market.hasSpaceport()) {
			tooltip.addPara("Requires a spaceport to have any effect", Misc.getNegativeHighlightColor(), 10f);
			return;
		}
		Color h = Misc.getHighlightColor();
		float opad = 10f;

		Industry orbital = market.getIndustry(Industries.ORBITALWORKS);
		Industry heavy = market.getIndustry(Industries.HEAVYINDUSTRY);

		if (orbital != null && orbital.isFunctional()) {
			tooltip.addPara("The facilities are %s and provide the following benefits:",
					opad, h,
					"currently operational");
		tooltip.addPara("%s to production of heavy armaments.",
				opad, h,
				"+10 units");
			tooltip.addPara("%s to production of ship hulls.",
					opad, h,
					"+2 units");
		}

		if (heavy != null && heavy.isFunctional()) {
			tooltip.addPara("The facilities are %s and provide the following benefits:",
					opad, h,
					"currently operational");
			tooltip.addPara("%s to production of heavy armaments.",
					opad, h,
					"+5 units");
			tooltip.addPara("%s to production of ship hulls.",
					opad, h,
					"+1 units");
		}
		if (heavy == null && orbital == null) {
			tooltip.addPara("The facilities are %s and do not provide any benefits.",
					opad, h,
					"currently not operational");
		}
	}
}
