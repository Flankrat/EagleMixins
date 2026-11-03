package eaglemixins.client;

import eaglemixins.config.ForgeConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Draws the parasite carapace as a small bar below the bottom left of the Neat health bar, with an icon on its right.
 * Called from HealthBarRenderer_CarapaceMixin, coordinates are in Neat's plate space.
 */
@SideOnly(Side.CLIENT)
public class CarapaceBarRenderer {
    // Stored on the client entity in ForgeData -> EagleMixinsCarapace, same layout as on the server plus Max and Rate
    private static final String KEY_CARAPACE = "EagleMixinsCarapace";
    private static final String KEY_AMOUNT = "Amount";
    private static final String KEY_TIME = "Time";
    private static final String KEY_MAX = "Max";
    private static final String KEY_RATE = "Rate";

    private static final float ICON_SIZE = 5;
    private static final float BAR_HEIGHT = 2.4F;
    // Extra vertical distance between the bottom edge of Neat's frame and the top edge of the carapace bar
    private static final float BAR_OFFSET_Y = 1.5F;

    private static String cachedIconId = null;
    private static TextureAtlasSprite cachedIcon = null;

    public static void setClientData(Entity entity, float budget, float maxBudget, float recoverPerTick) {
        NBTTagCompound data = getOrCreateCarapaceData(entity);
        data.setFloat(KEY_AMOUNT, budget);
        data.setFloat(KEY_MAX, maxBudget);
        data.setFloat(KEY_RATE, recoverPerTick);
        data.setLong(KEY_TIME, entity.world.getTotalWorldTime());
    }

    public static void render(Entity entity, float partialTicks, float size, int barHeight, float padding) {
        NBTTagCompound data = getCarapaceData(entity);
        if (!data.hasKey(KEY_MAX)) return;
        float max = data.getFloat(KEY_MAX);
        if (max <= 0) return;

        float elapsed = Math.max(0, entity.world.getTotalWorldTime() - data.getLong(KEY_TIME)) + partialTicks;
        float budget = Math.min(max, data.getFloat(KEY_AMOUNT) + elapsed * data.getFloat(KEY_RATE));
        float fraction = MathHelper.clamp(budget / max, 0, 1);

        // Bar is left aligned with the health bar, its length shows the max carapace (capped at full width),
        // icon sits right of it, vertically centered on the bar. Full width is half the health bar.
        float fullWidth = size;
        float widthFraction = Math.min(1, max / Math.max(1, ForgeConfigHandler.srparasites.carapace.barFullWidthAmount));
        float barLeft = -size;
        float barRight = barLeft + fullWidth * widthFraction;
        // Top edge at the bottom edge of Neat's background frame, shifted down by BAR_OFFSET_Y
        float barTop = barHeight + padding + BAR_OFFSET_Y;
        float fillRight = barLeft + (barRight - barLeft) * fraction;
        float iconLeft = barRight + 1;
        float iconTop = barTop + (BAR_HEIGHT - ICON_SIZE) / 2F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Neat has textures disabled at this point
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        quad(buffer, barLeft, barTop, barRight, barTop + BAR_HEIGHT, 60, 35, 25, 160);
        quad(buffer, barLeft, barTop, fillRight, barTop + BAR_HEIGHT, 150, 60, 35, 200);
        tessellator.draw();

        TextureAtlasSprite icon = getIcon();
        if (icon == null) return;
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(iconLeft, iconTop + ICON_SIZE, 0).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        buffer.pos(iconLeft + ICON_SIZE, iconTop + ICON_SIZE, 0).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        buffer.pos(iconLeft + ICON_SIZE, iconTop, 0).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        buffer.pos(iconLeft, iconTop, 0).tex(icon.getMinU(), icon.getMinV()).endVertex();
        tessellator.draw();
        GlStateManager.disableTexture2D();
    }

    private static void quad(BufferBuilder buffer, float x1, float y1, float x2, float y2, int r, int g, int b, int a) {
        buffer.pos(x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.pos(x1, y2, 0).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, 0).color(r, g, b, a).endVertex();
        buffer.pos(x2, y1, 0).color(r, g, b, a).endVertex();
    }

    private static TextureAtlasSprite getIcon() {
        String iconId = ForgeConfigHandler.srparasites.carapace.barIcon.trim();
        if (!iconId.equals(cachedIconId)) {
            cachedIconId = iconId;
            cachedIcon = null;
            String[] split = iconId.split(" ");
            Item item = Item.getByNameOrId(split[0]);
            if (item != null) {
                int meta = 0;
                try {
                    if (split.length > 1) meta = Integer.parseInt(split[1]);
                } catch (NumberFormatException ignored) {
                }
                cachedIcon = Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                        .getItemModel(new ItemStack(item, 1, meta)).getParticleTexture();
            }
        }
        return cachedIcon;
    }

    // ForgeData -> EagleMixinsCarapace, created if missing. Use for writing.
    private static NBTTagCompound getOrCreateCarapaceData(Entity entity) {
        NBTTagCompound forgeData = entity.getEntityData();
        if (!forgeData.hasKey(KEY_CARAPACE, Constants.NBT.TAG_COMPOUND)) forgeData.setTag(KEY_CARAPACE, new NBTTagCompound());
        return forgeData.getCompoundTag(KEY_CARAPACE);
    }

    // ForgeData -> EagleMixinsCarapace, or an empty detached compound if missing. Use for reading.
    private static NBTTagCompound getCarapaceData(Entity entity) {
        return entity.getEntityData().getCompoundTag(KEY_CARAPACE);
    }
}
