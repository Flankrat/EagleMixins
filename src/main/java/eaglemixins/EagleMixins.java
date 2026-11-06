package eaglemixins;

import eaglemixins.client.AmbientLightningHandler;
import eaglemixins.client.gui.TeleportOverlayHandler;
import eaglemixins.client.particles.ParticlesClientRunner;
import eaglemixins.compat.ForgottenItemsUtil;
import eaglemixins.compat.ModLoadedUtil;
import eaglemixins.config.ForgeConfigHandler;
import eaglemixins.debug.BO3_ChunkGen_Debug;
import eaglemixins.handlers.*;
import eaglemixins.client.render.RenderPlayerBoss;
import eaglemixins.client.render.RenderWastelandLightning;
import eaglemixins.entity.EntityPlayerBoss;
import eaglemixins.init.ModEntities;
import eaglemixins.init.ModBlocks;
import eaglemixins.init.ModStats;
import eaglemixins.network.PacketHandler;
import eaglemixins.teleport.entity.EntitySpawnListener;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = EagleMixins.MODID,
        version = EagleMixins.VERSION,
        name = EagleMixins.NAME,
        dependencies =
                "required-after:fermiumbooter@[1.3.2,);" +
                "required-after:betterconfig@[1.3.0,);" +
                "before:forgottenitems;"
)
public class EagleMixins {

    public static final String MODID = "eaglemixins";
    public static final String VERSION = "1.3.1";
    public static final String NAME = "EagleMixins";
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final boolean debugEnabled = false;
    public static final String CFG_VERSION = "1.0";

    @Mod.Instance(value = MODID)
    public static EagleMixins INSTANCE;

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        if(ForgeConfigHandler.mixintoggles.weaponDamageModifiersVanilla || ForgeConfigHandler.mixintoggles.weaponDamageModifiersRLCombat)
            MinecraftForge.EVENT_BUS.register(WeaponDamageHandler.class);

        if(event.getSide() == Side.CLIENT) registerIfModsPresent(new String[]{"nuclearcraft"}, ParticlesClientRunner.class);

        ModEntities.init();
        if(event.getSide() == Side.CLIENT) RenderingRegistry.registerEntityRenderingHandler(EntityPlayerBoss.class, RenderPlayerBoss::new);
        if(event.getSide() == Side.CLIENT) RenderingRegistry.registerEntityRenderingHandler(EntityLightningBolt.class, RenderWastelandLightning::new);

        MinecraftForge.EVENT_BUS.register(ProjectileImmunityHandler.class);
        registerIfModsPresent(new String[]{"iceandfire"}, DreadStoneRemapHandler.class);
        registerIfModsPresent(new String[]{"firstaid"}, FirstAidRadiationHandler.class);
        registerIfModsPresent(new String[]{"nuclearcraft"}, ContainerNBTRadHandler.class);
        registerIfModsPresent(new String[]{"nuclearcraft"}, ItemRadCapAttacher.class);
        registerIfModsPresent(new String[]{"nuclearcraft"}, AttributeHandler.class);
        registerIfModsPresent(new String[]{"srparasites", "nuclearcraft"}, EntityBurningTickHandler.class);
        //MinecraftForge.EVENT_BUS.register(BlockNoclipHandler.class); //let ppl suffocate
        MinecraftForge.EVENT_BUS.register(NuclearCraftInteractions.class);
        registerIfModsPresent(new String[]{"srparasites", "playerbosses"}, AbyssalRiftHandler.class);
        MinecraftForge.EVENT_BUS.register(AngryMobHandler.class);
        MinecraftForge.EVENT_BUS.register(BarrierBlockHandler.class);
        MinecraftForge.EVENT_BUS.register(BerianHandler.class);
        registerIfModsPresent(new String[]{"biomesoplenty"}, BerryDebuffHandler.class);
        registerIfModsPresent(new String[]{"champions", "playerbosses"}, BlightedShivaxiHandler.class);
        registerIfModsPresent(new String[]{"biomesoplenty"}, BlockDropsHandler.class);
        MinecraftForge.EVENT_BUS.register(ConductivityHandler.class);
        registerIfModsPresent(new String[]{"nuclearcraft"}, RadioactiveLightningHandler.class);
        if(event.getSide() == Side.CLIENT) MinecraftForge.EVENT_BUS.register(AmbientLightningHandler.class);
        if(ForgeConfigHandler.server.enableDamageFalloff) MinecraftForge.EVENT_BUS.register(DamageFalloffHandler.class);
        MinecraftForge.EVENT_BUS.register(DismountHandler.class);
        MinecraftForge.EVENT_BUS.register(WildfireHandler.class);
        MinecraftForge.EVENT_BUS.register(LockVanishHandler.class);
        MinecraftForge.EVENT_BUS.register(PlayerBossDamageHandler.class);
        MinecraftForge.EVENT_BUS.register(DispelEntityHandler.class);
        registerIfModsPresent(new String[]{"srparasites", "playerbosses"}, EntityRenameCancelHandler.class);
        MinecraftForge.EVENT_BUS.register(FallDamageHandler.class);
        MinecraftForge.EVENT_BUS.register(FallDamageNegation.class);
        registerIfModsPresent(new String[]{"bettercombatmod", "mod_lavacow"}, FURHandler.class);
        if(ForgeConfigHandler.server.asmodeusCraftingItems) registerIfModsPresent(new String[]{"mod_lavacow"}, AsmodeusDropsHandler.class);
        registerIfModsPresent(new String[]{"firstaid"}, HealthValidationHandler.class);
        MinecraftForge.EVENT_BUS.register(PotionEffectsByFluidsHandler.class);
        registerIfModsPresent(new String[]{"nuclearcraft"},RadiationResistanceApplier.class);
        MinecraftForge.EVENT_BUS.register(RandomTippedArrowHandler.class);
        if(ForgeConfigHandler.mixintoggles.punishAbyssalTeleportation) MinecraftForge.EVENT_BUS.register(RandomTpCancelHandler.class);
        MinecraftForge.EVENT_BUS.register(RecallFlightCancelHandler.class);
        if(ForgeConfigHandler.mixintoggles.safeRandomSpawns) MinecraftForge.EVENT_BUS.register(SafeSpawnHandler.class);
        if(ForgeConfigHandler.mixintoggles.customSRPGearEvolution) registerIfModsPresent(new String[]{"srparasites", "charm", "srpmixins"}, SentientWeaponEvolutionHandler.class);
        if(ForgeConfigHandler.mixintoggles.enableSpartanTwoHandedBuff) registerIfModsPresent(new String[]{"spartanweaponry"}, SpartanTwoHandedBuffHandler.class);
        registerIfModsPresent(new String[]{"srparasites", "biomesoplenty"}, SRParasitesHandler.class);
        if(ForgeConfigHandler.srparasites.carapace.enabled) registerIfModsPresent(new String[]{"srparasites"}, ParasiteCarapaceHandler.class);

        MinecraftForge.EVENT_BUS.register(PotionRadiationFatigueHandler.class);
        MinecraftForge.EVENT_BUS.register(RadiationPotionBlockHandler.class);
        registerIfModsPresent(new String[]{"nuclearcraft", "biomesoplenty"}, DoorDupeHandler.class);
        MinecraftForge.EVENT_BUS.register(AbyssalGateHandler.class);
        if(ForgeConfigHandler.irradiated.enabled) registerIfModsPresent(new String[]{"nuclearcraft"}, IrradiatedParasitesHandler.class);
        registerIfModsPresent(new String[]{"cookingforblockheads"}, TileCounterHandler.class);
        registerIfModsPresent(new String[]{"battletowers"}, GolemInfightingHandler.class);
        if(ForgeConfigHandler.teleporter.enableTeleporters) MinecraftForge.EVENT_BUS.register(new TeleportEventHandler());
        if(ForgeConfigHandler.mixintoggles.otgBiomeSearch) registerIfModsPresent(new String[]{"openterraingenerator"}, OTGBiomeSearchHandler.class);

        if(debugEnabled) {
            registerIfModsPresent(new String[]{"openterraingenerator"}, BO3_ChunkGen_Debug.class);
        }
    }

    private static void registerIfModsPresent(String[] dependencies, Class<?> classToRegister){
        for (String dependency : dependencies)
            if(!Loader.isModLoaded(dependency)) return;
        MinecraftForge.EVENT_BUS.register(classToRegister);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        BiomeTagHandler.init();
        if(ModLoadedUtil.forgottenitems.isLoaded()) ForgottenItemsUtil.remapRecipeCoreItems();
        if(Loader.isModLoaded("enhancedvisuals") && Loader.isModLoaded("nuclearcraft")) EnhancedVisualsHandler.init();
        if(ForgeConfigHandler.teleporter.enableTeleporters) EntitySpawnListener.init();
        ModStats.init();
        ModBlocks.init();

        if (event.getSide().isClient()) {
            ForgeConfigHandler.loadParticleRulesFromConfig();
            if(ForgeConfigHandler.teleporter.enableTeleporters) MinecraftForge.EVENT_BUS.register(TeleportOverlayHandler.class);
            registerIfModsPresent(new String[]{"nuclearcraft"}, ContainerNBTRadHandler.Tooltip.class);
            MinecraftForge.EVENT_BUS.register(WildfireHandler.Tooltip.class);
        }

        PacketHandler.init();
    }
}

