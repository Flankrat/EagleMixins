package eaglemixins.mixin.neat;

import com.llamalad7.mixinextras.sugar.Local;
import eaglemixins.client.CarapaceBarRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.neat.HealthBarRenderer;

@Mixin(HealthBarRenderer.class)
public abstract class HealthBarRenderer_CarapaceMixin {

    // Right after the health bar quads are drawn, still in plate space with textures disabled
    @Inject(
            method = "renderHealthBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;enableTexture2D()V", ordinal = 0)
    )
    private void eaglemixins_renderCarapaceBar(
            EntityLivingBase passedEntity, float partialTicks, Entity viewPoint,
            CallbackInfo ci,
            @Local(name = "entity") EntityLivingBase entity,
            @Local(name = "size") float size,
            @Local(name = "barHeight") int barHeight,
            @Local(name = "padding") float padding
    ) {
        CarapaceBarRenderer.render(entity, partialTicks, size, barHeight, padding);
    }
}
