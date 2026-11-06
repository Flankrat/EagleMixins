package eaglemixins.mixin.iceandfire;

import com.github.alexthe666.iceandfire.block.BlockDreadStairs;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * BlockDreadStairs names itself with setRegistryName(String), which takes the namespace from whichever mod
 * is active when the block is constructed. In Dregora that is NuclearCraft, so the stairs register as
 * nuclearcraft:dread_stone_stairs: placed blocks have no model, Ice and Fire's recipes can't find the item,
 * and the dread mausoleum template (which uses iceandfire:dread_stone_stairs) places air instead.
 * Always register under iceandfire.
 */
@Mixin(BlockDreadStairs.class)
public abstract class BlockDreadStairsMixin {

    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/iceandfire/block/BlockDreadStairs;setRegistryName(Ljava/lang/String;)Lnet/minecraftforge/registries/IForgeRegistryEntry;"),
            remap = false
    )
    private IForgeRegistryEntry<Block> eagleMixins_iafNamespace(BlockDreadStairs instance, String name) {
        return instance.setRegistryName(new ResourceLocation("iceandfire", name));
    }
}
