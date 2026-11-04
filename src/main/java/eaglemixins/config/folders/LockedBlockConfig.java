package eaglemixins.config.folders;

import eaglemixins.EagleMixins;
import eaglemixins.blocks.BlockLockSlave;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LockedBlockConfig {

    @Config.Comment({
            "Lock types. Keys (eaglemixins:lock_key) and lock blocks (eaglemixins:locked_block) are paired through the LockId NBT tag:",
            "a key only opens locks with the same LockId and is used up when it opens one.",
            "Keyslaves (eaglemixins:keyslave, eaglemixins:keyslave_alt) touching an opened lock, or touching those keyslaves, vanish together with it.",
            "Format: <lock id> | <name> | <key color> | <key lore> | <lock lore> | <vanish delay> | <replace block> | <replace slave> | <replace alt>",
            "Lock id: any text, used in the NBT. Name: shown as \"<Name> Key\" / \"<Name> Lock\". Key color: hex, e.g. #8B1E1E",
            "Key lore: tooltip of the key. Lock lore: message shown when right-clicking the lock without its key.",
            "Lore can use \\n for new lines and \u00a7 color codes, and can be left empty. Only the lock id is required.",
            "Vanish delay: seconds between each block of this lock vanishing, e.g. 0.1 for big doors. 0 = all at once.",
            "Leave it out or empty to use Vanish Delay below.",
            "Replace block / slave / alt: <modid:block>[@meta] that the lock blocks, keyslaves and alt keyslaves turn into when they vanish,",
            "e.g. minecraft:stained_glass@14. Leave them out or empty for minecraft:air.",
            "Locks and keys with a LockId that is not listed here still work, they just use default names and no lore.",
            "Examples:",
            "    /give @p eaglemixins:lock_key 1 0 {LockId:\"brutal_arachnid\"}",
            "    /give @p eaglemixins:locked_block 1 0 {BlockEntityTag:{LockId:\"brutal_arachnid\"}}",
            "    loot table function: {\"function\": \"set_nbt\", \"tag\": \"{LockId:\\\"brutal_arachnid\\\"}\"}",
            "In creative, right-clicking a lock that has no LockId with a key gives the lock that key's LockId."
    })
    @Config.Name("Locks")
    public String[] locks = {
            "brutal_arachnid | Arachnid | #9d9684 | \u00a77Pulled from the remains of the great spider. | \u00a77Thick webbing is woven through the keyhole.\\n\u00a77Only one who has faced the brood may pass. | 0.5 | minecraft:air | minecraft:air | lycanitesmobs:ashenstonepolished",
            "brutal_gatekeeper | Gatekeeper | #865ec9 | \u00a77Torn from the storm dragon that guarded the way. | \u00a77Static crackles across the keyhole.\\n\u00a77The Gatekeeper holds the key to what lies beyond. | 0.1 | minecraft:air | minecraft:air | quark:crystal@8",
            "brutal_sarevok | Sarevok's | #b7812c | \u00a77Taken from Sarevok's fallen body. | \u00a77Deep sword gouges scar the stone around the keyhole.\\n\u00a77The horned one does not give up his key willingly. | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_horror | Parasitic | #6a9834 | \u00a77Cut free from the horror's writhing mass. | \u00a77Fleshy growths pulse around the keyhole.\\n\u00a77Whatever opens this was grown, not forged. | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_necrotic_golem | Necrotic | #31352c | \u00a77Pried from the golem's crumbling core. | \u00a77The stone here is grey and dead, and smells of the grave.\\n\u00a77Its key lies within a heart of rotting stone. | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_bogle | Dread | #e5dd05 | \u00a77Left behind when the bogle finally fell silent. | \u00a77Faint scratch marks circle the keyhole.\\n\u00a77Only one who has silenced the bogle may enter. | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_overseer | Overseer's | #38761d | §7Torn from the overseer as its watch finally ended. | §7Moss and rot creep slowly across the keyhole.\\n§7The Degrading Overseer sees all who approach, and keeps the key. | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_staircase | Staircase | #a0812d | \u00a77Torn from the flying horrors of the staircase, the Banshee or the Avaton| \u00a77A faint smell of dispel lingers around the keyhole\\n\u00a77A Banshee or an Avaton holds its key, chained in the middle of the stairs.\\n\u00a77The Eagle salutes you, good luck and don't fall down! | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_dev | Developer's | #d42300 | \u00a77Wrestled from the developers after a long crunch. | \u00a77Words are scratched beside the keyhole: \"TODO: Phase 2/4\".\\n\u00a77The developers keep the key. Go and ask them nicely. | 0.5 | minecraft:air | minecraft:air | minecraft:air",
            "brutal_shivaxi | Shivaxi's | #00c8d4 | \u00a77Taken from Shivaxi, altered by the Eagle. | \u00a77A blinding light seeps through the keyhole.\\n\u00a77This is where the new world begins. | 0.5 | minecraft:air | minecraft:air | minecraft:air"
    };

    @Config.Comment({
            "Hardness of lock blocks and keyslaves. -1 = unbreakable (like bedrock), 50 = obsidian.",
            "They never drop themselves when broken."
    })
    @Config.Name("Hardness")
    @Config.RequiresMcRestart
    @Config.RangeDouble(min = -1)
    public float hardness = -1;

    @Config.Comment("If true, opening a lock also opens all locks with the same LockId touching it")
    @Config.Name("Open Connected Locks")
    public boolean openConnected = true;

    @Config.Comment("Max amount of blocks (locks and keyslaves) opened by one key")
    @Config.Name("Max Connected Locks")
    @Config.RangeInt(min = 1, max = 4096)
    public int maxConnected = 64;

    @Config.Comment({
            "Ticks between each block vanishing when a lock is opened (20 ticks = 1 second). 0 = all at once.",
            "Used by locks that don't set their own vanish delay in Locks"
    })
    @Config.Name("Vanish Delay")
    @Config.RangeInt(min = 0, max = 200)
    public int vanishDelay = 10;

    @Config.Comment("If true, keys are not used up in creative mode")
    @Config.Name("Keep Key In Creative")
    public boolean keepKeyInCreative = true;

    @Config.Comment({
            "If true, dropped keys glow (the spectral arrow outline, visible through walls).",
            "With Key Outline Color (Vanilla) in the mixin toggles the outline uses the key color from Locks."
    })
    @Config.Name("Glowing Keys")
    public boolean glowingKeys = true;

    private Map<String, LockType> lockTypes = null;

    @Nullable
    public LockType getLockType(String id) {
        return getLockTypeMap().get(id);
    }

    public Collection<LockType> getLockTypes() {
        return getLockTypeMap().values();
    }

    private Map<String, LockType> getLockTypeMap() {
        if (lockTypes == null) {
            lockTypes = new LinkedHashMap<>();
            for (String entry : locks) {
                String[] parts = entry.split("\\|", -1);
                String id = parts[0].trim();
                if (id.isEmpty()) continue;
                String name = parts.length > 1 ? parts[1].trim() : "";
                int color = 0xFFFFFF;
                if (parts.length > 2 && !parts[2].trim().isEmpty()) {
                    try {
                        color = Integer.parseInt(parts[2].trim().replace("#", ""), 16);
                    } catch (NumberFormatException ex) {
                        EagleMixins.LOGGER.error("Invalid key color \"{}\" for lock \"{}\" in Locked Blocks config", parts[2].trim(), id);
                    }
                }
                List<String> keyLore = parts.length > 3 ? lore(parts[3]) : Collections.emptyList();
                List<String> lockLore = parts.length > 4 ? lore(parts[4]) : Collections.emptyList();
                float vanishDelay = -1;
                if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                    try {
                        vanishDelay = Math.max(0, Float.parseFloat(parts[5].trim()));
                    } catch (NumberFormatException ex) {
                        EagleMixins.LOGGER.error("Invalid vanish delay \"{}\" for lock \"{}\" in Locked Blocks config", parts[5].trim(), id);
                    }
                }
                IBlockState replaceBlock = parts.length > 6 ? replacement(parts[6], id) : Blocks.AIR.getDefaultState();
                IBlockState replaceSlave = parts.length > 7 ? replacement(parts[7], id) : Blocks.AIR.getDefaultState();
                IBlockState replaceAlt = parts.length > 8 ? replacement(parts[8], id) : Blocks.AIR.getDefaultState();
                lockTypes.put(id, new LockType(id, name, color, keyLore, lockLore, vanishDelay, replaceBlock, replaceSlave, replaceAlt));
            }
        }
        return lockTypes;
    }

    private static List<String> lore(String text) {
        text = text.trim();
        return text.isEmpty() ? Collections.emptyList() : Arrays.asList(text.split("\\\\n"));
    }

    @SuppressWarnings("deprecation")
    private static IBlockState replacement(String text, String id) {
        text = text.trim();
        if (text.isEmpty()) return Blocks.AIR.getDefaultState();
        int meta = 0;
        int at = text.indexOf('@');
        if (at >= 0) {
            try {
                meta = Integer.parseInt(text.substring(at + 1).trim());
            } catch (NumberFormatException ex) {
                EagleMixins.LOGGER.error("Invalid meta in replacement block \"{}\" for lock \"{}\" in Locked Blocks config", text, id);
            }
            text = text.substring(0, at).trim();
        }
        ResourceLocation name = new ResourceLocation(text);
        Block block = ForgeRegistries.BLOCKS.containsKey(name) ? ForgeRegistries.BLOCKS.getValue(name) : null;
        if (block == null) {
            EagleMixins.LOGGER.error("Unknown replacement block \"{}\" for lock \"{}\" in Locked Blocks config, using air", text, id);
            return Blocks.AIR.getDefaultState();
        }
        return block.getStateFromMeta(meta);
    }

    /** What a lock block or keyslave of this lock type turns into when it vanishes. */
    public IBlockState getReplacement(String lockId, Block block) {
        LockType type = getLockType(lockId);
        if (type == null) return Blocks.AIR.getDefaultState();
        if (block instanceof BlockLockSlave) return ((BlockLockSlave) block).alt ? type.replaceAlt : type.replaceSlave;
        return type.replaceBlock;
    }

    /** Ticks between each block of this lock vanishing, can be fractional (several blocks per tick). */
    public double getVanishDelayTicks(String lockId) {
        LockType type = getLockType(lockId);
        return type != null && type.vanishDelay >= 0 ? type.vanishDelay * 20.0D : vanishDelay;
    }

    public void reset() {
        lockTypes = null;
    }

    public static class LockType {
        public final String id;
        public final String name; // empty = derived from the id
        public final int color;
        public final List<String> keyLore;
        public final List<String> lockLore;
        public final float vanishDelay; // seconds, negative = global Vanish Delay
        public final IBlockState replaceBlock;
        public final IBlockState replaceSlave;
        public final IBlockState replaceAlt;

        public LockType(String id, String name, int color, List<String> keyLore, List<String> lockLore, float vanishDelay,
                        IBlockState replaceBlock, IBlockState replaceSlave, IBlockState replaceAlt) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.keyLore = keyLore;
            this.lockLore = lockLore;
            this.vanishDelay = vanishDelay;
            this.replaceBlock = replaceBlock;
            this.replaceSlave = replaceSlave;
            this.replaceAlt = replaceAlt;
        }
    }
}
