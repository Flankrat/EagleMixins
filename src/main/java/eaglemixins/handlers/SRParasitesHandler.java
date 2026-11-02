package eaglemixins.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.*;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import eaglemixins.config.ForgeConfigHandler;
import eaglemixins.util.Ref;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SRParasitesHandler {

    //Parasites will be allowed to spawn via spawners, stay alive and will drop (reduced) loot in these biomes
    //Handled in ForgeConfigHandler class
    //Entries in config support "dimensionId@modid:biome" syntax (e.g. 1@biomesoplenty:steppe).
    //Entries with no "@" prefix (or a "*@" prefix) match that biome in any dimension.
    public static boolean isBiomeAllowed(ResourceLocation biomeId, int dimensionId) {
        String id = biomeId.toString();
        String modWildcard = biomeId.getNamespace() + ":*";

        boolean isInList = ForgeConfigHandler.srparasites.biomeList.stream()
                .anyMatch(entry -> matchesEntry(entry, id, modWildcard, dimensionId));

        //true (allowed) if in list and whitelist, or not in list and blacklist
        //false (not allowed) if in list and blacklist, or not in list and whitelist
        return isInList == ForgeConfigHandler.srparasites.biomeListIsWhitelist;
    }

    //Parasites with this ForgeData flag (e.g. from WakingNights) ignore the biome restrictions
    //Summon NBT: {ForgeData:{EagleMixinsOverworldAllowed:1b}}
    private static final String OVERWORLD_ALLOWED_KEY = "EagleMixinsOverworldAllowed";

    public static boolean isOverworldAllowed(Entity entity) {
        return entity.getEntityData().getBoolean(OVERWORLD_ALLOWED_KEY);
    }

    private static boolean matchesEntry(String entry, String biomeId, String biomeWildcard, int dimensionId) {
        String dimPart = null;
        String biomePart = entry;

        int atIndex = entry.indexOf('@');
        if (atIndex != -1) {
            dimPart = entry.substring(0, atIndex);
            biomePart = entry.substring(atIndex + 1);
        }

        // If a dimension is specified (and isn't a wildcard), it must match.
        if (dimPart != null && !dimPart.equals("*")) {
            try {
                if (Integer.parseInt(dimPart.trim()) != dimensionId) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // Malformed dimension prefix, treat as non-matching rather than crashing.
                return false;
            }
        }

        return biomePart.equals("*") || biomePart.equalsIgnoreCase(biomeWildcard) || biomePart.equalsIgnoreCase(biomeId);
    }

    public static boolean isBeckon(Entity entity){
        if(!(entity instanceof EntityPStationaryArchitect)) return false;
        return entity instanceof EntityVenkrol ||
                entity instanceof EntityVenkrolSII ||
                entity instanceof EntityVenkrolSIII ||
                entity instanceof EntityVenkrolSIV ||
                entity instanceof EntityVenkrolSV;
    }

    //Moves a Beckon summoned by a Stage IV Beckon to a random spot within the configured range
    //Returns false if no valid spot was found or too many Beckons are nearby, in which case the Beckon should not spawn
    public static boolean relocateSpreadBeckon(World world, EntityParasiteBase summoner, Entity beckon) {
        int minRange = Math.min(ForgeConfigHandler.srparasites.beckonSpreadMinRange, ForgeConfigHandler.srparasites.beckonSpreadMaxRange);
        int maxRange = Math.max(ForgeConfigHandler.srparasites.beckonSpreadMinRange, ForgeConfigHandler.srparasites.beckonSpreadMaxRange);
        Random rand = summoner.getRNG();

        double maxRangeSq = maxRange * maxRange;
        int nearbyBeckons = world.getEntitiesWithinAABB(EntityPStationaryArchitect.class, summoner.getEntityBoundingBox().grow(maxRange),
                nearby -> nearby != summoner && isBeckon(nearby) && nearby.getDistanceSq(summoner) <= maxRangeSq).size();
        if (nearbyBeckons >= ForgeConfigHandler.srparasites.beckonSpreadMaxNearby) return false;

        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            double distance = minRange + rand.nextDouble() * (maxRange - minRange);
            int x = MathHelper.floor(summoner.posX + Math.cos(angle) * distance);
            int z = MathHelper.floor(summoner.posZ + Math.sin(angle) * distance);

            BlockPos pos = findBeckonGround(world, x, MathHelper.floor(summoner.posY), z);
            if (pos == null || !canBeckonSpreadTo(world, pos)) continue;

            beckon.setLocationAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, beckon.rotationYaw, beckon.rotationPitch);
            return true;
        }
        return false;
    }

    //Searches top-down around the summoners height for a free spot on solid ground
    private static BlockPos findBeckonGround(World world, int x, int y, int z) {
        if (!world.isBlockLoaded(new BlockPos(x, y, z))) return null;
        for (int dy = 8; dy >= -8; dy--) {
            BlockPos pos = new BlockPos(x, y + dy, z);
            BlockPos below = pos.down();
            if (!world.isAirBlock(pos) || !world.isAirBlock(pos.up())) continue;
            IBlockState stateBelow = world.getBlockState(below);
            //SRP doesn't let Beckons spawn on Infested Stain either
            if (stateBelow.getBlock() == SRPBlocks.InfestedStain) continue;
            if (stateBelow.isSideSolid(world, below, EnumFacing.UP)) return pos;
        }
        return null;
    }

    private static boolean canBeckonSpreadTo(World world, BlockPos pos) {
        ResourceLocation biomeReg = world.getBiome(pos).getRegistryName();
        if (biomeReg == null || !isBiomeAllowed(biomeReg, world.provider.getDimension())) return false;
        return !ForgeConfigHandler.abyssal.killAbyssalNexus || !biomeReg.equals(Ref.abyssalRiftReg);
    }

    private static final String ESCAPED_CHECKS_KEY = "eaglemixins:escapedChecks";

    // SRParasites in overworld Script Biome Whitelist, kill Beckons
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        if (world.isRemote || world.getTotalWorldTime() % 50 != 23) return;

        if (!(entity instanceof EntityParasiteBase)) return;

        //Kill all Beckons and Dispatchers in Abyssal Rift
        if (ForgeConfigHandler.abyssal.killAbyssalNexus && Ref.entityIsInAbyssalRift(entity) && entity instanceof EntityPStationaryArchitect)
            entity.setDead();

        //Only if enabled
        if(!ForgeConfigHandler.srparasites.killEscapedParasites) return;
        if(isOverworldAllowed(entity)) return;

        //Slowly kill Parasites outside specific biomes
        ResourceLocation biomeReg = entity.world.getBiome(entity.getPosition()).getRegistryName();
        if (biomeReg != null && SRParasitesHandler.isBiomeAllowed(biomeReg, entity.dimension)) {
            entity.getEntityData().removeTag(ESCAPED_CHECKS_KEY);
            return;
        }

        //Failsafe: parasites that out-heal the drain get removed after enough checks outside
        int escapedChecks = entity.getEntityData().getInteger(ESCAPED_CHECKS_KEY) + 1;
        if (escapedChecks >= ForgeConfigHandler.srparasites.escapedParasiteRemovalChecks) {
            entity.setDead();
            return;
        }
        entity.getEntityData().setInteger(ESCAPED_CHECKS_KEY, escapedChecks);

        float health = entity.getHealth();
        if (health > 1000)      entity.setHealth(health / 50);
        else if (health > 100)  entity.setHealth(health / 10);
        else                    entity.setHealth(health - 10);
    }

    // Nexus parasites (Beckons/Dispatchers) don't appear at all where they would be killed, whatever spawned them
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (event.getWorld().isRemote || !(entity instanceof EntityPStationaryArchitect)) return;

        if (ForgeConfigHandler.abyssal.killAbyssalNexus && Ref.entityIsInAbyssalRift(entity)) {
            event.setCanceled(true);
            return;
        }
        if (!ForgeConfigHandler.srparasites.killEscapedParasites || isOverworldAllowed(entity)) return;
        ResourceLocation biomeReg = event.getWorld().getBiome(entity.getPosition()).getRegistryName();
        if (biomeReg != null && !SRParasitesHandler.isBiomeAllowed(biomeReg, entity.dimension))
            event.setCanceled(true);
    }

    // SRParasites in overworld - cancel spawns if not in Whitelisted Biome and from spawner
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event){
        if(!event.isSpawner()) return;
        int dimensionId = event.getWorld().provider.getDimension();
        EntityLivingBase entity = event.getEntityLiving();
        if(!(entity instanceof EntityParasiteBase) || isOverworldAllowed(entity)) return;
        ResourceLocation biomeReg = event.getWorld().getBiome(entity.getPosition()).getRegistryName();
        if (biomeReg != null && !SRParasitesHandler.isBiomeAllowed(biomeReg, dimensionId))
            event.setResult(Event.Result.DENY);
    }

    // Parasites in the tear swap dimensions (Underneath) drop Tainted Tears instead of Blood Tears
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDropsTearSwap(LivingDropsEvent event) {
        if (event.getDrops().isEmpty()) return;
        EntityLivingBase entity = event.getEntityLiving();
        if (!(entity instanceof EntityParasiteBase)) return;
        if (!ForgeConfigHandler.srparasites.isTearSwapDimension(entity.dimension)) return;

        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!ForgeConfigHandler.srparasites.isTearSwapOriginal(stack)) continue;
            ItemStack replacement = ForgeConfigHandler.srparasites.getTearSwapReplacement(stack.getCount());
            if (!replacement.isEmpty())
                drop.setItem(replacement);
        }
    }

    // OW SRParasites cancel loot if not in whitelisted biome
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event){
        if(event.getDrops().isEmpty()) return;
        EntityLivingBase entity = event.getEntityLiving();

        //Return if config is disabled
        if (!ForgeConfigHandler.srparasites.modifyLoot) return;

        //Only for Parasites
        if(!(entity instanceof EntityParasiteBase)) return;

        //Only in the configured dimensions
        if (!ForgeConfigHandler.srparasites.isReplacementDropDimension(entity.dimension)) return;

        //But not to ones that have special names
        if (entity.hasCustomName() &&
                ForgeConfigHandler.srparasites.keepLootNames.stream().anyMatch(entity.getName()::contains))
            return;

        ResourceLocation biomeReg = entity.world.getBiome(entity.getPosition()).getRegistryName();

        if (biomeReg != null && SRParasitesHandler.isBiomeAllowed(biomeReg, entity.dimension)){
            List<EntityItem> itemsToRemove = new ArrayList<>();
            List<EntityItem> itemsToAdd = new ArrayList<>();
            for (EntityItem drop : event.getDrops()) {
                ResourceLocation itemId = drop.getItem().getItem().getRegistryName();
                if(itemId == null) continue;
                if(itemId.getNamespace().equals(Ref.SRPMODID)) {
                    //SRP loot never drops here, it only has a chance to be replaced
                    itemsToRemove.add(drop);
                    //default 0.375 based of healthmultiplier 0.5 & damagemultiplier 0.25 averaged out on 0.625 the overall strength of ow parasites compared to LC parasites.
                    if (entity.getRNG().nextFloat() < ForgeConfigHandler.srparasites.replacementDropChance) {
                        ItemStack replacement = ForgeConfigHandler.srparasites.getReplacementDrop();
                        if (!replacement.isEmpty())
                            itemsToAdd.add(new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, replacement));
                    }
                }
            }
            event.getDrops().removeAll(itemsToRemove);
            event.getDrops().addAll(itemsToAdd);
        } else {
            event.getDrops().clear();
            event.setCanceled(true);
        }
    }
}
