package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;

import java.util.ArrayList;

public class sex_AddMarketplace {
    public static MarketAPI addMarketplace(
            String factionID,
            SectorEntityToken primaryEntity,
            ArrayList<SectorEntityToken> connectedEntities,
            String name,
            int size,
            ArrayList<String> marketConditions,
            ArrayList<String> submarkets,
            ArrayList<String> industries,
            Boolean WithJunkAndChatter,
            Boolean PirateMode) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", newMarket.getFaction().getTariffFraction());

        if (submarkets != null) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        for (String condition : marketConditions) {
            try {
                newMarket.addCondition(condition);
            } catch (RuntimeException e) {
                newMarket.addIndustry(condition);
            }
        }
        if (industries != null) {
            for (String industry : industries) {
                newMarket.addIndustry(industry);
            }

        }

        if (connectedEntities != null) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, WithJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (connectedEntities != null) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        if (PirateMode) {
            newMarket.setEconGroup(newMarket.getId());
            newMarket.setHidden(true);
            primaryEntity.setSensorProfile(1f);
            primaryEntity.setDiscoverable(true);
            primaryEntity.getDetectedRangeMod().modifyFlat("gen", 5000f);
            newMarket.getMemoryWithoutUpdate().set(DecivTracker.NO_DECIV_KEY, true);
        } else {
            for (MarketConditionAPI mc : newMarket.getConditions()) {
                mc.setSurveyed(true);
            }
            newMarket.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        }

        newMarket.reapplyIndustries();
        return newMarket;
    }
}
