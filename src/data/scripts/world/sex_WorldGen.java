package data.scripts.world;

import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.procgen.themes.ThemeGenContext;
import com.fs.starfarer.api.impl.campaign.procgen.themes.Themes;
import data.scripts.world.systems.sex_Marmara;
import data.scripts.world.systems.sex_Anadolu;

public class sex_WorldGen implements SectorGeneratorPlugin {

    @Override
    public void generate(SectorAPI sector) {
        new sex_Marmara().generate(sector);
        new sex_Anadolu().generate(sector);
        new sex_Relations().generate(sector);
    }
}
