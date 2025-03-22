package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.util.HashSet;
import java.util.Set;

public class sex_LangleyTooltip extends BreadcrumbIntel {
	public sex_LangleyTooltip(SectorEntityToken foundAt, SectorEntityToken target) {
		super(foundAt, target);
	}
	@Override
	public String getName() {
		return "Making Amends";
	}

	@Override
	public String getText() {
		return "Making Amends";
	}

	@Override
	public String getIcon() {
		return Global.getSettings().getSpriteName("intel","hegemony_inspection");
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
		info.addPara("You agreed to help Euterpe take care of a nosy inspector from the Hegemony." +
				"Asuka Langley's fleet has been lured to orbit the Orhangazi planet in the Marmara system.",10);
	}
}