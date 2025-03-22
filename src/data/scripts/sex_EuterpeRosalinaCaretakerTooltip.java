package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.HashSet;
import java.util.Set;

public class sex_EuterpeRosalinaCaretakerTooltip extends BreadcrumbIntel {
	public sex_EuterpeRosalinaCaretakerTooltip(SectorEntityToken foundAt, SectorEntityToken target) {
		super(foundAt, target);
	}
	@Override
	public String getName() {
		return "Guardian Angel";
	}

	@Override
	public String getText() {
		return "Guardian Angel";
	}

	@Override
	public String getIcon() {
		return Global.getSettings().getSpriteName("misc","sex_guard");
	}

	@Override
	public boolean hasSmallDescription() {
		return true;
	}

	@Override
	public Set<String> getIntelTags(SectorMapAPI map) {
		HashSet<String> set= new HashSet<>();
		set.add("Fleet log");
		return set;
	}

	@Override
	public FactionAPI getFactionForUIColors() {
		return Global.getSector().getFaction("sex_co");
	}

	@Override
	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		info.addPara("You agreed to help Euterpe's daughter Rosalina in her business affairs. " +
				"Rosalina can be found at the Seastar Outpost planet in the Anadolu system.",10);
		info.addButton("Delete", BUTTON_DELETE,300,20,20);
	}
}