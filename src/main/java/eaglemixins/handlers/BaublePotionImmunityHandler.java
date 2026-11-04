package eaglemixins.handlers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import eaglemixins.EagleMixins;
import eaglemixins.config.ForgeConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Makes players wearing certain baubles immune to certain potion effects
 * (Server Options > Bauble Potion Immunities). Default: the Quark Witch Hat blocks
 * PotionCore's Spin, Disorganization and Perplexity.
 *
 * Blocked effects are never applied, not even for one tick:
 *  - addPotionEffect (tipped arrows, mob/weapon effects, commands, ...) is denied through
 *    PotionApplicableEvent, which Forge fires before the effect is added, so it never ticks
 *    and is never synced to the client.
 *  - Instant applications (drinking, splash, lingering) skip that event; they are covered by
 *    the existing Potion.affectEntity redirects in ItemPotionMixin, EntityPotionMixin and
 *    EntityAreaEffectCloudMixin, which call isImmune(...) first.
 *  - Effects that were already active when the bauble was put on are removed at the start of
 *    the player tick (before updatePotionEffects), if "Bauble Immunity Removes Active Effects" is on.
 *
 * Registered only when Baubles is loaded. Baubles classes are only touched inside BaublesAccess,
 * so isImmune(...) is also safe to call from the mixins without Baubles installed.
 */
public class BaublePotionImmunityHandler {

    private static final boolean BAUBLES_LOADED = Loader.isModLoaded("baubles");

    private static Map<Item, Set<Potion>> immunities = null;
    private static Set<Potion> allBlockedPotions = null;

    /**
     * LOWEST so this runs last: for PotionApplicableEvent the last handler to set a result wins,
     * and nothing else should be able to re-allow an effect the bauble blocks.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        if (isImmune(event.getEntityLiving(), event.getPotionEffect().getPotion())) {
            event.setResult(Event.Result.DENY);
        }
    }

    /** START phase runs before EntityLivingBase.updatePotionEffects, so a removed effect never ticks again. */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.player.world.isRemote) return;
        if (!ForgeConfigHandler.server.baubleImmunityRemovesActiveEffects) return;

        getImmunities();
        if (allBlockedPotions.isEmpty()) return;

        EntityPlayer player = event.player;
        for (Potion potion : allBlockedPotions) {
            if (player.isPotionActive(potion) && isImmune(player, potion)) {
                player.removePotionEffect(potion);
            }
        }
    }

    /** True if the entity is a player wearing a configured bauble that blocks this potion. */
    public static boolean isImmune(EntityLivingBase entity, Potion potion) {
        if (!BAUBLES_LOADED || potion == null || !(entity instanceof EntityPlayer)) return false;

        Map<Item, Set<Potion>> map = getImmunities();
        if (map.isEmpty() || !allBlockedPotions.contains(potion)) return false;

        return BaublesAccess.wearsBlockingBauble((EntityPlayer) entity, potion, map);
    }

    private static Map<Item, Set<Potion>> getImmunities() {
        if (immunities == null) {
            Map<Item, Set<Potion>> map = new HashMap<>();
            Set<Potion> all = new HashSet<>();

            for (Map.Entry<ResourceLocation, ArrayList<String>> entry : ForgeConfigHandler.server.baublePotionImmunities.entrySet()) {
                if (!ForgeRegistries.ITEMS.containsKey(entry.getKey())) {
                    EagleMixins.LOGGER.warn("[Bauble Potion Immunities] Unknown item {}", entry.getKey());
                    continue;
                }
                Item item = ForgeRegistries.ITEMS.getValue(entry.getKey());
                Set<Potion> potions = map.computeIfAbsent(item, k -> new HashSet<>());

                for (String potionId : entry.getValue()) {
                    ResourceLocation potionLoc = new ResourceLocation(potionId.trim());
                    if (!ForgeRegistries.POTIONS.containsKey(potionLoc)) {
                        EagleMixins.LOGGER.warn("[Bauble Potion Immunities] Unknown potion {}", potionLoc);
                        continue;
                    }
                    Potion potion = ForgeRegistries.POTIONS.getValue(potionLoc);
                    potions.add(potion);
                    all.add(potion);
                }
            }

            allBlockedPotions = Collections.unmodifiableSet(all);
            immunities = Collections.unmodifiableMap(map);
        }
        return immunities;
    }

    public static void refreshConfig() {
        immunities = null;
        allBlockedPotions = null;
    }

    /** Kept separate so Baubles classes are only loaded when Baubles is present. */
    private static class BaublesAccess {
        static boolean wearsBlockingBauble(EntityPlayer player, Potion potion, Map<Item, Set<Potion>> map) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            if (baubles == null) return false;

            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                ItemStack stack = baubles.getStackInSlot(slot);
                if (stack.isEmpty()) continue;

                Set<Potion> blocked = map.get(stack.getItem());
                if (blocked != null && blocked.contains(potion)) return true;
            }
            return false;
        }
    }
}
