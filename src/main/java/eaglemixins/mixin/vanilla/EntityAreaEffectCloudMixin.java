package eaglemixins.mixin.vanilla;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import eaglemixins.handlers.RandomTpCancelHandler;
import eaglemixins.util.Ref;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(EntityAreaEffectCloud.class)
public class EntityAreaEffectCloudMixin {
    @WrapOperation(
            method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/potion/Potion;affectEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/EntityLivingBase;ID)V")
    )
    private void eaglemixins_areaEffectCloud_affectEntity(Potion instance, @Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entity, int amplifier, double health, Operation<Void> original) {
        original.call(instance, source, indirectSource, entity, amplifier, health);

        if(entity.world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        if (!Ref.entityIsInAbyssalRift(entity)) return;

        if (RandomTpCancelHandler.isTpPotion(instance))
            RandomTpCancelHandler.applyTpCooldownDebuffs((EntityPlayer) entity);
    }
}
