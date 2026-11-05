package eaglemixins.config;

import eaglemixins.EagleMixins;
import eaglemixins.client.particles.ParticleRule;
import eaglemixins.client.particles.ParticlesClientRunner;
import eaglemixins.client.particles.ParticlesRuleParser;
import eaglemixins.config.folders.*;
import eaglemixins.handlers.BaublePotionImmunityHandler;
import eaglemixins.util.PotionLevelCaps;
import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import meldexun.betterconfig.api.Order;
import meldexun.betterconfig.api.tree.IConfigCategory;
import meldexun.betterconfig.api.tree.IConfigContext;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import javax.annotation.Nullable;
import java.util.*;

@BetterConfig(
		modid = EagleMixins.MODID,
		version = EagleMixins.CFG_VERSION,
		addDefaultsToComments = false
)
public class ForgeConfigHandler {

	@Config.Comment("Server-Side Options")
	@Config.Name("Server Options")
	@Order(0)
	public static ServerConfig server = new ServerConfig();

	@Config.Comment("Client-Side Options")
	@Config.Name("Client Options")
	@Order(1)
	public static ClientConfig client = new ClientConfig();

	@Config.Comment("Irradiated Options")
	@Config.Name("Irradiated Options")
	@Order(2)
	public static IrradiatedConfig irradiated = new IrradiatedConfig();

	@Config.Comment("Conductivity Options")
	@Config.Name("Conductivity Options")
	@Order(3)
	public static ConductivityConfig conductivity = new ConductivityConfig();

	@Config.Comment("Tipped Arrow Options")
	@Config.Name("Tipped Arrow Options")
	@Order(4)
	public static TippedArrowConfig tippedarrows = new TippedArrowConfig();

	@Config.Comment("Berian Options")
	@Config.Name("Berian Options")
	@Order(5)
	public static BerianConfig berian = new BerianConfig();

	@Config.Comment("SRParasites Options")
	@Config.Name("SRParasites Options")
	@Order(6)
	public static SRParasiteConfig srparasites = new SRParasiteConfig();

	@Config.Comment("Abyssal Rift Options")
	@Config.Name("Abyssal Rift Options")
	@Order(7)
	public static AbyssalConfig abyssal = new AbyssalConfig();

	@Config.Comment("Disable to not modify any code")
	@Config.Name("Mixin Toggles")
	@Order(8)
	@SuppressWarnings("unused")
	public static MixinToggleConfig mixintoggles = new MixinToggleConfig();

	@Config.Comment("NuclearCraft Options")
	@Config.Name("NuclearCraft Options")
	@Order(9)
	public static NuclearConfig nuclear = new NuclearConfig();

	@Config.Comment("Modify Gear of some Mobs")
	@Config.Name("Mob Equipment")
	@Order(10)
	public static MobEquipmentConfig mobequipment = new MobEquipmentConfig();

	@Config.Comment("Modify teleporter behavior")
	@Config.Name("Teleporter")
	@Order(11)
	public static TeleporterConfig teleporter = new TeleporterConfig();

	@Config.Comment("Per-weapon critical-hit and range damage multipliers")
	@Config.Name("Weapon Damage Modifiers")
	@Order(12)
	public static WeaponDamageConfig weapondamage = new WeaponDamageConfig();

	@Config.Comment("Chance for some mobs (currently wolves) to turn hostile toward players")
	@Config.Name("Angry Mobs")
	@Order(13)
	public static AngryMobConfig angrymobs = new AngryMobConfig();

	@Config.Comment("Thorium ore generation in the nether. Requires NuclearCraft")
	@Config.Name("Nether Thorium Ore")
	@Order(14)
	public static NetherThoriumConfig netherthorium = new NetherThoriumConfig();

	@Config.Comment("Weapons that inflict the Wildfire effect")
	@Config.Name("Wildfire")
	@Order(15)
	public static WildfireConfig wildfire = new WildfireConfig();

	@Config.Comment("Biome search used by /otg tp <biome> [radius]")
	@Config.Name("OTG Biome Search")
	@Order(16)
	public static OTGBiomeSearchConfig otgbiomesearch = new OTGBiomeSearchConfig();

	@Config.Comment("Obsidian lock blocks that vanish when opened with their matching one-time key")
	@Config.Name("Locked Blocks")
	@Order(17)
	public static LockedBlockConfig lockedblocks = new LockedBlockConfig();

	@Config.Comment("Bosses that look like players, with skeleton AI, flight and optional dragon wings")
	@Config.Name("Player Bosses")
	@Order(18)
	public static PlayerBossConfig playerbosses = new PlayerBossConfig();

	@Config.Comment("Random spawns (first join, respawning without a bed), requires the \"Safe Random Spawns\" mixin toggle")
	@Config.Name("Safe Spawns")
	@Order(19)
	public static SafeSpawnConfig safespawn = new SafeSpawnConfig();

	@Config.Comment("Extra lightning in radioactive areas, red/black lightning in the Underneath and dark lightning in the nuclear wasteland")
	@Config.Name("Lightning")
	@Order(20)
	public static LightningConfig lightning = new LightningConfig();

	public static class ServerConfig {
		@Config.Comment("Reduces damage done by players by a factor of 1 to 1/3, with the falloff multiplier that scales linearly in the span between. No reduction for damages below 20. Caps out at 300 dmg before falloff.")
		@Config.Name("Enable Damage Falloff")
		public boolean enableDamageFalloff = false;

		@Config.Comment("Add Blocks you can drink from, will be treated like water blocks")
		@Config.Name("Additional Water Blocks:")
		public Set<String> waterblockListdrinkables = new LinkedHashSet<>(Arrays.asList(
				"cookingforblockheads:sink",
				"cookingforblockheads:sink_oak",
				"cookingforblockheads:sink_spruce",
				"cookingforblockheads:sink_birch",
				"cookingforblockheads:sink_jungle",
				"cookingforblockheads:sink_acacia",
				"cookingforblockheads:sink_darkoak"
		));

		@Config.Comment("Give Dismounting entities the ability to dismount players when they target a player in Abyssal Rift or Parasite biomes")
		@Config.Name("Dismount on target:")
		public boolean dismounterTarget = true;

		@Config.Comment("Cannot rename any mob with or to these names")
		@Config.Name("Blacklisted Name Change Any Mob")
		public String[] blackListEntitiesNameChangeAny = {
				"Dismounting",
				"Dismounter",
				"Dispel",
				"Sarevok",
				"IHaveNoClue",
				"Jester"
		};

		@Config.Comment("Any riding entity that takes more than this amount of damage will automatically be dismounted. Set negative to disable")
		@Config.Name("Dismount Damage Threshold")
		@Config.RangeDouble(min = -1)
		public float dismountThreshold = 6;

		@Config.Comment("Taking any amount of damage from these sources will automatically dismount any riding entity")
		@Config.Name("Dismounting Damage Types")
		public Set<String> dismountDamageTypes = new LinkedHashSet<>(Collections.singletonList("lightningBolt"));

		@Config.Comment("Cannot rename Player Bosses with or to these names")
		@Config.Name("Blacklisted Name Change Player Bosses")
		public String[] blackListEntitiesNameChangePlayerbosses = {
				"Blighted Shivaxi"
		};

		@Config.Comment("Cannot rename parasites with or to these names")
		@Config.Name("Blacklisted Name Change Parasite")
		public String[] blackListEntitiesNameChangeParasite = {
				"Sentient Horror",
				"Degrading Overseer",
				"Malformed Observer",
				"Shivaxi",
				"Corrupted Carrier",
				"Necrotic Blight"
		};

		@Config.Comment("Health multiplier for Blighted Shivaxi")
		@Config.Name("Blighted Shivaxi Health Multiplier")
		public float blightedShivaxiHealthModifier = 8.5F;

        @Config.Comment("Armor multiplier for Blighted Shivaxi")
        @Config.Name("Blighted Shivaxi Armor Multiplier")
        public float blightedShivaxiArmorModifier = 1.0F;

        @Config.Comment("Damage multiplier for Blighted Shivaxi")
        @Config.Name("Blighted Shivaxi Damage Multiplier")
        public float blightedShivaxiDamageModifier = 2.8F;

		@Config.Comment("If true, Battle Towers golems do not turn on each other when one golem damages another")
		@Config.Name("Prevent Golem Infighting")
		public boolean preventGolemInfighting = true;

		@Config.Comment("Chance of an underground un-looted chest becoming a mimic")
		@Config.Name("Underground Mimic Chance")
		@Config.RangeDouble(min = 0D, max = 1D)
		public double undergroundMimicChance = 0.05D;

		@Config.Comment("List of dimension IDs in which underground chests will have a chance to become mimics")
		@Config.Name("Underground Mimic Dimensions")
		@Config.RequiresMcRestart
		public Integer[] undergroundMimicDimensions = {0};

		@Config.Comment({
				"Registers the End crafting items Void Tear, Enigmoth Eye and Ghostly Gills.",
				"These items are meant to replace the Brutal Coins in Asmodeus' summoning recipe in RLCraft Dregora.",
				"This changes the item registry, so it must be set the same on the server and on every client."
		})
		@Config.Name("Asmodeus crafting items")
		@Config.RequiresMcRestart
		public boolean asmodeusCraftingItems = true;

		@Config.Comment({
				"OTG preset used for the overworld when a new world is created, regardless of the world's folder name (level-name).",
				"This lets hosts name their world anything while still generating this preset.",
				"Leave empty to keep OTG's default behavior (preset name = world folder name).",
				"Only affects newly created worlds, existing worlds keep the preset saved in their OpenTerrainGenerator/Dimensions config."
		})
		@Config.Name("OTG Overworld Preset")
		public String otgOverworldPreset = "DregoraRL";

		@Config.Name("Fix Biomes O Plenty Door Duplication")
		@Config.Comment("Prevents Biomes O Plenty doors from dropping twice when broken")
		public boolean fixBOPDoorDupe = true;

		@Config.Comment("Attaches the given BiomeDictionary tags to the specified biome.")
		@Config.Name("BiomeDictionary Tag List")
		@Config.RequiresMcRestart
		public Map<ResourceLocation, ArrayList<String>> biomeDictionaryTagList = new LinkedHashMap<>();
		private void initBiomeDictTagList(){
			biomeDictionaryTagList.put(new ResourceLocation("nuclearcraft:nuclear_wasteland"), new ArrayList<>(Collections.singletonList("NUCLEAR")));
			biomeDictionaryTagList.put(new ResourceLocation("palebloom:pale_garden"), new ArrayList<>(Collections.singletonList("PALE")));
			biomeDictionaryTagList.put(new ResourceLocation("srparasites:biome_parasite"), new ArrayList<>(Collections.singletonList("PARASITE")));
		}

		@Config.Comment({
				"Stops Lycanites Mobs' fluid lake generation (ooze/poison/acid/moglava lakes) from generating in biomes carrying any of these BiomeDictionary tags.",
				"One tag per line.",
				"Example: NUCLEAR"
		})
		@Config.Name("Lycanites Mobs Fluid Lake Disabled Biome Tags")
		public Set<String> lycanitesGenerationDisabledBiomeTags = new LinkedHashSet<>(Collections.singletonList("PALE"));

		@Config.Comment({
				"Stops Lycanites Mobs' dungeons from building blocks in chunks whose biome carries any of these BiomeDictionary tags.",
				"Lycanites only checks the biome at a dungeon's origin, so dungeons starting nearby could otherwise extend into these biomes.",
				"Dungeons crossing into such a chunk are cut off at its edge.",
				"One tag per line.",
				"Example: ABYSSAL"
		})
		@Config.Name("Lycanites Mobs Dungeon Disabled Biome Tags")
		public Set<String> lycanitesDungeonDisabledBiomeTags = new LinkedHashSet<>(Arrays.asList("ABYSSAL", "PARASITE", "PALE", "NUCLEAR"));

		@Config.Comment({
				"Stops Ice and Fire's Pixie Village structure from generating in biomes carrying any of these BiomeDictionary tags.",
				"One tag per line.",
				"Example: NUCLEAR"
		})
		@Config.Name("Pixie Village Disabled Biome Tags")
		public Set<String> pixieVillageDisabledBiomeTags = new LinkedHashSet<>(Arrays.asList("PALE", "PARASITE"));

		@Config.Comment({
				"Forgotten Item Crafting Rune Recipe Overrides for the guaranteed core item.",
				"This will not affect the other 8 items in the recipe.",
				"For example:",
				"    \"forgottenitems:golem_helmet\" can have \"rlmixins:steel_helmet\" replace \"minecraft:diamond_helmet\""
		})
		@Config.Name("Crafting Rune's Recipe Core Item")
		@Config.RequiresMcRestart
		public Map<ResourceLocation, ResourceLocation> forgottenItemsRecipeCoreItems = new LinkedHashMap<>();

		@Config.Comment({
				"Caps the level of potion effects. Any effect applied above its cap is lowered to the cap.",
				"Levels are as displayed in game (1 = level I, 3 = level III).",
				"Requires the \"Potion Level Caps (Vanilla)\" mixin toggle.",
				"Format: <potion_id>=<max level>",
				"Example: minecraft:resistance=3"
		})
		@Config.Name("Potion Level Caps")
		public Map<ResourceLocation, Integer> potionLevelCaps = new LinkedHashMap<>();
		private void initPotionLevelCaps(){
			potionLevelCaps.put(new ResourceLocation("minecraft:resistance"), 3);
			potionLevelCaps.put(new ResourceLocation("bountifulbaubles:sinful"), 4);
			potionLevelCaps.put(new ResourceLocation("minecraft:strength"), 4);
			potionLevelCaps.put(new ResourceLocation("potioncore:magic_focus"), 2);
		}

		@Config.Comment({
				"Baubles that make the wearer immune to the listed potion effects.",
				"Blocked effects are never applied: they are denied before being added, so they never tick, not even once.",
				"Instant applications (drinking, splash, lingering) don't fire PotionApplicableEvent, so they aren't blocked.",
				"Effects already active when the bauble is put on are removed with the \"Bauble Potion Immunity Removes Active Effects (Baubles)\" mixin toggle.",
				"Requires Baubles.",
				"Format: <bauble item id> { <potion id> ... }"
		})
		@Config.Name("Bauble Potion Immunities")
		public Map<ResourceLocation, ArrayList<String>> baublePotionImmunities = new LinkedHashMap<>();
		private void initBaublePotionImmunities(){
			baublePotionImmunities.put(new ResourceLocation("quark:witch_hat"), new ArrayList<>(Arrays.asList(
					"potioncore:spin",
					"potioncore:disorganization",
					"potioncore:perplexity")));
		}

		public ServerConfig(){
			initBiomeDictTagList();
			initPotionLevelCaps();
			initBaublePotionImmunities();
		}
	}

	public static class ClientConfig {
		@Config.Comment("How many seconds to display each loadingscreen picture")
		@Config.Name("LoadingScreens: Frequency")
		public int frequency = 10;

		@Config.Comment("Whether the loadingscreen pictures are displayed in set order or randomised order")
		@Config.Name("LoadingScreens: Display in Random Order")
		public boolean randomOrder = true;

		@Config.Comment("Whether there should be only one loadingscreen picture displayed per loading, no cycling during load. Will always display first picture in list if random order config is off")
		@Config.Name("LoadingScreens: Don't Cycle")
		public boolean disableCycling = false;

        @Config.Comment({
                "Adds Particles to biomes",
                "Syntax: <dimension>,<biomeid>,[<blockid>|SOLID|<another block id>|<leave empty for all blocks>],[<particle>@color|<particle additional>@color|<randomly selected>],<ticksbetweenruns>,<maxperrun>,<chance>,<yoffset>,<add random y block>,<rise>,<range>,<canseesky|true|false>,<miny>,<maxy>,[thunder|rain|clear|leave empty for all],<optional biomedictionarytag overrules biomeid>",
                "Particles accept optional color via '@': REDSTONE@#RRGGBB, SPELL_MOB@r,g,b (0..1 or 0..255), NOTE@hue(0..1).",
                "Example: all, all, [], [SPELL_MOB@121,189,101], 3, 8, 1.0, 0.0, 0.01, 20, true, 0, 256, [], NUCLEAR",
                "Example: 0, minecraft:plains, [minecraft:grass|minecraft:tallgrass], [VILLAGER_HAPPY@121,189,101|TOWN_AURA@121,189,101], 3, 8, 1.0, 0.0, 0.01, 20, true, 0, 256, [], NUCLEAR",
                "Particle List: use /particle"
        })
        @Config.Name("Particle Spawn System")
        public String[] ambientparticlespawnlist = {
                "all, all, [SOLID], [mobSpell@71,116,57|mobSpell@146,116,24], 6, 25, 1.0, 1.0, 0, 0.001, 30, true, 0, 256, [], all",
                "all, all, [], [totem], 6, 1, 1.0, 2.0, 10, 0, 30, true, 0, 256, [], all"
        };

        @Config.Comment("Max distance (blocks) at which a spawner's mob model renders. Beyond this, only the cage renders")
        @Config.Name("Spawners: Render Distance")
        @Config.RangeDouble(min = 0)
        public double spawnerRenderDistance = 32;
	}

	@BetterConfig.AfterRead
	@SuppressWarnings("unused")
	public static <T extends IConfigContext<T>> void migrateConfigs(IConfigCategory<T> config, T context, @Nullable ArtifactVersion version) {
		ConfigMigrator.handleMigration(config, context, version);
	}

	@Mod.EventBusSubscriber
	public static class EventHandler {

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(EagleMixins.MODID)) {
				BetterConfigManager.sync(EagleMixins.MODID);
				tippedarrows.reset();
				berian.reset();
				conductivity.reset();
				abyssal.reset();
				mobequipment.reset();
				teleporter.reset();
				srparasites.reset();
				nuclear.reset();
				lockedblocks.reset();
				safespawn.reset();
				lightning.reset();
				PotionLevelCaps.refreshConfig();
				BaublePotionImmunityHandler.refreshConfig();
				loadParticleRulesFromConfig();
			}
		}
	}

    public static void loadParticleRulesFromConfig() {
        String[] lines = client.ambientparticlespawnlist;
        List<ParticleRule> rules = ParticlesRuleParser.parse(lines);
        ParticlesClientRunner.install(rules);
        EagleMixins.LOGGER.info("[Particles] Installed {} rules", rules.size());
    }

}