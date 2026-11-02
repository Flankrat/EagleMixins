package eaglemixins.config.folders;

import eaglemixins.EagleMixins;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.Arrays;

public class SRParasiteConfig {

    @Config.Comment("Treat the allowed biomes list as whitelist. Set to false to treat as blacklist")
    @Config.Name("SRParasites allowed biomes is whitelist")
    public boolean biomeListIsWhitelist = true;

    //Parasites will be allowed to spawn via spawners, stay alive and will drop (reduced) loot in these biomes
    @Config.Comment("List of biome IDs to whitelist or blacklist depending on biomeListIsWhitelist.\n" +
            "This list supports the * wildcard, example: biomesoplenty:* would whitelist all biomesoplenty biomes.\n" +
            "Optionally prefix an entry with \"dimensionId@\" to restrict it to a specific dimension,\n" +
            "example: 1@biomesoplenty:steppe only matches biomesoplenty:steppe in dimension 1.\n" +
            "Entries with no dimension prefix (or a \"*@\" prefix) match that biome in any dimension.\n" +
            "The dimension prefix can be combined with the wildcard biome, example: 1@biomesoplenty:*"
    )
    @Config.Name("SRParasites allowed biomes")
    public ArrayList<String> biomeList = new ArrayList<>(Arrays.asList(
            "0@biomesoplenty:steppe",
            "0@openterraingenerator:overworld_abyssal_rift",
            "0@srparasites:biome_parasite",
            "0@openterraingenerator:overworld_lair_of_the_thing",
            "0@openterraingenerator:overworld_nuclear_ruins",
            "0@openterraingenerator:overworld_ruins_of_blight",
            "1@*",
            "3@*",
            "-1@*",
            "111@*"
    ));

    @Config.Comment({
            "EagleMixins modifies parasite loot in the overworld.",
            "Parasites are only allowed to drop loot in certain named biomes (\"SRParasites allowed biomes\")",
            "In those biomes, they never drop parasite (srparasites) items. Each one instead has a chance to drop a replacement item (\"Parasite Replacement Drop Chance\" / \"Parasite Replacement Drop\")",
            "Only parasites with specific custom names are exempt from this modification (\"Parasite full loot-drop enabler\")",
            "If set to false, EagleMixins will instead not modify parasite loot at all."
    })
    @Config.Name("Modify Parasite Loot")
    public boolean modifyLoot = true;

    @Config.Comment({
            "Parasite display names that are allowed to always drop loot (even outside allowed biomes).",
            "This matches the entity's *custom display name*, not its ID.",
            "It's enough if the custom name contains any of these listed strings for it to be always allowed to drop loot.",
            "Requires 'Modify Parasite Loot' to be enabled."
    })
    @Config.Name("Parasite full loot-drop enabler")
    public ArrayList<String> keepLootNames = new ArrayList<>(Arrays.asList(
            "Sentient Horror",
            "Degrading Overseer",
            "Malformed Observer",
            "Shivaxi",
            "Corrupted Carrier",
            "Necrotic Blight"
    ));

    @Config.Comment({
            "Minimum distance in blocks from a Stage IV Beckon at which it spawns new Beckons.",
            "Requires the \"Stage IV Beckon Spread (SRP)\" mixin toggle."
    })
    @Config.Name("Stage IV Beckon Spread min range")
    @Config.RangeInt(min = 0, max = 64)
    public int beckonSpreadMinRange = 3;

    @Config.Comment({
            "Maximum distance in blocks from a Stage IV Beckon at which it spawns new Beckons.",
            "Requires the \"Stage IV Beckon Spread (SRP)\" mixin toggle."
    })
    @Config.Name("Stage IV Beckon Spread max range")
    @Config.RangeInt(min = 0, max = 64)
    public int beckonSpreadMaxRange = 6;

    @Config.Comment({
            "A Stage IV Beckon will not spawn another Beckon if this many Beckons are already within the max range around it (not counting itself).",
            "Requires the \"Stage IV Beckon Spread (SRP)\" mixin toggle."
    })
    @Config.Name("Stage IV Beckon Spread max nearby Beckons")
    @Config.RangeInt(min = 0)
    public int beckonSpreadMaxNearby = 3;

    @Config.Comment({
            "SRP prevents Beckons spawned by a Stage IV Beckon from ever growing. Set to true to allow them to grow.",
            "Requires the \"Stage IV Beckon Spread (SRP)\" mixin toggle."
    })
    @Config.Name("Stage IV Beckon Spread Beckons can grow")
    public boolean beckonSpreadCanGrow = false;

    @Config.Comment("All SRParasites outside of the allowed biomes in the allowed biome whitelist will automatically be killed")
    @Config.Name("Kill Parasites outside alllowed biomes")
    public boolean killEscapedParasites = true;

    @Config.Comment({
            "Failsafe for escaped parasites that heal faster than they get drained.",
            "After this many checks (one every 2.5 seconds) outside the allowed biomes, the parasite is removed outright.",
            "Re-entering an allowed biome resets the count."
    })
    @Config.Name("Escaped Parasite Removal Checks")
    @Config.RangeInt(min = 1)
    public int escapedParasiteRemovalChecks = 4;

    @Config.Comment("Chance per removed parasite item to drop the replacement drop instead. Parasite items are always removed, at 0 nothing drops in their place.")
    @Config.Name("Parasite Replacement Drop Chance")
    public float replacementDropChance = 0.375f;

    @Config.Comment({
            "Dimension IDs in which \"Modify Parasite Loot\" applies",
            "Leave epmty to disable the everywhere."
    })
    @Config.Name("Parasite Drop Replacement Dimensions")
    public int[] replacementDropDimensions = {0};

    @Config.Comment({
            "Item that replaces parasite drops in the overworld (see \"Parasite Replacement Drop Chance\").",
            "Format: modid:item or modid:item metadata (e.g. \"minecraft:dye 15\").",
            "Defaults to Wardlights' Tainted Flesh. Falls back to air (nothing drops) if the item can't be found."
    })
    @Config.Name("Parasite Replacement Drop")
    public String replacementDropItem = "wardlights:tainted_flesh";

    @Config.Comment({
            "Translation key (or plain text) used as display name for the replacement drop.",
            "Leave empty to keep the item's original name.",
            "E.g. use \"eaglemixins.tooltip.corruptedashes\" with biomesoplenty:ash to get the old \"Corrupted Ashes\"."
    })
    @Config.Name("Parasite Replacement Drop Name")
    public String replacementDropName = "";

    public boolean isReplacementDropDimension(int dimensionId) {
        return Arrays.stream(replacementDropDimensions).anyMatch(dim -> dim == dimensionId);
    }

    private ItemStack replacementDrop = null;
    public ItemStack getReplacementDrop() {
        if (replacementDrop == null) {
            Item item = null;
            int metadata = 0;
            String[] split = replacementDropItem.trim().split(" ");
            try {
                item = Item.getByNameOrId(split[0].trim());
                if (split.length > 1)
                    metadata = Integer.parseInt(split[1].trim());
            } catch (Exception exception) {
                EagleMixins.LOGGER.error("Failed parsing parasite replacement drop ({})", replacementDropItem);
            }
            // Unknown ids come back as air from Forge's item registry, or null if parsing failed.
            if (item == null || item == Items.AIR) {
                EagleMixins.LOGGER.error("Parasite replacement drop not found ({}), using air (nothing drops)", replacementDropItem);
                item = Items.AIR;
                metadata = 0;
            }
            replacementDrop = new ItemStack(item, 1, metadata);
            if (!replacementDrop.isEmpty() && !replacementDropName.trim().isEmpty())
                replacementDrop.setTranslatableName(replacementDropName.trim());
        }
        return replacementDrop.copy();
    }

    @Config.Comment({
            "Dimension IDs in which parasites drop the \"Tear Swap Replacement\" instead of the \"Tear Swap Original\".",
            "Leave empty to disable the swap."
    })
    @Config.Name("Tear Swap Dimensions")
    public int[] tearSwapDimensions = {3};

    @Config.Comment({
            "Item dropped by parasites that gets swapped out in the \"Tear Swap Dimensions\".",
            "Format: modid:item or modid:item metadata. Without metadata, any metadata matches."
    })
    @Config.Name("Tear Swap Original")
    public String tearSwapOriginalItem = "contenttweaker:blood_tear";

    @Config.Comment({
            "Item that parasites drop instead of the \"Tear Swap Original\" in the \"Tear Swap Dimensions\". Keeps the stack size.",
            "Format: modid:item or modid:item metadata."
    })
    @Config.Name("Tear Swap Replacement")
    public String tearSwapReplacementItem = "eaglemixins:tainted_tear";

    private Item tearSwapOriginal = null;
    private int tearSwapOriginalMeta = -1;
    private ItemStack tearSwapReplacement = null;

    public boolean isTearSwapDimension(int dimensionId) {
        return Arrays.stream(tearSwapDimensions).anyMatch(dim -> dim == dimensionId);
    }

    public boolean isTearSwapOriginal(ItemStack stack) {
        if (tearSwapOriginal == null) {
            String[] split = tearSwapOriginalItem.trim().split(" ");
            try {
                tearSwapOriginal = Item.getByNameOrId(split[0].trim());
                tearSwapOriginalMeta = split.length > 1 ? Integer.parseInt(split[1].trim()) : -1;
            } catch (Exception exception) {
                EagleMixins.LOGGER.error("Failed parsing tear swap original ({})", tearSwapOriginalItem);
            }
            if (tearSwapOriginal == null || tearSwapOriginal == Items.AIR) {
                EagleMixins.LOGGER.error("Tear swap original not found ({}), tear swap disabled", tearSwapOriginalItem);
                tearSwapOriginal = Items.AIR;
            }
        }
        if (tearSwapOriginal == Items.AIR) return false;
        return stack.getItem() == tearSwapOriginal && (tearSwapOriginalMeta == -1 || stack.getMetadata() == tearSwapOriginalMeta);
    }

    // Returns an empty stack if the replacement item can't be found
    public ItemStack getTearSwapReplacement(int count) {
        if (tearSwapReplacement == null) {
            Item item = null;
            int metadata = 0;
            String[] split = tearSwapReplacementItem.trim().split(" ");
            try {
                item = Item.getByNameOrId(split[0].trim());
                if (split.length > 1)
                    metadata = Integer.parseInt(split[1].trim());
            } catch (Exception exception) {
                EagleMixins.LOGGER.error("Failed parsing tear swap replacement ({})", tearSwapReplacementItem);
            }
            if (item == null || item == Items.AIR) {
                EagleMixins.LOGGER.error("Tear swap replacement not found ({}), original drop is kept", tearSwapReplacementItem);
                item = Items.AIR;
                metadata = 0;
            }
            tearSwapReplacement = new ItemStack(item, 1, metadata);
        }
        ItemStack stack = tearSwapReplacement.copy();
        if (!stack.isEmpty()) stack.setCount(count);
        return stack;
    }

    public void reset() {
        replacementDrop = null;
        tearSwapOriginal = null;
        tearSwapOriginalMeta = -1;
        tearSwapReplacement = null;
    }
}
