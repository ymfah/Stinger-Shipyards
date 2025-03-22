package data.scripts.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.System.out;

public class sex_Anadolu implements SectorGeneratorPlugin { //A SectorGeneratorPlugin is a class from the game, that identifies this as a script that will have a 'generate' method
    @Override
    public void generate(SectorAPI sector) { //the parameter sector is passed. This is the instance of the campaign map that this script will add a star system to
        //initialise system
        StarSystemAPI system = sector.createStarSystem("Anadolu"); //create a new variable called system. this is assigned an instance of the new star system added to the Sector at the same time
        system.getLocation().set(3000, 4800); //sets location of system in hyperspace. map size is in the order of 100000x100000, and 0, 0 is the center of the map
        system.setBackgroundTextureFilename("graphics/backgrounds/background1.jpg"); //sets the background image for when in the system. this is a filepath to an image in the core game files

        //set up star
        PlanetAPI star = system.initStar( //stars and planets are technically the same category of object, so stars use PlanetAPI
                "sex_anadolu", //set star id, this should be unique
                "star_orange", //set star type, the type IDs come from starsector-core/data/campaign/procgen/star_gen_data.csv
                1200, //set radius, 900 is a typical radius size
                3000, //sets the location of the star's one-way jump point in hyperspace, since it is the center of the star system, we want it to be in the center of the star system jump points in hyperspace
                4800,
                900 //radius of corona terrain around star
        );

        system.setEnteredByPlayer(true);

        //add first planet
        PlanetAPI sex_ankara = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_ankara", //unique id string
                star, //orbit focus for planet
                "Ankara", //display name of planet
                "barren-desert", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                80f, //starting angle in orbit
                300f, //planet size
                9500, //radius gap from the star
                420); //number of in-game days for it to orbit once
        sex_ankara.getMarket().addCondition(Conditions.HABITABLE);
        sex_ankara.getMarket().addCondition(Conditions.HOT);
        sex_ankara.getMarket().addCondition(Conditions.POOR_LIGHT);
        sex_ankara.getMarket().addCondition(Conditions.ORE_ABUNDANT);
        sex_ankara.getMarket().addCondition(Conditions.RARE_ORE_MODERATE);
        sex_ankara.getMarket().addCondition("sex_machinery");
        sex_ankara.getMarket().addCondition("sex_culture");
        sex_ankara.setCustomDescriptionId("sex_ankara"); //reference descriptions.csv

        //use helper method from other script to easily configure the market. feel free to copy it into your own project
        MarketAPI sex_ankaramarket = sex_AddMarketplace.addMarketplace( //A Market is separate to a Planet, and contains data about population, industries and conditions. This is a method from the other script in this mod, that will assign all marketplace conditions to the planet in one go, making it simple and easy
                "sex_co", //Factions.INDEPENDENT references the id String of the Independent faction, so it is the same as writing "independent", but neater. This determines the Faction associated with this market
                sex_ankara, //the PlanetAPI variable that this market will be assigned to
                null, //some mods and vanilla will have additional floating space stations or other entities, that when accessed, will open this marketplace. We don't have any associated entities for this method to add, so we leave null
                "Ankara", //Display name of market
                6, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_6,
                        Conditions.HABITABLE,
                        Conditions.HOT,
                        Conditions.ORE_RICH,
                        Conditions.RARE_ORE_MODERATE,
                        "sex_machinery",
                        "sex_culture"
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.MEGAPORT, //same with spaceport
                        Industries.REFINING,
                        Industries.ORBITALWORKS,
                        Industries.BATTLESTATION,
                        Industries.WAYSTATION,
                        Industries.HEAVYBATTERIES,
                        "sex_command"
                )),
                true, //if true, the planet will have visual junk orbiting and will play an ambient chatter audio track when the player is nearby
                false //used by the method to make a market hidden like a pirate base, not recommended for generating markets in a core world
        );
        sex_ankaramarket.addIndustry(Industries.MINING,
                Collections.singletonList(Items.MANTLE_BORE));
        ((SectorEntityToken) sex_ankara).setCustomDescriptionId("sex_ankara");
        sex_ankara.getSpec().setGlowTexture(Global.getSettings().getSpriteName("hab_glows", "volturn"));
        sex_ankara.getSpec().setGlowColor(new Color(255,255,255,255));
        sex_ankara.getSpec().setUseReverseLightForGlow(true);

        sex_ankara.applySpecChanges();
        sex_ankara.setInteractionImage("illustrations", "mine");

        //ankara dust
        system.addRingBand(sex_ankara,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_dust0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                1,
                Color.white, //colour tint
                256f, //band width in game
                396, //distance from planet
                200f,
                null,
                null
        );


        // Orbital fabricators
        SectorEntityToken sex_ankara_machinery_one = system.addCustomEntity("sex_ankara_machinery", null, "sex_Machinery", "neutral");
        sex_ankara_machinery_one.setCircularOrbitPointingDown( sex_ankara, 0, 360, 12 );
        sex_ankara_machinery_one.setCustomDescriptionId("sex_machinery");

        SectorEntityToken sex_ankara_machinery_two = system.addCustomEntity("sex_ankara_machinery", null, "sex_Machinery", "neutral");
        sex_ankara_machinery_two.setCircularOrbitPointingDown( sex_ankara, 60, 360, 12 );
        sex_ankara_machinery_two.setCustomDescriptionId("sex_machinery");

        SectorEntityToken sex_ankara_machinery_three = system.addCustomEntity("sex_ankara_machinery", null, "sex_Machinery", "neutral");
        sex_ankara_machinery_three.setCircularOrbitPointingDown( sex_ankara, 120, 360, 12 );
        sex_ankara_machinery_three.setCustomDescriptionId("sex_machinery");

        SectorEntityToken sex_ankara_machinery_four = system.addCustomEntity("sex_ankara_machinery", null, "sex_Machinery", "neutral");
        sex_ankara_machinery_four.setCircularOrbitPointingDown( sex_ankara, 180, 360, 12 );
        sex_ankara_machinery_four.setCustomDescriptionId("sex_machinery");

        SectorEntityToken sex_ankara_machinery_five = system.addCustomEntity("sex_ankara_machinery", null, "sex_Machinery", "neutral");
        sex_ankara_machinery_five.setCircularOrbitPointingDown( sex_ankara, 240, 360, 12 );
        sex_ankara_machinery_five.setCustomDescriptionId("sex_machinery");

        SectorEntityToken sex_ankara_machinery_six = system.addCustomEntity("sex_ankara_machinery", null, "sex_Machinery", "neutral");
        sex_ankara_machinery_six.setCircularOrbitPointingDown( sex_ankara, 300, 360, 12 );
        sex_ankara_machinery_six.setCustomDescriptionId("sex_machinery");



        //add first planet's first moon
        PlanetAPI sex_mescidiye = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_mescidiye", //unique id string
                sex_ankara, //orbit focus for planet
                "Mescidiye", //display name of planet
                "tundra", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                120f, //starting angle in orbit
                110f, //planet size
                900, //radius gap from the star
                24); //number of in-game days for it to orbit once
        sex_mescidiye.getMarket().addCondition(Conditions.HABITABLE);
        sex_mescidiye.getMarket().addCondition(Conditions.COLD);
        sex_mescidiye.getMarket().addCondition(Conditions.POLLUTION);
        sex_mescidiye.getMarket().addCondition(Conditions.POOR_LIGHT);
        sex_mescidiye.getMarket().addCondition(Conditions.RUINS_WIDESPREAD);
        sex_mescidiye.getMarket().addCondition(Conditions.DECIVILIZED);
        sex_mescidiye.getMarket().addCondition(Conditions.ORE_SPARSE);
        sex_mescidiye.getMarket().addCondition(Conditions.RARE_ORE_SPARSE);
        sex_mescidiye.getMarket().addCondition(Conditions.FARMLAND_ADEQUATE);
        sex_mescidiye.getMarket().addCondition(Conditions.ORGANICS_PLENTIFUL);
        sex_mescidiye.setCustomDescriptionId("sex_mescidiye"); //reference descriptions.csv

        //add first planet's second moon
        PlanetAPI sex_akdere = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_akdere", //unique id string
                sex_ankara, //orbit focus for planet
                "Seastar Outpost", //display name of planet
                "water", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                240f, //starting angle in orbit
                120f, //planet size
                900, //radius gap from the star
                24); //number of in-game days for it to orbit once
        sex_akdere.getMarket().addCondition(Conditions.HABITABLE);
        sex_akdere.getMarket().addCondition(Conditions.MILD_CLIMATE);
        sex_akdere.getMarket().addCondition(Conditions.WATER_SURFACE);
        sex_akdere.getMarket().addCondition(Conditions.ORGANICS_ABUNDANT);
        sex_akdere.setCustomDescriptionId("sex_akdere"); //reference descriptions.csv

        //use helper method from other script to easily configure the market. feel free to copy it into your own project
        MarketAPI sex_akderemarket = sex_AddMarketplace.addMarketplace( //A Market is separate to a Planet, and contains data about population, industries and conditions. This is a method from the other script in this mod, that will assign all marketplace conditions to the planet in one go, making it simple and easy
                "sex_co", //Factions.INDEPENDENT references the id String of the Independent faction, so it is the same as writing "independent", but neater. This determines the Faction associated with this market
                sex_akdere, //the PlanetAPI variable that this market will be assigned to
                null, //some mods and vanilla will have additional floating space stations or other entities, that when accessed, will open this marketplace. We don't have any associated entities for this method to add, so we leave null
                "Akdere Outpost", //Display name of market
                4, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_4,
                        Conditions.HABITABLE,
                        Conditions.MILD_CLIMATE,
                        Conditions.WATER_SURFACE,
                        Conditions.ORGANICS_ABUNDANT
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.SPACEPORT, //same with spaceport
                        Industries.AQUACULTURE,
                        Industries.PATROLHQ,
                        Industries.WAYSTATION,
                        Industries.HEAVYBATTERIES,
                        "sex_tea"
                )),
                true, //if true, the planet will have visual junk orbiting and will play an ambient chatter audio track when the player is nearby
                false //used by the method to make a market hidden like a pirate base, not recommended for generating markets in a core world
        );
        ((SectorEntityToken) sex_akdere).setCustomDescriptionId("sex_akdere");

        Industry industry = sex_akderemarket.getIndustry(Industries.AQUACULTURE); industry.setImproved(true);
        sex_akdere.setInteractionImage("illustrations", "sex_seastar");

        //add first planet's third moon
        PlanetAPI sex_plevne = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_plevne", //unique id string
                sex_ankara, //orbit focus for planet
                "Plevne", //display name of planet
                "cryovolcanic", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                360f, //starting angle in orbit
                100f, //planet size
                940, //radius gap from the star
                24); //number of in-game days for it to orbit once
        sex_plevne.getMarket().addCondition(Conditions.VERY_COLD);
        sex_plevne.getMarket().addCondition(Conditions.EXTREME_WEATHER);
        sex_plevne.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
        sex_plevne.getMarket().addCondition(Conditions.VOLATILES_DIFFUSE);
        sex_plevne.setCustomDescriptionId("sex_plevne"); //reference descriptions.csv

        SectorEntityToken sex_magreb = system.addCustomEntity("sex_magreb","Magreb Raider Base","station_sporeship_derelict", Factions.PIRATES);
        sex_magreb.setCircularOrbitPointingDown(star,0,17500,345);
        MarketAPI sex_magrebMarket = Global.getFactory().createMarket("sex_magrebMarket","Magreb Raider Base",4);
        sex_magreb.setCustomDescriptionId("sex_magreb");
        sex_magreb.setMarket(sex_magrebMarket);
        sex_magrebMarket.setPrimaryEntity(sex_magreb);
        //use helper method from other script to easily configure the market. feel free to copy it into your own project
        sex_magrebMarket = sex_AddMarketplace.addMarketplace( //A Market is separate to a Planet, and contains data about population, industries and conditions. This is a method from the other script in this mod, that will assign all marketplace conditions to the planet in one go, making it simple and easy
                Factions.PIRATES, //Factions.PIRATES references the id String of the Pirates faction, so it is the same as writing "pirates", but neater. This determines the Faction associated with this market
                sex_magreb, //the PlanetAPI variable that this market will be assigned to
                null, //some mods and vanilla will have additional floating space stations or other entities, that when accessed, will open this marketplace. We don't have any associated entities for this method to add, so we leave null
                "Magreb Raider Base", //Display name of market
                4, //population size
                new ArrayList<>(Arrays.asList( //List of conditions for this method to iterate through and add to the market
                        Conditions.POPULATION_4
                )),
                new ArrayList<>(Arrays.asList( //list of submarkets for this method to iterate through and add to the market. if a military base industry was added to this market, it would be consistent to add a military submarket too
                        Submarkets.SUBMARKET_OPEN, //add a default open market
                        Submarkets.SUBMARKET_STORAGE, //add a player storage market
                        Submarkets.SUBMARKET_BLACK //add a black market
                )),
                new ArrayList<>(Arrays.asList( //list of industries for this method to iterate through and add to the market
                        Industries.POPULATION, //population industry is required for weirdness to not happen
                        Industries.SPACEPORT, //same with spaceport
                        Industries.WAYSTATION,
                        Industries.ORBITALSTATION,
                        Industries.MILITARYBASE
                )),
                true, //if true, the planet will have visual junk orbiting and will play an ambient chatter audio track when the player is nearby
                false //used by the method to make a market hidden like a pirate base, not recommended for generating markets in a core world
        );

        //add an asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                star, //orbit focus
                80, //number of asteroid entities
                7500, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Smuggler's Mirage" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                7500, //same as above
                200f,
                null,
                null
        );

        system.addAsteroidBelt(star, 90, 11550, 500, 150, 300, Terrain.ASTEROID_BELT,  "Baklava");
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.white, 256f, 11500, 305f, null, null);
        system.addRingBand(star, "misc", "rings_asteroids0", 256f, 3, Color.white, 256f, 11620, 295f, null, null);

        //add a second asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        //       system.addAsteroidBelt(
        //               star, //orbit focus
        //              80, //number of asteroid entities
        //              11500, //orbit radius is 500 gap for outer randomly generated entity above
        //            255, //width of band
        //            190, //minimum and maximum visual orbit speeds of asteroids
        //            220,
        //             Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
        //            "Baklava" //display name
        //   );

        //  //add a ring texture. it will go under the asteroid entities generated above
        //  system.addRingBand(star,
        //           "misc", //used to access band texture, this is the name of a category in settings.json
        //           "rings_asteroids0", //specific texture id in category misc in settings.json
        //           256f, //texture width, can be used for scaling shenanigans
        //          2,
        //          Color.white, //colour tint
        //         256f, //band width in game
        //         11500, //same as above
        //         200f,
        //          null,
        //         null
        //  );

        //add second planet
        PlanetAPI sex_adana = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_adana", //unique id string
                star, //orbit focus for planet
                "Adana", //display name of planet
                "lava_minor", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                10f, //starting angle in orbit
                110f, //planet size
                3000, //radius gap from the star
                520); //number of in-game days for it to orbit once
        sex_adana.getMarket().addCondition(Conditions.RUINS_SCATTERED);
        sex_adana.getMarket().addCondition(Conditions.VERY_HOT);
        sex_adana.getMarket().addCondition(Conditions.EXTREME_TECTONIC_ACTIVITY);
        sex_adana.getMarket().addCondition(Conditions.THIN_ATMOSPHERE);
        sex_adana.getMarket().addCondition(Conditions.ORE_MODERATE);
        sex_adana.getMarket().addCondition(Conditions.RARE_ORE_SPARSE);
        sex_adana.setCustomDescriptionId("sex_adana"); //reference descriptions.csv

        //add third planet
        PlanetAPI sex_kayseri = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_kayseri", //unique id string
                star, //orbit focus for planet
                "Kayseri", //display name of planet
                "gas_giant", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                290f, //starting angle in orbit
                400f, //planet size
                5200, //radius gap from the star
                620); //number of in-game days for it to orbit once
        sex_kayseri.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
        sex_kayseri.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        sex_kayseri.getMarket().addCondition(Conditions.POOR_LIGHT);
        sex_kayseri.getMarket().addCondition(Conditions.VOLATILES_DIFFUSE);
        sex_kayseri.setCustomDescriptionId("sex_kayseri"); //reference descriptions.csv

        system.addRingBand(sex_kayseri, // the planet the ring orbits around
                "misc", // the location where game can find the texture data, DO NOT CHANGE
                "rings_ice0", // the name of the .png file to be loaded as found in Starsector\starsector-core\graphics\planets
                256, // the total width of the image file as on disk
                1, // the index of the ring to display in game
                Color.RED, // the colour that does something?
                256, // the size of the ring as displayed in game
                950, // the radius form the orbitFocus to the middle of the spawned ring.
                240); // the amount of days it takes to to complate one loop around the orbitFocus

        system.addAsteroidBelt(
                sex_kayseri, //orbit focus
                8, //number of asteroid entities
                950, //orbit radius
                255, //width of band
                220, //minimum and maximum visual orbit speeds of asteroids
                300,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Kayseri's Stone" //display name
        );

        //add third planet's first moon
        PlanetAPI sex_develi = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_develi", //unique id string
                sex_kayseri, //orbit focus for planet
                "Develi", //display name of planet
                "barren", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                260f, //starting angle in orbit
                90f, //planet size
                1700, //radius gap from the star
                24); //number of in-game days for it to orbit once
        sex_develi.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
        sex_develi.getMarket().addCondition(Conditions.LOW_GRAVITY);
        sex_develi.getMarket().addCondition(Conditions.DECIVILIZED);
        sex_develi.getMarket().addCondition(Conditions.RUINS_SCATTERED);
        sex_develi.setCustomDescriptionId("sex_develi"); //reference descriptions.csv

        //add third planet's second moon
        PlanetAPI sex_talas = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_talas", //unique id string
                sex_kayseri, //orbit focus for planet
                "Talas", //display name of planet
                "barren", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                10f, //starting angle in orbit
                60f, //planet size
                1200, //radius gap from the star
                12); //number of in-game days for it to orbit once
        sex_talas.getMarket().addCondition(Conditions.NO_ATMOSPHERE);
        sex_talas.getMarket().addCondition(Conditions.LOW_GRAVITY);
        sex_talas.setCustomDescriptionId("sex_talas"); //reference descriptions.csv

        //add fourth planet
        PlanetAPI sex_sivas = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_sivas", //unique id string
                star, //orbit focus for planet
                "Sivas", //display name of planet
                "ice_giant", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                300f, //starting angle in orbit
                250f, //planet size
                15000, //radius gap from the star
                730); //number of in-game days for it to orbit once
        sex_sivas.getMarket().addCondition(Conditions.DENSE_ATMOSPHERE);
        sex_sivas.getMarket().addCondition(Conditions.HIGH_GRAVITY);
        sex_sivas.getMarket().addCondition(Conditions.VERY_COLD);
        sex_sivas.getMarket().addCondition(Conditions.POOR_LIGHT);
        sex_sivas.getMarket().addCondition(Conditions.VOLATILES_ABUNDANT);
        sex_sivas.setCustomDescriptionId("sex_sivas"); //reference descriptions.csv

        system.addRingBand(sex_sivas, "misc", "rings_ice0", 256f, 2, Color.white, 256f, 700, 45, Terrain.RING, null);


        //add fifth planet
        PlanetAPI sex_konya = system.addPlanet( //assigns instance of newly created planet to variable planetOne
                "sex_konya", //unique id string
                star, //orbit focus for planet
                "Konya", //display name of planet
                "tundra", //planet type id, comes from starsector-core/data/campaign/procgen/planet_gen_data.csv
                210f, //starting angle in orbit
                210f, //planet size
                13000, //radius gap from the star
                670); //number of in-game days for it to orbit once
        sex_konya.getMarket().addCondition(Conditions.COLD);
        sex_konya.getMarket().addCondition(Conditions.POOR_LIGHT);
        sex_konya.getMarket().addCondition(Conditions.FARMLAND_ADEQUATE);
        sex_konya.getMarket().addCondition(Conditions.ORGANICS_COMMON);
        sex_konya.getMarket().addCondition(Conditions.POLLUTION);
        sex_konya.getMarket().addCondition(Conditions.DECIVILIZED);
        sex_konya.setCustomDescriptionId("sex_konya"); //reference descriptions.csv

        //system.addRingBand(sex_sivas, // the planet the ring orbits around
        //        "misc", // the location where game can find the texture data, DO NOT CHANGE
        //        "rings_special0", // the name of the .png file to be loaded as found in Starsector\starsector-core\graphics\planets
        //        256, // the total width of the image file as on disk
        //        0, // the index of the ring to display in game
        //        Color.RED, // the colour that does something?
        //        256, // the size of the ring as displayed in game
        //        550, // the radius form the orbitFocus to the middle of the spawned ring.
        //        240); // the amount of days it takes to to complate one loop around the orbitFocus

        ////add an asteroid belt around Sivas. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        // system.addAsteroidBelt(
        //        sex_sivas, //orbit focus
        //        40, //number of asteroid entities
        //        550, //orbit radius is 500 gap for outer randomly generated entity above
        //        255, //width of band
        //        220, //minimum and maximum visual orbit speeds of asteroids
        //        240,
        //        Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
        //        "Ring System" //display name
        //);


        //add a third asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                star, //orbit focus
                80, //number of asteroid entities
                17000, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Inner Tulumba" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_dust0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                17000, //same as above
                200f,
                null,
                null
        );

        //add a fourth asteroid belt. asteroids are separate entities inside these, it will randomly distribute a defined number of them around the ring
        system.addAsteroidBelt(
                star, //orbit focus
                80, //number of asteroid entities
                18000, //orbit radius is 500 gap for outer randomly generated entity above
                255, //width of band
                190, //minimum and maximum visual orbit speeds of asteroids
                220,
                Terrain.ASTEROID_BELT, //ID of the terrain type that appears in the section above the abilities bar
                "Outer Tulumba" //display name
        );

        //add a ring texture. it will go under the asteroid entities generated above
        system.addRingBand(star,
                "misc", //used to access band texture, this is the name of a category in settings.json
                "rings_asteroids0", //specific texture id in category misc in settings.json
                256f, //texture width, can be used for scaling shenanigans
                2,
                Color.white, //colour tint
                256f, //band width in game
                18000, //same as above
                200f,
                null,
                null
        );

        //add comm relay entity to system
        SectorEntityToken sex_anadolumakeshiftRelay = system.addCustomEntity(
                "ankara_relay",
                "Ankara System Relay",
                Entities.COMM_RELAY,
                "sex_co"
        );
        //assign an orbit
        sex_anadolumakeshiftRelay.setCircularOrbit(star, 270f, 4000f, 650f); //assign an orbit

        //add gate entity to system
        SectorEntityToken sex_ankaragate = system.addCustomEntity(
                "ankara_gate",
                "Anadolu Gate",
                Entities.INACTIVE_GATE,
                Factions.NEUTRAL
        );
        //assign an orbit
        sex_ankaragate.setCircularOrbit(star, 130f, 7800, 650f); //assign an orbit

        JumpPointAPI sex_anadoluinner = Global.getFactory().createJumpPoint("sex_anadoluinner", "Ankara Jump point");
        sex_anadoluinner.setCircularOrbit(star, 110f, 12100f, 240f);
        system.addEntity(sex_anadoluinner);

        //add domain sensor array
        SectorEntityToken sex_anadaolusensorArray = system.addCustomEntity(
                "ankara_navigation",
                "Ankara Navigation Cluster",
                Entities.NAV_BUOY_MAKESHIFT,
                "sex_co"
        );
        //assign an orbit, point down ensures it rotates to point towards center while orbiting
        sex_anadaolusensorArray.setCircularOrbitPointingDown(star, 90f, 7000f, 520f);

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

        //add nebula from png file
        Misc.addNebulaFromPNG("data/campaign/terrain/sex_anadolu.png", 0.0F, 0.0F, system, "terrain", "nebula", 4, 4, StarAge.OLD);

        //ImportantPeopleAPI ip = Global.getSector().getImportantPeople();

        //PersonAPI sex_lilly = Global.getFactory().createPerson();
        //sex_lilly.setId("sex_lilly");
        //sex_lilly.setFaction("sex_co");
        //sex_lilly.setGender(FullName.Gender.FEMALE);
        //sex_lilly.setPostId(Ranks.POST_ACADEMICIAN);
        //sex_lilly.setRankId(Ranks.POST_GUARD_LEADER);
        //sex_lilly.setImportance(PersonImportance.HIGH);
        //sex_lilly.getName().setFirst("Lilly");
        //sex_lilly.getName().setLast("Satou");
        //sex_lilly.setPortraitSprite("graphics/portraits/characters/sex_lilly.png");
        //sex_lilly.getStats().setLevel(6);
        //sex_lilly.setVoice(Voices.SCIENTIST);
        //ip.addPerson(sex_lilly);
        //sex_akderemarket.getCommDirectory().addPerson(sex_lilly, 1);
        //sex_akderemarket.addPerson(sex_lilly);
        //out.println("Executing setup for Lilly");
    }
}
