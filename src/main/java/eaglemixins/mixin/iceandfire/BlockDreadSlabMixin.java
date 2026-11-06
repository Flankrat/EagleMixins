package eaglemixins.mixin.iceandfire;

import com.github.alexthe666.iceandfire.block.BlockDreadSlab;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Same issue as BlockDreadStairsMixin: the half and double dread stone slabs name themselves with
 * setRegistryName(String) and end up as nuclearcraft:dread_stone_slab(_double) in Dregora.
 * Always register under iceandfire.
 */
@Mixin(BlockDreadSlab.class)
public abstract class BlockDreadSlabMixin {

    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/block/BlockDreadSlab;setRegistryName(Ljava/lang/String;)Lnet/minecraftforge/registries/IForgeRegistryEntry;"),
            remap = false
    )
    private IForgeRegistryEntry<Block> eagleMixins_iafNamespace(BlockDreadSlab instance, String name) {
        return instance.setRegistryName(new ResourceLocation("iceandfire", name));
    }
}
