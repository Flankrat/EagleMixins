package eaglemixins.mixin.vc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import noppes.vc.items.ItemBasic;
import noppes.vc.items.ItemOrb;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemOrb.class)
public abstract class ItemOrb_DisplayNameMixin extends ItemBasic {

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String metaDataLangKey = "rldregora.variedcommodities:orb:" + stack.getMetadata() + ".name";
        if(I18n.canTranslate(metaDataLangKey)) {
            return I18n.translateToLocal(metaDataLangKey).trim();
        }
        return super.getItemStackDisplayName(stack);
    }
}
