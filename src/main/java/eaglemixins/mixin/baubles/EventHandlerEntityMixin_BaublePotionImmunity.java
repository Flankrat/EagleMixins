package eaglemixins.mixin.baubles;

import baubles.api.IBauble;
import baubles.common.event.EventHandlerEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eaglemixins.handlers.BaublePotionImmunityHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Baubles calls IBauble.onWornTick for every worn bauble each player tick.
// Hooking that call lets a configured bauble cure the effects it blocks while it is worn,
// no matter which mod gives the item its IBauble (the Quark Witch Hat gets it from RLMixins).
@Mixin(value = EventHandlerEntity.class, remap = false)
public abstract class EventHandlerEntityMixin_BaublePotionImmunity {

    @WrapOperation(
            method = "playerTick",
            at = @At(value = "INVOKE", target = "Lbaubles/api/IBauble;onWornTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V")
    )
    private void eaglemixins_removeBlockedEffects(IBauble bauble, ItemStack stack, EntityLivingBase wearer, Operation<Void> original) {
        original.call(bauble, stack, wearer);
        BaublePotionImmunityHandler.onBaubleWornTick(stack, wearer);
    }
}
