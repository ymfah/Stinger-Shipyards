package data.scripts.world.econ;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class sex_Culture extends BaseHazardCondition {

	public static float DEFENSE_BONUS = 1f;

	public void apply(String id) {
		super.apply(id);
		float bonus = DEFENSE_BONUS;
		market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
				.modifyMult(getModId(), 1f + bonus, "Militant population");

		if (!market.getFactionId().equals("sex_co")) {
			market.removeCondition("sex_Culture");
		}
	}

	@Override
	protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
		super.createTooltipAfterDescription(tooltip, expanded);

		tooltip.addPara("Ground defense strength is doubled.", 10f);
	}
}
