package eaglemixins.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Worlds made before the dread stone registry name fix (see BlockDreadStairsMixin) have the stairs and slabs
 * saved as nuclearcraft:dread_stone_*. Remap those IDs to iceandfire so placed blocks and items survive.
 */
public class DreadStoneRemapHandler {

    private static final String OLD_NAMESPACE = "nuclearcraft";
    private static final String NEW_NAMESPACE = "iceandfire";
    private static final Set<String> PATHS = new HashSet<>(Arrays.asList(
            "dread_stone_stairs",
            "dread_stone_slab",
            "dread_stone_slab_double"
    ));

    @SubscribeEvent
    public static void onMissingBlocks(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getAllMappings()) {
            ResourceLocation target = getTarget(mapping.key);
            if (target == null) continue;
            Block block = ForgeRegistries.BLOCKS.getValue(target);
            if (block != null && target.equals(block.getRegistryName())) mapping.remap(block);
        }
    }

    @SubscribeEvent
    public static void onMissingItems(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
            ResourceLocation target = getTarget(mapping.key);
            if (target == null) continue;
            Item item = ForgeRegistries.ITEMS.getValue(target);
            if (item != null && target.equals(item.getRegistryName())) mapping.remap(item);
        }
    }

    private static ResourceLocation getTarget(ResourceLocation key) {
        if (!OLD_NAMESPACE.equals(key.getNamespace()) || !PATHS.contains(key.getPath())) return null;
        return new ResourceLocation(NEW_NAMESPACE, key.getPath());
    }
}
