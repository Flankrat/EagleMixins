package eaglemixins.mixin.xat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import eaglemixins.handlers.RandomTpCancelHandler;
import eaglemixins.util.Ref;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xzeroair.trinkets.traits.abilities.AbilityEnderQueen;

/**
 * Trinkets and Baubles 0.33+: every Ender Queen's Crown teleport (when hurt, and the keybind teleport)
 * goes through teleportRandomly(EntityLivingBase, float), so hooking its return value covers both.
 * require = 1 so a future Trinkets and Baubles change fails loudly instead of silently doing nothing.
 */
@Mixin(AbilityEnderQueen.class)
public abstract class EnderQueensCrownRandomTpCancel {
    @ModifyReturnValue(
            method = "teleportRandomly(Lnet/minecraft/entity/EntityLivingBase;F)Z",
            at = @At("RETURN"),
            remap = false,
            require = 1
    )
    private boolean eaglemixins_trinketsAndBaubles_teleportRandomly(boolean teleported, @Local(argsOnly = true) EntityLivingBase entity){
        if(!teleported) return false; //If tp failed for whatever reason, we don't punish (yet)
        if(entity.world.isRemote) return true;
        if (!(entity instanceof EntityPlayer)) return true;
        if (!RandomTpCancelHandler.isTpMethodEnabled("enderCrown")) return true;
        if (!Ref.entityIsInAbyssalRift(entity)) return true;
        RandomTpCancelHandler.applyTpCooldownDebuffs((EntityPlayer) entity);
        return true;
    }
}
