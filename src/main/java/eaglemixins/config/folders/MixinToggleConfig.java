package eaglemixins.config.folders;

import eaglemixins.EagleMixins;
import fermiumbooter.annotations.MixinConfig;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = EagleMixins.MODID)
@SuppressWarnings("unused")
public class MixinToggleConfig {

    @Config.Comment("Adds metadata-specific fertility support to Serene Seasons, allowing shared-ID saplings (vanilla saplings, Biomes O' Plenty saplings, etc.) to have per-species seasonal fertility instead of being treated as one item.")
    @Config.Name("Metadata Fertility Support (SereneSeasons)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.sereneseasons.metadata.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "sereneseasons", desired = true, reason = "Requires mod to properly function")
    public boolean sereneSeasonsMetadataFertility = true;

    @Config.Comment("Enables serene seaons cropfertility for dynamictrees cancelling the growth-tick outside of fertile seasons")
    @Config.Name("Dynamic Trees Seasonal Growth (DynamicTrees/SereneSeasons)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.dynamictrees.seasongrowth.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "dynamictrees", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "sereneseasons", desired = true, reason = "Requires mod to properly function")
    public boolean dynamicTreesSeasonalGrowth = true;

    @Config.Comment("Improves fog performance and makes render distance changes update fog immediately.")
    @Config.Name("Faster Fog (Biomes O' Plenty)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.biomesoplenty.foghandler.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "biomesoplenty", desired = true, reason = "Requires mod to properly function")
    public boolean biomesOPlentyFogHandlerCache = true;

    @Config.Comment("Fixes broken fog colors after changing render distance.")
    @Config.Name("Render Distance Fog Fix (Dynamic Surroundings)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.dynamicsurroundings.fogblendradius.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "dsurround", desired = true, reason = "Requires mod to properly function")
    public boolean dynamicSurroundingsFogBlendRadiusFix = true;

    @Config.Comment("Will stop disarming mobs with living/sentient or dragonbone gear using the BS Disarm or the SME Disarmament enchantment.")
    @Config.Name("Stop disarming some gear (BS/SME/RLCombat)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.various.stopdisarming.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "mujmajnkraftsbettersurvival", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "somanyenchantments", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "bettercombatmod", desired = true, reason = "Requires RLCombat to properly function")
    public boolean stopDisarming = true;

    @Config.Comment("Renames Varied Commodities Orbs to optionally use metadata based display name lang key: \"rldregora.variedcommodities:orb:<metadata>.name\"")
    @Config.Name("Rename Orbs (VC)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.vc.orbrename.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "variedcommodities", desired = true, reason = "Requires mod to properly function")
    public boolean renameOrb = true;

    @Config.Comment("Renames Varied Commodities Ruby to gem_ruby_vc to avoid mix-ups.")
    @Config.Name("Rename Ruby (VC)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.vc.rubyrename.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "variedcommodities", desired = true, reason = "Requires mod to properly function")
    public boolean renameRuby = true;

    @Config.Comment("Treats player bosses differently if they generated in the Abyssal Rift biome.")
    @Config.Name("Abyssal Player Bosses (Giant Player Boss)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.playerbosses.abyssalriftspawn.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "playerbosses", desired = true, reason = "Requires mod to properly function")
    public boolean abyssalPlayerBosses = true;

    @Config.Comment({
            "Players that randomly spawn (first join, respawning without a bed) are moved to a safe spot once the area around them",
            "is fully generated, so OTG objects placed after the spawn point was picked can't trap them in blocks.",
            "Can also make players spawn inside buildings, see \"Safe Spawns\"."
    })
    @Config.Name("Safe Random Spawns (Vanilla/OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.safespawn.json", defaultValue = true)
    public boolean safeRandomSpawns = true;

    @Config.Comment({
            "Spawners only count player bosses with the same BossId for MaxNearbyEntities, instead of every player boss.",
            "Without it, several boss spawners close together fill up each other's count, and the later ones skip their boss for good."
    })
    @Config.Name("Player Boss Spawner Count Fix (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.spawner.json", defaultValue = true)
    public boolean playerBossSpawnerCount = true;

    @Config.Comment("Allows to change between loading screens with a modifiable waiting time between screens.")
    @Config.Name("Cycle Screens (Loading Screens)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.loadingscreens.cyclescreens.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "loadingscreens", desired = true, reason = "Requires mod to properly function")
    public boolean cycleLoadingScreens = true;

    @Config.Comment("Will show the progress for unpacking DregoraRL during first time startup.")
    @Config.Name("First Time Setup Progress (DregoraRL)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.dregorarl.firsttimesetup.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "dregorarl", desired = true, reason = "Requires mod to properly function")
    public boolean firstTimeStartupProgress = true;

    @Config.Comment("Allows to separately modify chance for mimics for unlooted underground chests specifically.")
    @Config.Name("Underground Mimics (RLArtifacts)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.rlartifacts.undergroundmimics.json", defaultValue = false)
    @MixinConfig.CompatHandling(modid = "artifacts", desired = true, reason = "Requires mod to properly function")
    public boolean undergroundMimics = false;

    @Config.Comment("Allows to modify what kind of blocks simplediff will see as being able to drink from.")
    @Config.Name("Extend Drinkable Blocks (SimpleDifficulty)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.simpledifficulty.drinkableblocks.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "simpledifficulty", desired = true, reason = "Requires mod to properly function")
    public boolean extendDrinkableBlocks = true;

    @Config.Comment("If players use teleportation methods in Abyssal Rift, they will get potion effects applied to prevent them to cheese brutal towers.")
    @Config.Name("Prevent Abyssal Teleportation (Vanilla/Trinkets)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.eaglemixins.vanilla.randomtp.json",
            lateMixin = "mixins.eaglemixins.xat.randomtp.json",
            defaultValue = true
    )
    @MixinConfig.CompatHandling(modid = "xat", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "potioncore", desired = true, reason = "Requires mod to properly function")
    public boolean punishAbyssalTeleportation = true;

    @Config.Comment("Compared to SRPs system, this will only count parasite kills, distribute the killed mobs HP over the equipped SRP living gear and also evolve living bows and living armor.")
    @Config.Name("Custom Living Gear Evolution (SRP)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.srparasites.disabledefaultgearevo.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "srparasites", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "charm", disableMixin = false, desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "srpmixins", disableMixin = false, desired = true, reason = "Optional compat", warnIngame = false)
    public boolean customSRPGearEvolution = true;

    @Config.Comment({
            "Makes the distance at which Stage IV Beckons spawn their Beckons, the max amount of nearby Beckons and whether those Beckons can grow configurable under \"SRParasites Options\".",
            "The spawn is cancelled if the position is not in an allowed parasite biome or is in a biome where Nexus parasites get killed."
    })
    @Config.Name("Stage IV Beckon Spread (SRP)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.srparasites.beckonspread.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "srparasites", desired = true, reason = "Requires mod to properly function")
    public boolean stageIVBeckonSpread = true;

    @Config.Comment({
            "Enables the per-weapon critical-hit and range damage multipliers configured under \"Weapon Damage Modifiers\".",
            "This Mixin Toggle makes RLCombat flag the damage source as a critical hit in order to modify damage that is modified by other mods."
    })
    @Config.Name("Weapon Damage Modifiers (RLCombat/Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.weapondamagemodifiers.json", lateMixin = "mixins.eaglemixins.bettercombatmod.weapondamagemodifiers.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "bettercombatmod", desired = true, reason = "Requires RLCombat to properly function")
    public boolean weaponDamageModifiersRLCombat = true;

    @Config.Comment({
            "Enables the per-weapon critical-hit and range damage multipliers configured under \"Weapon Damage Modifiers\".",
            "This Mixin Toggle makes Vanilla flag the damage source as a critical hit in order to modify damage that is modified by other mods.",
            "This is safe to have enabled while using the RLCombat version."
    })
    @Config.Name("Weapon Damage Modifiers (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.weapondamagemodifiers.json", defaultValue = true)
    public boolean weaponDamageModifiersVanilla = true;

    @Config.Comment("Allow NuclearCraft blocks with a Reskillable level requirement to still be right click interactable.")
    @Config.Name("Override Block Interaction Lock (Reskillable)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.reskillable.rightclickblockoverride.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "reskillable", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "nuclearcraft", disableMixin = false, desired = true, reason = "Mixin not needed without NuclearCraft", warnIngame = false)
    public boolean overrideReskillableLock = true;

    @Config.Comment("Change NuclearCraft chunk radiation to be variable over subchunks (16 blocks high slices of a chunk).")
    @Config.Name("SubChunk Radiation (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.subchunkradiation.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean subchunkRadiation = true;

    @Config.Comment("Will multiply all rads by 1000 to make them better readable. Also will stop displaying negligible radiation in item tooltips.")
    @Config.Name("Change Rads Scale (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.radiationscalechange.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean radiationScaleChange = true;

    @Config.Comment("Allows to modify the radiation some fluid blocks give off, making them work similar to Corium fluid.")
    @Config.Name("Radiating Fluids (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.radiatingfluids.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean radiatingFluids = true;

    @Config.Comment("Fixes Hazmat Suit taking durability dmg from armor ignoring dmg like poison etc.")
    @Config.Name("Fix Hazmat Dmg (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.fixhazmatdmg.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean fixHazmatDmg = true;

    @Config.Comment("Makes EagleMixins Radiation Resistance attribute reduce the radiation an entity is receiving.")
    @Config.Name("Rad Resistance Attribute (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.radresistanceattribute.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean radResistanceAttribute = true;

    @Config.Comment("Fixes potion effects applied by NuclearCraft via config onto irradiated entities having a too short duration, not showing particles and using the potion level instead of amplifier.")
    @Config.Name("Fix Radiation Effects (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.radiationeffects.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean fixRadiationEffects = true;

    @Config.Comment("Makes armor keep its current durability when applying radiation shielding.")
    @Config.Name("Fix Shielding Recipe (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.shieldingrecipe.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean fixShieldingRecipe = true;

    @Config.Comment("Allows to define radiation values for containers with loot tables that havent generated loot yet. Also fixes NuclearCraft automatically generating the loot in such chests.")
    @Config.Name("Loot Table Radiation (NuclearCraft)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.loottableradiation.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    public boolean lootTableRadiation = true;

    @Config.Comment("Will make a lot of otherwise safe inventories also irradiate the player and the area if they have irradiated items, such as Shulker Boxes and Charm Crates, worn Backpacks and Tool Belts and opened Ender Chests. Also takes care of the one item a player can hold with their mouse when having a GUI open, and the 2x2 crafting matrix in the players inventory.")
    @Config.Name("Inventory Radiation (NuclearCraft/Charm/WearableBackpacks/ToolBelt)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.nuclearcraft.inventoryradiation.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "nuclearcraft", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "charm", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "wearablebackpacks", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "toolbelt", desired = true, reason = "Requires mod to properly function")
    public boolean inventoryRadiation = true;

    @Config.Comment("Caps potion effect levels to the values in Server Options > Potion Level Caps.")
    @Config.Name("Potion Level Caps (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.potionlevelcap.json", defaultValue = true)
    public boolean potionLevelCaps = true;

    @Config.Comment("Make players regain their air slowly when resurfacing from submerged - instead of regaining it instantly.")
    @Config.Name("Slow Air Refill (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.slowairrefill.json", defaultValue = true)
    public boolean slowAirRefill = true;

    @Config.Comment("Allow NBT (loot tables etc) using vanillas RandomValueRange to have min > max. Swap those in that case so the given min value will be interpreted as max and vice versa.")
    @Config.Name("Fix Eagle Math (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.randomvaluerange_eaglemath.json", defaultValue = true)
    public boolean fixEagleMath = true;

    @Config.Comment("Make Sussyberians and Mentalberians keep their berian state over zombification and curing.")
    @Config.Name("Zombie Berians (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.zombieberians.json", defaultValue = true)
    public boolean zombieBerians = true;

    @Config.Comment("Extend climbable property of open trapdoors to needing any ladder below the trapdoor instead of just vanilla ladders.")
    @Config.Name("Climbable Trapdoors (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.climbabletrapdoors.json", defaultValue = true)
    public boolean climbableTrapdoors = true;

    @Config.Comment("Renders GUI hearts differently if the player is irradiated.")
    @Config.Name("Radiation Hearts (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.customradiationhearts.json", defaultValue = true)
    public boolean customRadiationHearts = true;

    @Config.Comment("Fixes forge ignoring default attributes (atk dmg/speed/armor/toughness) of gear if there is additional attributes set via the set_attributes loot function. Also allows to fix old gear from dregora 1.0.4 (deprecated, should be removed soon).")
    @Config.Name("Item Attributes (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.itemattributes.json", defaultValue = true)
    public boolean itemAttributes = true;

    @Config.Comment("Prevents Observers from ticking a redstone pulse on world gen")
    @Config.Name("Patch Observer Ticking (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.observertickonchunkgen.json", defaultValue = true)
    public boolean patchObserversTickingOnWorldGen = true;

    @Config.Comment("Ice upgrades for the fridge will also treat SimpleDifficulty ice chunks as ice.")
    @Config.Name("More Ice in Fridge (CookingForBlockheads/SimpleDifficulty)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.cookingforblockheads.moreice.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "cookingforblockheads", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.CompatHandling(modid = "simpledifficulty", desired = true, reason = "Requires mod to properly function")
    public boolean moreIceInFridge = true;

    @Config.Comment("Makes all CookingForBlockheads counters allow to have generatable loot using the vanilla loot table system.")
    @Config.Name("Counters allow Loot Tables (CookingForBlockheads)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.cookingforblockheads.loottables.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "cookingforblockheads", desired = true, reason = "Requires mod to properly function")
    public boolean counterLootTables = true;

    @Config.Comment("Allows CookingForBlockheads counters to have custom inventory names using the vanilla system (rename the item and place it).")
    @Config.Name("Custom Inventory Names (CookingForBlockheads)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.cookingforblockheads.custominventorynames.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "cookingforblockheads", desired = true, reason = "Requires mod to properly function")
    public boolean customInventoryNames = true;

    @Config.Comment("Caches sourceBlock hashes to greatly reduce performance for some OTG BO3 type structures. This skips the unnecessarily expensive MaterialSet.contains() call.")
    @Config.Name("BO3 Material Performance (OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.otg.bo3materialperformance.json", defaultValue = false)
    @MixinConfig.CompatHandling(modid = "openterraingenerator", desired = true, reason = "Requires mod to properly function")
    public boolean bo3getMaterialPerformanceFix = false;

    @Config.Comment("Caches BlockFunctions for some (rotatingRandomly=false) BO3 type OTG structures instead of gathering them again every time that structure generates.")
    @Config.Name("BO3 BlockFunction Cache (OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.otg.bo3blockfunctioncache.json", defaultValue = false)
    @MixinConfig.CompatHandling(modid = "openterraingenerator", desired = true, reason = "Requires mod to properly function")
    public boolean bo3blockFunctionCache = false;

    @Config.Comment("Modifies the world creation process to limit users to creating only the preset world type")
    @Config.Name("Create World Simplification (OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.otg.createworldsimplify.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "openterraingenerator", desired = true, reason = "Requires mod to properly function")
    public boolean otgCreateWorldFix = true;

    @Config.Comment("Stops crashing when OTG biomes are missing and instead uses a different biome for the missing one. No guarantees that this is gonna be structurally sound biome layering")
    @Config.Name("Old world compatibility (OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.otg.fixmissingbiomecrash.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "openterraingenerator", desired = true, reason = "Requires mod to properly function")
    public boolean otgFixMissingBiomeCrash = true;

    @Config.Comment("Prevents Fish's Undead Rising Undertaker, Avaton and Skeleton King summons from dropping loot or XP.")
    @Config.Name("Cancel Summon Loot & XP (FUR)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.fishsundeadrising.nolootforsummons.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "mod_lavacow", desired = true, reason = "Requires mod to properly function")
    public boolean noLootForFURSummons = true;

    @Config.Comment("Changes how Fish's Undead Rising Skeleton Kings are summoned. Allows biomes with at least one of the following BiomeDictionary types: MESA, SAVANNA, SANDY or DEAD.")
    @Config.Name("Modify Skeleton King (FUR)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.fishsundeadrising.modifyskeletonking.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "mod_lavacow", desired = true, reason = "Requires mod to properly function")
    public boolean modifiedSkeletonKing = true;

    @Config.Comment("Cancel Fish's Undead Rising Reaper Scythe custom sweep handling.")
    @Config.Name("Cancel Reaper Scythe Custom Sweep (FUR)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.fishsundeadrising.cancelcustomsweep.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "mod_lavacow", desired = true, reason = "Requires mod to properly function")
    public boolean cancelCustomSweep = true;

    @Config.Comment("Fish's Undead Rising Infested effect spawns FUR parasites when undead mobs die. This limits how many parasites can spawn from one entity.")
    @Config.Name("Limit Infested Effect (FUR)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.fishsundeadrising.limitparasiteinfestation.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "mod_lavacow", desired = true, reason = "Requires mod to properly function")
    public boolean limitInfestedEffect = true;

    @Config.Comment("By default, Legendary Tooltips only allows 16 different tooltip frames. This increases that max to 64.")
    @Config.Name("More Frames (LegendaryTooltips)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.legendarytooltips.moreframes.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "legendarytooltips", desired = true, reason = "Requires mod to properly function")
    public boolean moreLegendaryTooltipFrames = true;

    @Config.Comment("Allows to modify mob equipment (mainly zombies and skeletons) set in the \"Mob Equipment\" config.")
    @Config.Name("Mob Equipment Modification (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.mobequipment.json", defaultValue = true)
    public boolean mobEquipmentModify = true;

    @Config.Comment("Allows skeletons to use Spartan Weaponry and benefit from weapon stats. Options in the \"Mob Equipment\" config. If \"Modded Arrow Skeletons\" is disabled, skeletons using crossbows will always use normal bolts, and skeletons using longbows will use the same arrow behaviors like when they have a normal bow.")
    @Config.Name("Spartan Skeletons (Vanilla/SpartanWeaponry)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.eaglemixins.vanilla.rangedaiweapons.json",
            lateMixin = "mixins.eaglemixins.spartanweaponry.rangedaiweapons.json",
            defaultValue = true
    )
    @MixinConfig.CompatHandling(modid = "spartanweaponry", desired = true, reason = "Requires mod to properly function")
    public boolean enableSpartanRangedSkeletons = true;

    @Config.Comment("Adds an inverse buff to Spartan Weaponry's Two-Handed I/II debuff: wielding the weapon with an empty off-hand (i.e. the debuff isn't active) increases final damage by a percentage (see \"Weapon Damage\" config for the amount, default +25%/+50%). Also documents the buff on the weapon's tooltip alongside the existing debuff description.")
    @Config.Name("Two-Handed Weapon Buff (SpartanWeaponry)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.spartanweaponry.twohandedbuff.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "spartanweaponry", desired = true, reason = "Requires mod to properly function")
    public boolean enableSpartanTwoHandedBuff = true;

    @Config.Comment("Allows all AbstractSkeletons (stray, wither skellie, FUR forsaken etc) to use offhand special arrows like tipped arrows, spectral arrows etc. This is also needed for the \"Tipped Arrows\" section to work.")
    @Config.Name("Modded Arrow Skeletons (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.skeletonarrows.json", defaultValue = true)
    public boolean enabledModdedArrowsForAll = true;

    @Config.Comment("Mobs holding any bow or crossbow in mainhand will pickup tipped arrows or tipped bolts if they can pickup items at all. Only tipped arrows/bolts with potion types defined in the tipped arrow config are viable to pickup.")
    @Config.Name("Mobs Pickup Tipped Arrows (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.pickuptippedarrows.json", defaultValue = true)
    public boolean mobsPickupArrows = true;

    @Config.Comment("Cancels generation of strongholds between 0 and 10.000 blocks from spawn")
    @Config.Name("Limit Stronghold Generation (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.mapgenstronghold.json", defaultValue = true)
    public boolean limitStrongholdGen = true;

    @Config.Comment("Account for OTG when Limit Stronghold Generation is set to true")
    @Config.Name("OTG Stronghold Limiter (OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.CompatHandling(modid = "openterraingenerator", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.otg.tweakstronghold.json", defaultValue = true)
    public boolean limitOTGStrongholdGen = true;

    @Config.Comment({
            "Replaces the biome search of /otg tp with a nearest-first search spread over multiple ticks and adds a radius argument: /otg tp <biome> [radius] [-p player].",
            "Avoids OTG's search freezing the server and permanently keeping hundreds of MB of biome arrays in memory. Radius limits are under \"OTG Biome Search\"."
    })
    @Config.Name("Better Biome Teleport Search (OTG)")
    @Config.RequiresMcRestart
    @MixinConfig.CompatHandling(modid = "openterraingenerator", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.otg.biomesearch.json", defaultValue = true)
    public boolean otgBiomeSearch = true;

    @Config.Comment("Stops blocks affected by gravity from updating when they are generated during worldgen")
    @Config.Name("Blocks do not fall during world gen (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.stopworldgenfallingblocks.json", defaultValue = true)
    public boolean stopFallingBlocks = true;

    @Config.Comment({
            "Allows to set qualities to only generate if the stack was created by one of a set of given loottables.",
            "Use \"loottables: [...]\" in QT cfg item matchers to denote for which loottable(s) the given quality should generate"
    })
    @Config.Name("QualityTools Loottables (QualityTools)")
    @Config.RequiresMcRestart
    @MixinConfig.CompatHandling(modid = "qualitytools", desired = true, reason = "Requires mod to properly function")
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.eaglemixins.vanilla.qualitytoolsloot.json",
            lateMixin = "mixins.eaglemixins.qualitytools.loottablequalities.json",
            defaultValue = true
    )
    public boolean enableQualityLootTables = true;

    @Config.Comment("Allows namespaced nbt enchantments to be used within nbt data using STRING_TAG: 'name' ")
    @Config.Name("Namespaced NBT Enchantments (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.nbtnamespaceenchants.json", defaultValue = true)
    public boolean enableNamespaceNBT = true;

    @Config.Comment("Restricts mob spawner rendering (spinning mob model) for performance. Occluded spawners and spawners too far away won't render the mob inside.")
    @Config.Name("Spawner Render Limiter (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.spawnerrendercancel.json", defaultValue = true)
    public boolean limitSpawnerRendering = true;

    @Config.Comment("Gives the glowing outline of dropped lock keys their key color instead of white")
    @Config.Name("Key Outline Color (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.eaglemixins.vanilla.keyoutlinecolor.json", defaultValue = true)
    public boolean keyOutlineColor = true;

    @Config.Comment({
            "This restores old set bonus behavior of removing set bonus potion effects when losing the bonus (removing armor etc) no matter the duration or amplifier of the current effect on the player.",
            "This counteracts players using wine/wildberry wine to avoid getting the effect removed."
    })
    @Config.Name("Remove Effects Aggressively (SetBonus)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.setbonus.effectremoval.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "setbonus", desired = true, reason = "Tweak for SetBonus requires SetBonus to apply.")
    public boolean removeSetBonusEffectsCorrectly = true;

    @Config.Comment("Shows the remaining carapace of parasites as a small bar below the Neat health bar.")
    @Config.Name("Carapace Bar (Neat/SRParasites)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(lateMixin = "mixins.eaglemixins.neat.carapacebar.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "neat", desired = true, reason = "Optional compat with Neat", warnIngame = false)
    @MixinConfig.CompatHandling(modid = "srparasites", desired = true, reason = "Optional compat with Neat for SRP carapace", warnIngame = false)
    public boolean neatCarapaceBar = true;
}
