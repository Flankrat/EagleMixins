package eaglemixins.handlers;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import eaglemixins.config.ForgeConfigHandler;
import eaglemixins.config.folders.SRParasiteConfig;
import eaglemixins.network.PacketHandler;
import eaglemixins.network.PacketSyncCarapace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Carapace for SRParasites: heavy hits get divided down, and every point of absorbed damage
 * is drained from a per-mob budget that regenerates over time.
 */
public class ParasiteCarapaceHandler {
    private static final String SRP_NAMESPACE = "srparasites";
    // Stored in ForgeData -> EagleMixinsCarapace -> Amount & Time
    private static final String KEY_CARAPACE = "EagleMixinsCarapace";
    private static final String KEY_AMOUNT = "Amount";
    private static final String KEY_TIME = "Time";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target.world.isRemote || !(target instanceof EntityParasiteBase)) return;

        SRParasiteConfig.CarapaceConfig config = ForgeConfigHandler.srparasites.carapace;
        if (config.onlyMelee && !isMelee(event.getSource())) return;

        float maxBudget = getMaxBudget(target, config);
        if (maxBudget <= 0) return;

        float amount = event.getAmount();
        float wantedReduction = amount - amount / getDivisor(amount, config);
        if (wantedReduction <= 0) return;

        float budget = getCurrentBudget(target, maxBudget, config);
        float reduction = Math.min(wantedReduction, budget);
        event.setAmount(amount - reduction);

        NBTTagCompound data = getOrCreateCarapaceData(target);
        data.setFloat(KEY_AMOUNT, budget - reduction);
        data.setLong(KEY_TIME, target.world.getTotalWorldTime());
        PacketHandler.sendToAllTracking(new PacketSyncCarapace(target.getEntityId(), budget - reduction, maxBudget, config.carapaceRecoverPerTick), target);
    }

    // Players that start seeing a parasite need its current carapace for the Neat bar
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (!(target instanceof EntityParasiteBase) || !(event.getEntityPlayer() instanceof EntityPlayerMP)) return;

        SRParasiteConfig.CarapaceConfig config = ForgeConfigHandler.srparasites.carapace;
        float maxBudget = getMaxBudget((EntityLivingBase) target, config);
        if (maxBudget <= 0) return;

        float budget = getCurrentBudget(target, maxBudget, config);
        PacketHandler.sendTo(new PacketSyncCarapace(target.getEntityId(), budget, maxBudget, config.carapaceRecoverPerTick), (EntityPlayerMP) event.getEntityPlayer());
    }

    // Lazy regeneration: recover based on ticks since the last hit instead of ticking every parasite
    private static float getCurrentBudget(Entity entity, float maxBudget, SRParasiteConfig.CarapaceConfig config) {
        NBTTagCompound data = getCarapaceData(entity);
        float budget = data.hasKey(KEY_AMOUNT) ? data.getFloat(KEY_AMOUNT) : maxBudget;
        if (data.hasKey(KEY_TIME)) {
            long elapsed = Math.max(0, entity.world.getTotalWorldTime() - data.getLong(KEY_TIME));
            budget = budget + elapsed * config.carapaceRecoverPerTick;
        }
        return Math.min(maxBudget, budget);
    }

    private static boolean isMelee(DamageSource source) {
        Entity trueSource = source.getTrueSource();
        return trueSource != null && source.getImmediateSource() == trueSource && !source.isProjectile() && !source.isMagicDamage();
    }

    private static float getMaxBudget(EntityLivingBase entity, SRParasiteConfig.CarapaceConfig config) {
        ResourceLocation id = EntityList.getKey(entity);
        if (id == null || !SRP_NAMESPACE.equals(id.getNamespace())) return 0;
        if (config.carapaceMobBlacklist.contains(id.getPath())) return 0;
        return entity.getMaxHealth() * config.carapaceHealthMultiplier;
    }

    // Linear from minDivisor at minDmgThreshold to maxDivisor at maxDmgThreshold, clamped outside
    private static float getDivisor(float amount, SRParasiteConfig.CarapaceConfig config) {
        float range = config.maxDmgThreshold - config.minDmgThreshold;
        if (range <= 0) return amount >= config.maxDmgThreshold ? config.maxDivisor : config.minDivisor;
        float progress = MathHelper.clamp((amount - config.minDmgThreshold) / range, 0F, 1F);
        return Math.max(1F, config.minDivisor + progress * (config.maxDivisor - config.minDivisor));
    }

    // ForgeData -> EagleMixinsCarapace, created if missing. Use for writing.
    private static NBTTagCompound getOrCreateCarapaceData(Entity entity) {
        NBTTagCompound forgeData = entity.getEntityData();
        if (!forgeData.hasKey(KEY_CARAPACE, Constants.NBT.TAG_COMPOUND)) forgeData.setTag(KEY_CARAPACE, new NBTTagCompound());
        return forgeData.getCompoundTag(KEY_CARAPACE);
    }

    // ForgeData -> EagleMixinsCarapace, or an empty detached compound if missing. Use for reading.
    private static NBTTagCompound getCarapaceData(Entity entity) {
        return entity.getEntityData().getCompoundTag(KEY_CARAPACE);
    }
}
