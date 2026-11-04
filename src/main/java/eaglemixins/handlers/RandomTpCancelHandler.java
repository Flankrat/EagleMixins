package eaglemixins.handlers;


import eaglemixins.config.ForgeConfigHandler;
import eaglemixins.util.Ref;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class RandomTpCancelHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClick (PlayerInteractEvent.RightClickBlock event) {

        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if(!isTpMethodEnabled("enderTalisman")) return;

        if (!Ref.entityIsInAbyssalRift(entity) && !Ref.entityIsInAbyssalGate(entity)) return;

        ItemStack heldItem = entity.getHeldItemMainhand();
        if (heldItem.isEmpty()) return;

        Item enderTalisman = Item.getByNameOrId("forgottenitems:ender_talisman");
        if (enderTalisman == null || heldItem.getItem() != enderTalisman) return;

        applyTpCooldownDebuffs((EntityPlayer) entity);

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        if (event.getSource() != DamageSource.IN_WALL) return;
        if(!isTpMethodEnabled("wallDmg")) return;
        if (!Ref.entityIsInAbyssalRift(entity) && !Ref.entityIsInAbyssalGate(entity)) return;

        // Only teleport after suffocating without a break for "Suffocation Teleport Delay" ticks
        EntityPlayer player = (EntityPlayer) entity;
        long now = player.world.getTotalWorldTime();
        NBTTagCompound data = player.getEntityData();
        // Suffocation hurts every 10 ticks, a longer gap means the player got out
        if (!data.hasKey(suffocationStartKey) || now > data.getLong(suffocationLastKey) + 30L)
            data.setLong(suffocationStartKey, now);
        data.setLong(suffocationLastKey, now);

        if (now - data.getLong(suffocationStartKey) < ForgeConfigHandler.abyssal.suffocationTpDelay) return;
        data.setLong(suffocationStartKey, now);
        applyTpDebuffs(player);
    }

    private static final String suffocationStartKey = "SuffocationStart";
    private static final String suffocationLastKey = "SuffocationLast";

    //when throwing enderpearl
    @SubscribeEvent
    public static void onEnderPearlImpact(ProjectileImpactEvent.Throwable event) {
        if(!(event.getThrowable() instanceof EntityEnderPearl)) return;
        if(event.getThrowable().world.isRemote) return;
        EntityLivingBase entity = event.getThrowable().getThrower();
        if (!(entity instanceof EntityPlayer)) return;
        if(!isTpMethodEnabled("enderPearl")) return;
        if (!Ref.entityIsInAbyssalRift(entity) && !Ref.entityIsInAbyssalGate(entity)) return;

        applyTpCooldownDebuffs((EntityPlayer) entity);
    }

    //When eating chorus fruit
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        if (!(event.getItem().getItem() instanceof ItemChorusFruit)) return;
        if(!isTpMethodEnabled("chorusFruit")) return;
        if (!Ref.entityIsInAbyssalRift(entity) && !Ref.entityIsInAbyssalGate(entity)) return;

        applyTpCooldownDebuffs((EntityPlayer) entity);
    }

    //When applying a non-instant potion effect
    @SubscribeEvent
    public static void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if(entity.world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        if (!Ref.entityIsInAbyssalRift(entity) && !Ref.entityIsInAbyssalGate(entity)) return;

        if (isTpPotion(event.getPotionEffect().getPotion()))
            applyTpCooldownDebuffs((EntityPlayer) entity);
    }

    private static Set<Potion> tpPotions = null;
    public static Set<String> tpMethods = null;

    public static boolean isTpPotion(Potion potion) {
        if (tpPotions == null) {
            tpPotions = new HashSet<>();
            for (String potionString : ForgeConfigHandler.abyssal.randomTpPots) {
                if(!potionString.contains(":")) continue;
                //Surface Teleport is the punishment itself, counting it would punish in a loop
                if(potionString.trim().equals(SURFACE_TP_POTION)) continue;
                ResourceLocation location = new ResourceLocation(potionString);
                if (ForgeRegistries.POTIONS.containsKey(location))
                    tpPotions.add(ForgeRegistries.POTIONS.getValue(location));
            }
        }
        return tpPotions.contains(potion);
    }

    public static boolean isTpMethodEnabled(String method){
        if(tpMethods == null) tpMethods = Arrays.stream(ForgeConfigHandler.abyssal.randomTpPots)
                .filter(s -> !s.contains(":"))
                .collect(Collectors.toSet());
        return tpMethods.contains(method);
    }

    private static List<Potion> tpCooldownPotions = null;
    private static final String SURFACE_TP_POTION = "potioncore:teleport_surface";
    private static final List<String> tpCooldownPotionStrings = new ArrayList<>(Arrays.asList(
            "potioncore:potion_sickness",
            SURFACE_TP_POTION
    ));
    private static final String tpCooldownKey = "TeleportCooldown";

    /**
     * Punishes every listed teleport in the Abyssal Rift / Abyssal Gate.
     * There used to be a 5 second window after each punishment (plus a free first teleport) where
     * teleports went unpunished. It stopped the old onEnderTeleport handler from re-punishing the
     * Surface Teleport (which fires an EnderTeleportEvent); that handler is gone, and the window let
     * players spam teleports (Ender Queen's Crown, chorus fruit, teleport potions) into brutal towers.
     */
    public static void applyTpCooldownDebuffs(EntityPlayer player) {
        applyTpDebuffs(player);
    }

    /** Potion Sickness and Surface Teleport right away, and restarts the teleport cooldown */
    public static void applyTpDebuffs(EntityPlayer player) {
        player.getEntityData().setLong(tpCooldownKey, player.world.getTotalWorldTime());

        if (tpCooldownPotions == null) {
            tpCooldownPotions = new ArrayList<>();
            for (String potionString : tpCooldownPotionStrings) {
                ResourceLocation location = new ResourceLocation(potionString);
                if (ForgeRegistries.POTIONS.containsKey(location))
                    tpCooldownPotions.add(ForgeRegistries.POTIONS.getValue(location));
                else
                    tpCooldownPotions.add(null);
            }
        }

        // Potion Sickness
        if (tpCooldownPotions.get(0) != null)
            player.addPotionEffect(new PotionEffect(tpCooldownPotions.get(0), 200, 1));

        // Surface Teleport
        if (tpCooldownPotions.get(1) != null)
            player.addPotionEffect(new PotionEffect(tpCooldownPotions.get(1), 1, 0));
    }

    public static void refreshConfig(){
        tpPotions = null;
        tpMethods = null;
    }
}