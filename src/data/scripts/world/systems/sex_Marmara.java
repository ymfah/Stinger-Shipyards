package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.intel.SexWreckWranglerListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class sex_Marmara implements SectorGeneratorPlugin { //A SectorGeneratorPlugin is a class from the game, that identifies this as a script that will have a 'generate' method
    @Override
    public void generate(SectorAPI sector) { //the parameter sector is passed. This is the instance of the campaign map that this script will add a star system to
        //initialise system
        StarSystemAPI system = sector.createStarSystem("Marmara"); //create a new variable called system. this is assigned an instance of the new star system added to the Sector at the same time
        system.getLocation().set(5300, 6800); //sets location of system in hyperspace. map size is in the order of 100000x100000, and 0, 0 is the center of the map
        system.setBackgroundTextureFilename("graphics/backgrounds/sex_purple_bg.jpg"); //sets the background image for when in the system. this is a filepath to an image in the core game files

        //set up star
        PlanetAPI star = system.initStar( //stars and planets are technically the same category of object, so stars use PlanetAPI
                "sex_marmara", //set star id, this should be unique
                "star_purple", //set star type, the type IDs come from starsector-core/data/campaign/procgen/star_gen_data.csv
                3000, //set radius, 900 is a typical radius size
                5300, //sets the location of the star's one-way jump point in hyperspace, since it is the center of the star system, we want it to be in the center of the star system jump points in hyperspace
                6800,
                900 //radius of corona terrain around star
        );

        system.setEnteredByPlayer(true);

        //set up wreckwrangler outpost
		SectorEntityToken sex_wreckwangler = system.addCustomEntity("sex_wreckwangler","Wreckwrangler Outpost","station_mining00", "sex_co");
        sex_wreckwangler.setCircularOrbitPointingDown(star,0,7600,245);
		MarketAPI sex_wreckwanglermarket = Global.getFactory().createMarket("wreckwanglerMarket","Wreckwrangler Outpost",4);
        sex_wreckwangler.setCustomDescriptionId("sex_wreckwangler");
        sex_wreckwangler.setMarket(sex_wreckwanglermarket);
        sex_wreckwanglermarket.setPrimaryEntity(sex_wreckwangler);
        sex_wreckwanglermarket.addIndustry("sex_salvage");
        //use helper method from other script to easily configure the market. feel free to copy it into your own project
        sex_wreckwanglermarket = sex_AddMarketplace.addMarketplace( //A Market is separate to a Planet, and contains data about population, industries and conditions. This is a method from the other script in this mod, that will assign all marketplace conditions to the planet in one go, making it simple and easy
                "sex_co", //Factions.INDEPENDENT references the id String of the Independent faction, so it is the same as writing "independent", but neater. This determines the Faction associated with this market
                sex_wreckwangler, //the PlanetAPI variable that this market will be assigned to
                null, //some mods and vanilla will have additional floating space stations or other entities, that when accessed, will open this marketplace. We don't have any associated entities for this method to add, so we leave null
                "Wreckwrangler Outpost", //Display name of market
                5, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_5,
                        "sex_culture"
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.GENERIC_MILITARY, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.SPACEPORT, //same with spaceport
                        Industries.WAYSTATION,
                        Industries.ORBITALSTATION,
                        Industries.MILITARYBASE,
                        "sex_salvage"
                )),
                true, //if true, the planet will have visual junk orbiting and will play an ambient chatter audio track when the player is nearby
                false //used by the method to make a market hidden like a pirate base, not recommended for generating markets in a core world
        );
        sex_wreckwangler.setInteractionImage("illustrations", "sex_station");

		//add an asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                star, //orbit focus
                80, //number of asteroid entities
                7000, //orbit radius is 500 gap for outer randomly generated entity above
                580, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Junk Belt" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_wrecks", //specific texture id in category misc in settings.json
                1024f, //texture width, can be used for scaling shenanigans 256f
                0,
                Color.white, //colour tint
                600, //band width in game
                7000, //same as above
                200f,
                null,
                null
        );

        //add some wrecks to start
        for(int i =0;i<10;i++){
            SexWreckWranglerListener.generateWreck(star,system);
        }

        //add first planet - a local hegemony outpost
        PlanetAPI sex_edirne = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_edirne", //unique id string
                star, //orbit focus for planet
                "Ironforge Command", //display name of planet
                "lava", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                120f, //starting angle in orbit
                160f, //planet size
                9000, //radius gap from the star
                412 //number of in-game days for it to orbit once
        );

        //add second planet
        PlanetAPI sex_bursa = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_bursa", //unique id string
                star, //orbit focus for planet
                "Bursa", //display name of planet
                "gas_giant", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                80f, //starting angle in orbit
                340f, //planet size
                12000, //radius gap from the star
                520); //number of in-game days for it to orbit once
        sex_bursa.getMarket().addCondition(Conditions.VOLATILES_DIFFUSE);
        sex_bursa.getMarket().addCondition(Conditions.THIN_ATMOSPHERE);
        sex_bursa.getMarket().addCondition(Conditions.LOW_GRAVITY);
        sex_bursa.setCustomDescriptionId("sex_bursa"); //reference descriptions.csv

        system.addRingBand(sex_bursa, // the planet the ring orbits around
                "misc", // the location where game can find the texture data, DO NOT CHANGE
                "rings_ice0", // the name of the .png file to be loaded as found in Starsector\starsector-core\graphics\planets
                256, // the total width of the image file as on disk
                2, // the index of the ring to display in game
                Color.RED, // the colour that does something?
                256, // the size of the ring as displayed in game
                800, // the radius form the orbitFocus to the middle of the spawned ring.
                240); // the amount of days it takes to to complate one loop around the orbitFocus

        system.addAsteroidBelt(
                sex_bursa, //orbit focus
                8, //number of asteroid entities
                800, //orbit radius
                255, //width of band
                220, //minimum and maximum visual orbit speeds of asteroids
                300,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Bursa's Tribute" //display name
        );


        //add second planet's first moon
        PlanetAPI sex_orhangazi = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_orhangazi", //unique id string
                sex_bursa, //orbit focus for planet
                "Orhangazi", //display name of planet
                "barren2", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                20f, //starting angle in orbit
                110f, //planet size
                1400, //radius gap from the star
                45); //number of in-game days for it to orbit once
        sex_orhangazi.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
        sex_orhangazi.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        sex_orhangazi.getMarket().addCondition(Conditions.ORE_SPARSE);
        sex_orhangazi.getMarket().addCondition(Conditions.RARE_ORE_SPARSE);
        sex_orhangazi.setCustomDescriptionId("sex_orhangazi"); //reference descriptions.csv

        //add second planet's second moon
        PlanetAPI sex_karacabey = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_karacabey", //unique id string
                sex_bursa, //orbit focus for planet
                "Karacabey", //display name of planet
                "rocky_metallic", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                240f, //starting angle in orbit
                120f, //planet size
                1300, //radius gap from the star
                45); //number of in-game days for it to orbit once
        sex_karacabey.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
        sex_karacabey.getMarket().addCondition(Conditions.TECTONIC_ACTIVITY);
        sex_karacabey.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        sex_karacabey.getMarket().addCondition(Conditions.ORE_RICH);
        sex_karacabey.getMarket().addCondition(Conditions.RARE_ORE_MODERATE);
        sex_karacabey.setCustomDescriptionId("sex_karacabey"); //reference descriptions.csv

        //add third planet
        PlanetAPI sex_yalova = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_yalova", //unique id string
                star, //orbit focus for planet
                "Yalova", //display name of planet
                "lava", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                80f, //starting angle in orbit
                140f, //planet size
                5000, //radius gap from the star
                180); //number of in-game days for it to orbit once
        sex_yalova.getMarket().addCondition(Conditions.VERY_HOT);
        sex_yalova.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
        sex_yalova.getMarket().addCondition(Conditions.EXTREME_TECTONIC_ACTIVITY);
        sex_yalova.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        sex_yalova.getMarket().addCondition(Conditions.ORE_MODERATE);
        sex_yalova.getMarket().addCondition(Conditions.RARE_ORE_SPARSE);
        sex_yalova.setCustomDescriptionId("sex_yalova"); //reference descriptions.csv

        //use helper method from other script to easily configure the market. feel free to copy it into your own project
        MarketAPI sex_edirnemarket = sex_AddMarketplace.addMarketplace( //A Market is separate to a Planet, and contains data about population, industries and conditions. This is a method from the other script in this mod, that will assign all marketplace conditions to the planet in one go, making it simple and easy
                Factions.HEGEMONY, //Factions.INDEPENDENT references the id String of the Independent faction, so it is the same as writing "independent", but neater. This determines the Faction associated with this market
                sex_edirne, //the PlanetAPI variable that this market will be assigned to
                null, //some mods and vanilla will have additional floating space stations or other entities, that when accessed, will open this marketplace. We don't have any associated entities for this method to add, so we leave null
                "Ironforge Command", //Display name of market
                4, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.EXTREME_WEATHER,
                        Conditions.VERY_HOT,
                        Conditions.POPULATION_4
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.GENERIC_MILITARY, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.SPACEPORT, //same with spaceport
                        Industries.MILITARYBASE,
                        Industries.BATTLESTATION,
                        Industries.WAYSTATION,
                        Industries.GROUNDDEFENSES
                )),
                true, //if true, the planet will have visual junk orbiting and will play an ambient chatter audio track when the player is nearby
                false //used by the method to make a market hidden like a pirate base, not recommended for generating markets in a core world
        );
        ((SectorEntityToken) sex_edirne).setCustomDescriptionId("sex_ironforge");

        //add an asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                star, //orbit focus
                80, //number of asteroid entities
                5700, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Inner Gemlik" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                5700, //same as above
                200f,
                null,
                null
        );

        //add an asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                star, //orbit focus
                80, //number of asteroid entities
                14000, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Outer Gemlik" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                14000, //same as above
                200f,
                null,
                null
        );

        //add makeshift comm relay entity to system
        SectorEntityToken sex_marmaramakeshiftRelay = system.addCustomEntity(
                "marmara_makeshift_relay",
                "Marmara System Relay",
                Entities.COMM_RELAY_MAKESHIFT,
                "sex_co"
        );
        //assign an orbit
        sex_marmaramakeshiftRelay.setCircularOrbit(star, 270f, 9300f, 950f); //assign an orbit

        //add domain sensor array
        SectorEntityToken sex_marmarasensorArray = system.addCustomEntity(
                "marmara_domain_sensor",
                "Marmara Sensor Cluster",
                Entities.SENSOR_ARRAY,
                "sex_co"
        );
        //assign an orbit, point down ensures it rotates to point towards center while orbiting
        sex_marmarasensorArray.setCircularOrbitPointingDown(star, 90f, 12000f, 520f);

        //autogenerate jump points that will appear in hyperspace and in system
        system.autogenerateHyperspaceJumpPoints(true, true);

        //the following is hyperspace cleanup code that will remove hyperstorm clouds around this system's location in hyperspace
        //don't need to worry about this, it's more or less copied from vanilla

        //set up hyperspace editor plugin
        HyperspaceTerrainPlugin hyperspaceTerrainPlugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin(); //get instance of hyperspace terrain
        NebulaEditor nebulaEditor = new NebulaEditor(hyperspaceTerrainPlugin); //object used to make changes to hyperspace nebula

        //set up radiuses in hyperspace of system
        float minHyperspaceRadius = hyperspaceTerrainPlugin.getTileSize() * 2f; //minimum radius is two 'tiles'
        float maxHyperspaceRadius = system.getMaxRadiusInHyperspace();

        //hyperstorm-b-gone (around system in hyperspace)
        nebulaEditor.clearArc(system.getLocation().x, system.getLocation().y, 0, minHyperspaceRadius + maxHyperspaceRadius, 0f, 360f, 0.25f);

        Misc.addNebulaFromPNG("data/campaign/terrain/sex_marmara.png", 0.0F, 0.0F, system, "terrain", "nebula", 4, 4, StarAge.OLD);

        //PersonAPI sex_james = Global.getFactory().createPerson();
        //sex_james.setId("sex_james");
        //sex_james.setName(new FullName("James","Granger", FullName.Gender.MALE));
        //sex_james.setPortraitSprite(Global.getSettings().getSpriteName("characters","sex_james"));
        //sex_james.setRankId(Ranks.POST_SMUGGLER);
        //sex_james.setFaction("sex_co");
        //Global.getSector().getImportantPeople().addPerson(sex_james);
        //sex_wreckwangler.getMarket().getCommDirectory().addPerson(Global.getSector().getImportantPeople().getPerson("sex_james"));

        //PersonAPI sex_grey = Global.getFactory().createPerson();
        //sex_grey.setId("sex_grey");
        //sex_grey.setName(new FullName("Grey","Eisenwolf", FullName.Gender.MALE));
        //sex_grey.setPortraitSprite(Global.getSettings().getSpriteName("characters","sex_grey"));
        //sex_grey.setRankId(Ranks.POST_SUPPLY_OFFICER);
        //sex_grey.setFaction(Factions.HEGEMONY);
        //Global.getSector().getImportantPeople().addPerson(sex_grey);
        //sex_edirne.getMarket().getCommDirectory().addPerson(Global.getSector().getImportantPeople().getPerson("sex_grey"));

        /*
        PersonAPI sex_ashbird = Global.getFactory().createPerson();
        sex_ashbird.setId("sex_ashbird");
        sex_ashbird.setName(new FullName("Arthr","Ashbird", FullName.Gender.MALE));
        sex_ashbird.setPortraitSprite(Global.getSettings().getSpriteName("characters","sex_ashbird"));
        sex_ashbird.setRankId(Ranks.POST_ARMS_DEALER);
        sex_ashbird.setFaction("sex_co");
        Global.getSector().getImportantPeople().addPerson(sex_ashbird);
        sex_wreckwangler.getMarket().getCommDirectory().addPerson(Global.getSector().getImportantPeople().getPerson("sex_ashbird"));

        PersonAPI sex_tia = Global.getFactory().createPerson();
        sex_tia.setId("sex_tia");
        sex_tia.setName(new FullName("Tia","Tsai", FullName.Gender.FEMALE));
        sex_tia.setPortraitSprite(Global.getSettings().getSpriteName("characters","sex_mari"));
        sex_tia.setRankId(Ranks.POST_ADMINISTRATOR);
        sex_tia.setPostId(Ranks.POST_STATION_COMMANDER);
        sex_tia.setFaction("sex_co");
        sex_tia.getMemoryWithoutUpdate().set("$nex_preferredAdmin", true);
        sex_tia.getMemoryWithoutUpdate().set("$nex_preferredAdmin_factionId", "sex_co");
        Global.getSector().getImportantPeople().addPerson(sex_tia);
        sex_wreckwangler.getMarket().setAdmin(sex_tia);
        sex_wreckwangler.getMarket().addPerson(sex_tia);
        sex_wreckwangler.getMarket().getCommDirectory().addPerson(Global.getSector().getImportantPeople().getPerson("sex_tia"));
         */

        LocationAPI hyper = Global.getSector().getHyperspace();
        SectorEntityToken sex_label = hyper.addCustomEntity("sex_label", null, "sex_label", null);
        sex_label.setFixedLocation(3800, 5800);
    }
}
