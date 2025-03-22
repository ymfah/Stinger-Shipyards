package data.scripts.world.econ;

import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseHazardCondition;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

import static com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator.makeDiscoverable;

public class sex_MachineryCustomGen extends BaseHazardCondition {

	boolean orbitalsGenerated;

	@Override
	public void apply(String id) {
		Industry orbital = market.getIndustry(Industries.ORBITALWORKS);
		if (orbital != null && orbital.isFunctional()) {
			orbital.supply(market.getId() + "_0", Commodities.HAND_WEAPONS, 6, "Fabricators (Assisting Orbital Works)");
			orbital.supply(market.getId() + "_0", Commodities.SHIPS, 3, "Fabricators (Assisting Orbital Works)");
			orbital.supply(market.getId() + "_0", Commodities.SUPPLIES, 4, "Fabricators (Assisting Heavy Industry)");
			orbital.supply(market.getId() + "_0", Commodities.HEAVY_MACHINERY, 2, "Fabricators (Assisting Heavy Industry)");
		}
		Industry heavy = market.getIndustry(Industries.HEAVYINDUSTRY);
		if (heavy != null && heavy.isFunctional()) {
			heavy.supply(market.getId() + "_0", Commodities.HAND_WEAPONS, 3, "Fabricators (Assisting Heavy Industry)");
			heavy.supply(market.getId() + "_0", Commodities.SHIPS, 2, "Fabricators (Assisting Heavy Industry)");
			heavy.supply(market.getId() + "_0", Commodities.SUPPLIES, 2, "Fabricators (Assisting Heavy Industry)");
			heavy.supply(market.getId() + "_0", Commodities.HEAVY_MACHINERY, 1, "Fabricators (Assisting Heavy Industry)");
		}

//		if (!orbitalsGenerated) {
//			SectorEntityToken planet = market.getPrimaryEntity();
//			LocationAPI system = planet.getStarSystem();
//			float radius = planet.getRadius();
//			if (planet != null) {
//			SectorEntityToken fab1 = system.addCustomEntity(null, "Stellar Mirror Alpha", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//			SectorEntityToken fab2 = system.addCustomEntity(null, "Stellar Mirror Beta", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//			SectorEntityToken fab3 = system.addCustomEntity(null, "Stellar Mirror Gamma", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//			SectorEntityToken fab4 = system.addCustomEntity(null, "Stellar Mirror Delta", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//			SectorEntityToken fab5 = system.addCustomEntity(null, "Stellar Mirror Epsilon", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//			SectorEntityToken fab6 = system.addCustomEntity(null, "Stellar Mirror Zeta", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//			SectorEntityToken fab7 = system.addCustomEntity(null, "Stellar Mirror Lambda", Entities.STELLAR_MIRROR, Factions.NEUTRAL);
//
//			fab1.setCircularOrbitPointingDown(planet, 0, radius + 60, 12);
//			fab2.setCircularOrbitPointingDown(planet, 60, radius + 60, 12);
//			fab3.setCircularOrbitPointingDown(planet, 120, radius + 60, 12);
//			fab4.setCircularOrbitPointingDown(planet, 180, radius + 60, 12);
//			fab5.setCircularOrbitPointingDown(planet, 240, radius + 60, 12);
//			fab6.setCircularOrbitPointingDown(planet, 300, radius + 60, 12);
//			fab7.setCircularOrbitPointingDown(planet, 360, radius + 60, 12);
//
//			makeDiscoverable(fab1, 300f, 2000f);
//			makeDiscoverable(fab2, 300f, 2000f);
//			makeDiscoverable(fab3, 300f, 2000f);
//			makeDiscoverable(fab4, 300f, 2000f);
//			makeDiscoverable(fab5, 300f, 2000f);
//			makeDiscoverable(fab6, 300f, 2000f);
//			makeDiscoverable(fab7, 300f, 2000f);
//			orbitalsGenerated = true;
//			}
//		}
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
					"+6 units");
			tooltip.addPara("%s to production of ship hulls.",
					opad, h,
					"+3 units");
			tooltip.addPara("%s to production of supplies.",
					opad, h,
					"+4 units");
			tooltip.addPara("%s to production of heavy machinery.",
					opad, h,
					"+2 units");
		}

		if (heavy != null && heavy.isFunctional()) {
			tooltip.addPara("The facilities are %s and provide the following benefits:",
					opad, h,
					"currently operational");
			tooltip.addPara("%s to production of heavy armaments.",
					opad, h,
					"+3 units");
			tooltip.addPara("%s to production of ship hulls.",
					opad, h,
					"+2 units");
			tooltip.addPara("%s to production of supplies.",
					opad, h,
					"+2 units");
			tooltip.addPara("%s to production of heavy machinery.",
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
