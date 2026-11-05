package eaglemixins.mixin.baubles;

import baubles.api.IBauble;
import baubles.common.container.SlotBauble;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eaglemixins.handlers.BaublePotionImmunityHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Baubles calls IBauble.onEquipped when a bauble is put into a bauble slot.
// Hooking that call lets a configured bauble cure the effects it blocks as soon as it is equipped,
// no matter which mod gives the item its IBauble (the Quark Witch Hat gets it from RLMixins,
// which doesn't override onEquipped).
@Mixin(SlotBauble.class)
public abstract class SlotBaubleMixin_BaublePotionImmunity {

    @WrapOperation(
            method = "putStack",
            at = @At(value = "INVOKE", target = "Lbaubles/api/IBauble;onEquipped(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", remap = false)
    )
    private void eaglemixins_removeBlockedEffects(IBauble bauble, ItemStack stack, EntityLivingBase wearer, Operation<Void> original) {
        original.call(bauble, stack, wearer);
        BaublePotionImmunityHandler.onBaubleEquipped(stack, wearer);
    }
}
