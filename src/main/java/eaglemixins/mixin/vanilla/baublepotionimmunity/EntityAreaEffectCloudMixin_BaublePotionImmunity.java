package eaglemixins.mixin.vanilla.baublepotionimmunity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import eaglemixins.handlers.BaublePotionImmunityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

/** Instant potion effects skip PotionApplicableEvent, so blocked effects are stopped here before they apply. */
@Mixin(EntityAreaEffectCloud.class)
public abstract class EntityAreaEffectCloudMixin_BaublePotionImmunity {
    @WrapWithCondition(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potion;affectEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityLivingBase;ID)V")
    )
    private boolean eaglemixins_areaEffectCloud_baublePotionImmunity(Potion instance, @Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entity, int amplifier, double health) {
        return !BaublePotionImmunityHandler.isImmune(entity, instance);
    }
}
