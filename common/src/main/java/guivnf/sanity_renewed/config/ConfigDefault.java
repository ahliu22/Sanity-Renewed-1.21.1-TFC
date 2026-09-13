package guivnf.sanity_renewed.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;

import java.util.ArrayList;
import java.util.List;

public class ConfigDefault
{
    public final DoubleValue m_posMul;
    public final DoubleValue m_negMul;

    public final DoubleValue m_passive;
    public final DoubleValue m_raining;
    public final BooleanValue m_affectInWater;
    public final DoubleValue m_hotSpring;
    public final BooleanValue m_affectThirsty;
    public final IntValue m_thirstyThreshold;
    public final DoubleValue m_thirsty;
    public final BooleanValue m_affectHot;
    public final DoubleValue m_hot;
    public final DoubleValue m_hotTemperature;
    public final BooleanValue m_affectCold;
    public final DoubleValue m_cold;
    public final DoubleValue m_coldTemperature;
    public final IntValue m_hungerThreshold;
    public final DoubleValue m_hungry;
    public final DoubleValue m_pet;
    public final DoubleValue m_petFamiliarity;
    public final DoubleValue m_monster;
    public final DoubleValue m_darkness;
    public final IntValue m_darknessThreshold;
    public final DoubleValue m_lightness;
    public final IntValue m_lightnessThreshold;
    public final DoubleValue m_jukeboxPleasant;
    public final DoubleValue m_jukeboxUnsettling;
    public final ConfigValue<List<? extends String>> m_passiveBlocks;

    public final DoubleValue m_sleeping;
    public final DoubleValue m_hurtRatio;
    public final DoubleValue m_advancement;
    public final DoubleValue m_animalBreeding;
    public final DoubleValue m_animalBreedingCd;
    public final DoubleValue m_animalBreedingRadius;
    public final DoubleValue m_eating;
    public final DoubleValue m_eatingCd;
    public final DoubleValue m_innerEntityKill;
    public final ConfigValue<List<? extends String>> m_items;
    public final ConfigValue<List<? extends String>> m_itemCats;

    public final DoubleValue m_sanePlayerCompany;
    public final DoubleValue m_insanePlayerCompany;

    public final BooleanValue m_saneSeeInnerEntities;
    public final DoubleValue m_innerEntityVisibleSanity;
    public final DoubleValue m_innerEntitySpawnSanity;
    public final DoubleValue m_innerEntityTargetSanity;

    public final BooleanValue m_renderIndicator;
    public final BooleanValue m_twitchIndicator;
    public final DoubleValue m_twitchIndicatorSanity;
    public final DoubleValue m_indicatorScale;
    public final EnumValue<SanityIndicatorLocation> m_indicatorLocation;

    public final BooleanValue m_renderHint;
    public final BooleanValue m_twitchHint;
    public final DoubleValue m_hintsSanity;

    public final BooleanValue m_renderBloodTendrilsOverlay;
    public final BooleanValue m_flashBtOnShortBurst;
    public final BooleanValue m_renderBtPassive;
    public final DoubleValue m_bloodTendrilsSanity;

    public final BooleanValue m_renderPost;
    public final DoubleValue m_postStartSanity;
    public final DoubleValue m_postMaxSanity;
    public final BooleanValue m_playSounds;
    public final DoubleValue m_insanityVolume;
    public final DoubleValue m_ambienceStartSanity;
    public final DoubleValue m_ambienceMaxSanity;
    public final DoubleValue m_heartbeatStartSanity;
    public final DoubleValue m_heartbeatMaxSanity;
    public final DoubleValue m_hallucinationsStartSanity;

    public ConfigDefault(ModConfigSpec.Builder builder)
    {
        builder.comment(
                "Sanity configuration",
                "NOTE: all sanity values are measured in percentages (i.e. 40.0 is equal to 40% of sanity bar)",
                "NOTE: all threshold values are sanity percentages: an effect applies when the player's sanity is at or below the configured value (100 = full sanity bar)",
                "NOTE: each subsequent usage of an active source or item has its effectiveness multiplied by (timeSinceLastUsage / cooldown) (capped at 1.0)")
                .push("sanity");

        m_posMul = builder
                .comment("For balancing purposes: the effectiveness of all positive sanity sources will be multiplied by this number")
                .defineInRange("positive_multiplier", 1.0, Float.MIN_VALUE, Float.MAX_VALUE);
        m_negMul = builder
                .comment("For balancing purposes: the effectiveness of all negative sanity sources will be multiplied by this number")
                .defineInRange("negative_multiplier", 1.0, Float.MIN_VALUE, Float.MAX_VALUE);

        builder.comment("Configuration for passive sanity sources").push("passive");

        m_passive = builder
                .comment("This value will be added to sanity each second regardless of any other factors")
                .defineInRange("passive", .0, -100.0, 100.0);
        m_raining = builder
                .comment("Sanity gain per second during rainy weather or while standing in any non-hot water (vanilla water, TFC river water, TFC salt water)")
                .defineInRange("raining", -.2, -100.0, 100.0);
        m_affectInWater = builder
                .comment("Whether 'raining' sanity also applies when the player is standing in water (rain still applies regardless)")
                .define("affect_in_water", true);
        m_hotSpring = builder
                .comment("Sanity gain per second while standing in a TFC hot spring (tfc:spring_water). This takes priority over rain and regular water")
                .defineInRange("hot_spring", .2, -100.0, 100.0);
        m_affectThirsty = builder
                .comment("Whether low TFC thirst should drain sanity")
                .define("affect_thirsty", true);
        m_thirstyThreshold = builder
                .comment("Players' sanity will start getting affected with TFC thirst at and below this percentage (0-100)")
                .defineInRange("thirsty_threshold", 40, 0, 100);
        m_thirsty = builder
                .comment("Players with TFC thirst at and below <thirsty_threshold> percent gain this amount of sanity per second")
                .defineInRange("thirsty", -.2, -100.0, 100.0);
        m_affectHot = builder
                .comment("Whether being in hot TFC environments should drain sanity")
                .define("affect_hot", true);
        m_hot = builder
                .comment("Players in hot TFC environments gain this amount of sanity per second")
                .defineInRange("hot", -.15, -100.0, 100.0);
        m_hotTemperature = builder
                .comment("The instantaneous TFC environment temperature (in degrees Celsius) above which the environment is considered hot")
                .defineInRange("hot_temperature", 25.0, -100.0, 100.0);
        m_affectCold = builder
                .comment("Whether being in cold TFC environments should drain sanity")
                .define("affect_cold", true);
        m_cold = builder
                .comment("Players in cold TFC environments gain this amount of sanity per second")
                .defineInRange("cold", -.15, -100.0, 100.0);
        m_coldTemperature = builder
                .comment("The instantaneous TFC environment temperature (in degrees Celsius) below which the environment is considered cold")
                .defineInRange("cold_temperature", 5.0, -100.0, 100.0);
        m_hungerThreshold = builder
                .comment("Players' sanity will start getting affected with food levels at and below this threshold (in half-drumsticks)")
                .defineInRange("hunger_threshold", 8, 0, 20);
        m_hungry = builder
                .comment("Players with food levels at and below <hunger_threshold> gain this amount of sanity per second")
                .defineInRange("hungry", -.2, -100.0, 100.0);
        m_pet = builder
                .comment("Players will gain this amount of sanity per second while being near a familiar TFC animal")
                .defineInRange("pet", .05, -100.0, 100.0);
        m_petFamiliarity = builder
                .comment("Minimum TFC familiarity (0-1) an animal needs to have for it to count as a pet and provide sanity")
                .defineInRange("pet_familiarity", .3, 0.0, 1.0);
        m_monster = builder
                .comment("Players will gain this amount of sanity per second while being near any monsters")
                .comment("This value is doubled if the monster is aggressive towards the player")
                .defineInRange("monster", -.1, -100.0, 100.0);
        m_darkness = builder
                .comment("Players will gain this amount of sanity per second while being in the dark")
                .defineInRange("darkness", -.15, -100.0, 100.0);
        m_darknessThreshold = builder
                .comment("Maximum light level considered to be darkness (inclusive)")
                .defineInRange("darkness_threshold", 4, 0, 15);
        m_lightness = builder
                .comment("Players will gain this amount of sanity per second while being in the light")
                .defineInRange("lightness", .00, -100.0, 100.0);
        m_lightnessThreshold = builder
                .comment("Minimum light level considered to be lightness (inclusive)")
                .defineInRange("lightness_threshold", 10, 0, 15);
        m_jukeboxPleasant = builder
                .comment("Nearby jukebox playing a pleasant melody gives this amount of sanity per second")
                .defineInRange("jukebox_pleasant", .08, -100.0, 100.0);
        m_jukeboxUnsettling = builder
                .comment("Nearby jukebox playing an unsettling melody gives this amount of sanity per second (this takes priority over pleasant melodies)")
                .defineInRange("jukebox_unsettling", -.11, -100.0, 100.0);

        List<String> path = new ArrayList<>();
        path.add("blocks");

        m_passiveBlocks = builder.comment(
                "Define a list of blocks that affect sanity of players standing near them",
                "A block should be included as follows: block_registry_name[property1=value1,property2=value2];A;B;C",
                "Where A is how much sanity is gained per second, B is a radius in blocks,",
                "C is whether a block needs to be naturally generated (not placed by player) (true/false)",
                "Boolean block state properties can be matched with '=true'/'=false'",
                "Integer block state properties support comparisons: 'heat_level>0', 'heat_level>=2', 'heat_level<5', 'heat_level<=4', 'heat_level=0'",
                "Prefix with TAG_ and follow with a tag registry name to define all blocks with the tag",
                "NOTE: not everything may work correctly with any configuration, e.g. multiblocks like tall flowers and beds; needs testing")
                .defineListAllowEmpty(path, ConfigDefault::passiveBlocksDefault, ConfigManager::stringEntryIsValid);

        builder.pop();
        builder.comment("Configuration for active sanity sources").push("active");

        m_sleeping = builder
                .comment("Sleeping through the night restores this amount of sanity")
                .defineInRange("sleeping", 50.0, -100.0, 100.0);
        m_hurtRatio = builder
                .comment("Players will gain sanity based on the damage they take from any sources with the ratio of 1 to this number")
                .defineInRange("hurt_ratio", -1.0, -100.0, 100.0);
        m_advancement = builder
                .comment("Earning an advancement gives this amount of sanity")
                .defineInRange("advancement", 10.0, -100.0, 100.0);
        m_animalBreeding = builder
                .comment("TFC animals mating within <animal_breeding_radius> blocks of a player give this amount of sanity")
                .defineInRange("animal_breeding", 2.0, -100.0, 100.0);
        m_animalBreedingCd = builder
                .comment("Animal breeding cooldown (see notes above), real time in seconds")
                .defineInRange("animal_breeding_cd", 600.0, 0.0, Float.MAX_VALUE);
        m_animalBreedingRadius = builder
                .comment("Radius in blocks around the mating TFC animals in which players receive sanity")
                .defineInRange("animal_breeding_radius", 4.0, 0.0, 64.0);
        m_eating = builder
                .comment("Consuming a TFC food gives this amount of sanity for every point of total nutrition (sum of all five nutrients)",
                        "The value is then multiplied by the modifiers from data/<namespace>/sanity/food/*.json rules matching the food")
                .defineInRange("eating", .5, -100.0, 100.0);
        m_eatingCd = builder
                .comment("Eating cooldown (see notes above), real time in seconds. Use 0 to make every food give its full value")
                .defineInRange("eating_cd", 0.0, 0.0, Float.MAX_VALUE);
        m_innerEntityKill = builder
                .comment("Killing an inner entity gives this amount of sanity")
                .defineInRange("inner_entity_kill", 3.0, -100.0, 100.0);

        path.clear();
        path.add("items");

        m_items = builder.comment(
                "Define a list of items that will affect sanity upon their usage",
                "An item should be included as follows: item_registry_name;A;B",
                "Where A is how much sanity is gained upon usage and B is a custom category",
                "Items with the same categories share the same cooldown",
                "The sanity gained will be multiplied by (timeSinceLastUsage / categoryCooldown) capping at 1.0")
                .defineListAllowEmpty(path, ConfigDefault::itemsDefault, ConfigManager::stringEntryIsValid);

        path.clear();
        path.add("item_categories");

        m_itemCats = builder.comment(
                "Define a list of custom categories for items specified in <items>",
                "A category should be included as follows: A;B",
                "Where A is a category id (integer) and B is a cooldown (in seconds) all items in this category share")
                .defineListAllowEmpty(path, ConfigDefault::itemCatsDefault, ConfigManager::stringEntryIsValid);

        builder.pop();
        builder.comment("Multiplayer configuration").push("multiplayer");

        m_sanePlayerCompany = builder
                .comment("Being around players with sanity higher than 50% gives this amount of sanity per second")
                .defineInRange("sane_player_company", .05, -100.0, 100.0);
        m_insanePlayerCompany = builder
                .comment("Being around players with sanity lower than 50% gives this amount of sanity per second")
                .defineInRange("insane_player_company", -.12, -100.0, 100.0);

        builder.pop();

        builder.comment("Entities configuration").push("entity");

        m_saneSeeInnerEntities = builder
                .comment(
                        "Whether sane players should be able to see and battle inner entities",
                        "Mobs will still be there server-side and will count towards passive sanity",
                        "Players who are targeted by inner entities see them regardless")
                .define("sane_see_inner_entities", false);
        m_innerEntityVisibleSanity = builder
                .comment("Inner entities become visible to players with sanity at or below this percentage")
                .defineInRange("visible_sanity", 25.0, 0.0, 100.0);
        m_innerEntitySpawnSanity = builder
                .comment("Inner entities start spawning around players with sanity at or below this percentage")
                .defineInRange("spawn_sanity", 20.0, 0.0, 100.0);
        m_innerEntityTargetSanity = builder
                .comment("Inner entities actively target and can be fought by players with sanity at or below this percentage")
                .defineInRange("target_sanity", 15.0, 0.0, 100.0);

        builder.pop();

        builder.comment("Client configuration").push("client");
        builder.comment("Sanity indicator configuration").push("indicator");

        m_renderIndicator = builder
                .comment("Whether to render sanity indicator")
                .define("render", true);
        m_twitchIndicator = builder
                .comment("Whether to twitch sanity indicator at low sanity levels")
                .define("twitch", true);
        m_twitchIndicatorSanity = builder
                .comment("Sanity indicator and inner monologue start twitching at or below this sanity percentage")
                .defineInRange("twitch_sanity", 50.0, 0.0, 100.0);
        m_indicatorScale = builder
                .comment("Sanity indicator scale")
                .defineInRange("scale", 1.0, 0.0, Float.MAX_VALUE);
        m_indicatorLocation = builder
                .comment("Sanity indicator location")
                .defineEnum("location", SanityIndicatorLocation.HOTBAR_LEFT);

        builder.pop();
        builder.comment("Inner monologue configuration").push("hints");

        m_renderHint = builder
                .comment("Whether to render inner monologue/random thoughts")
                .define("render", true);
        m_twitchHint = builder
                .comment("Whether to twitch inner monologue/random thoughts at low sanity levels")
                .define("twitch", true);
        m_hintsSanity = builder
                .comment("Inner monologue starts appearing at or below this sanity percentage")
                .defineInRange("hints_sanity", 40.0, 0.0, 100.0);

        builder.pop();
        builder.comment("Blood tendrils overlay configuration").push("blood_tendrils");

        m_renderBloodTendrilsOverlay = builder
                .comment("Whether to render blood tendrils overlay")
                .define("render", true);
        m_flashBtOnShortBurst = builder
                .comment("Whether to flash blood tendrils overlay upon losing sanity in a short burst")
                .define("short_burst_flash", true);
        m_renderBtPassive = builder
                .comment("Whether to render blood tendrils overlay when passively losing sanity")
                .define("render_passive", true);
        m_bloodTendrilsSanity = builder
                .comment("Blood tendrils only appear at or below this sanity percentage")
                .defineInRange("blood_sanity", 40.0, 0.0, 100.0);

        builder.pop();

        m_renderPost = builder
                .comment("Whether to render sanity postprocessing effects")
                .define("render_post", true);
        m_postStartSanity = builder
                .comment("Postprocessing effects start at or below this sanity percentage")
                .defineInRange("post_start_sanity", 40.0, 0.0, 100.0);
        m_postMaxSanity = builder
                .comment("Postprocessing effects reach their maximum strength at or below this sanity percentage")
                .defineInRange("post_max_sanity", 20.0, 0.0, 100.0);
        m_playSounds = builder
                .comment("Whether to enable sanity sound effects")
                .define("play_sounds", true);
        m_insanityVolume = builder
                .comment("Insanity ambience max volume")
                .defineInRange("insanity_volume", .6, .0, 1.0);
        m_ambienceStartSanity = builder
                .comment("Insanity ambience starts fading in at or below this sanity percentage")
                .defineInRange("ambience_start_sanity", 45.0, 0.0, 100.0);
        m_ambienceMaxSanity = builder
                .comment("Insanity ambience reaches full volume at or below this sanity percentage")
                .defineInRange("ambience_max_sanity", 20.0, 0.0, 100.0);
        m_heartbeatStartSanity = builder
                .comment("Heartbeat starts fading in at or below this sanity percentage")
                .defineInRange("heartbeat_start_sanity", 50.0, 0.0, 100.0);
        m_heartbeatMaxSanity = builder
                .comment("Heartbeat reaches full volume at or below this sanity percentage")
                .defineInRange("heartbeat_max_sanity", 20.0, 0.0, 100.0);
        m_hallucinationsStartSanity = builder
                .comment("Fake footsteps and hallucination sounds start at or below this sanity percentage")
                .defineInRange("hallucinations_start_sanity", 40.0, 0.0, 100.0);

        builder.pop();
    }

    private static List<String> passiveBlocksDefault()
    {
        List<String> list = new ArrayList<>();

        list.add("tfc:firepit[lit=true];0.1;4;false");
        list.add("tfc:grill[lit=true];0.1;4;false");
        list.add("tfc:pot[lit=true];0.1;4;false");
        list.add("tfc:stove[lit=true];0.1;4;false");
        list.add("tfc:stove_pot[lit=true];0.1;4;false");
        list.add("tfc:charcoal_forge[heat_level>0];0.1;4;false");

        return list;
    }

    private static List<String> itemsDefault()
    {
        List<String> list = new ArrayList<>();

        list.add("minecraft:pufferfish;-5;0");
        list.add("minecraft:poisonous_potato;-5;0");
        list.add("minecraft:spider_eye;-5;0");
        list.add("minecraft:rotten_flesh;-5;0");
        list.add("minecraft:chorus_fruit;-3;0");
        list.add("minecraft:ender_pearl;-1;0");
        list.add("minecraft:honey_bottle;6;1");
        list.add("minecraft:golden_carrot;7;1");
        list.add("minecraft:golden_apple;8;1");
        list.add("minecraft:enchanted_golden_apple;13;1");

        return list;
    }

    private static List<String> itemCatsDefault()
    {
        List<String> list = new ArrayList<>();

        list.add("0;0");
        list.add("1;800.0");

        return list;
    }
}
