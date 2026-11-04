package eaglemixins.client.render;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.client.races.dragon.RaceDragonHorns;
import xzeroair.trinkets.client.races.dragon.RaceDragonWings;
import xzeroair.trinkets.client.races.generic.RaceHorns;
import xzeroair.trinkets.client.races.generic.RaceHornsInverted;

/**
 * Draws Trinkets and Baubles (xat, 0.33+) dragon wings and horns on any biped entity.
 * Only call this when xat is loaded, it references xat classes directly.
 */
@SideOnly(Side.CLIENT)
public final class XatDragonRenderCompat {

    public static void renderWings(EntityLivingBase entity, RenderLivingBase<?> renderer, boolean slim, float partialTicks, float scale, int primary, int secondary) {
        RaceDragonWings.INSTANCE.render(entity, renderer, false, slim, partialTicks, scale, 0, primary, secondary, 0);
    }

    /** style: INVERTED, HORNS or DRAGON */
    public static void renderHorns(EntityLivingBase entity, RenderLivingBase<?> renderer, boolean slim, float partialTicks, float scale, String style, int color) {
        switch (style) {
            case "INVERTED": RaceHornsInverted.INSTANCE.render(entity, renderer, false, slim, partialTicks, scale, 0, color); break;
            case "HORNS": RaceHorns.INSTANCE.render(entity, renderer, false, slim, partialTicks, scale, 1, color); break;
            case "DRAGON": RaceDragonHorns.INSTANCE.render(entity, renderer, false, slim, partialTicks, scale, 2, color); break;
            default: break;
        }
    }
}
