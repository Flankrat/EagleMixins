package eaglemixins.client.render;

import eaglemixins.compat.ModLoadedUtil;
import eaglemixins.config.folders.PlayerBossConfig.BossModel;
import eaglemixins.entity.EntityPlayerBoss;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Player model with the boss' skin (classic or slim arms), armor, held items, and xat dragon wings / horns,
 * or a vanilla skeleton / zombie model with that mob's texture, depending on the boss' Model.
 */
@SideOnly(Side.CLIENT)
public class RenderPlayerBoss extends RenderLivingBase<EntityPlayerBoss> {

    private static final Map<String, ResourceLocation> SKINS = new HashMap<>();
    private static final Map<BossModel, ResourceLocation> MOB_TEXTURES = new EnumMap<>(BossModel.class);
    private static final ResourceLocation STRAY_OVERLAY = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");

    static {
        MOB_TEXTURES.put(BossModel.SKELETON, new ResourceLocation("textures/entity/skeleton/skeleton.png"));
        MOB_TEXTURES.put(BossModel.WITHER_SKELETON, new ResourceLocation("textures/entity/skeleton/wither_skeleton.png"));
        MOB_TEXTURES.put(BossModel.STRAY, new ResourceLocation("textures/entity/skeleton/stray.png"));
        MOB_TEXTURES.put(BossModel.ZOMBIE, new ResourceLocation("textures/entity/zombie/zombie.png"));
        MOB_TEXTURES.put(BossModel.HUSK, new ResourceLocation("textures/entity/zombie/husk.png"));
    }

    private final ModelPlayer classicModel = new ModelPlayer(0.0F, false);
    private final ModelPlayer slimModel = new ModelPlayer(0.0F, true);
    private final ModelBossSkeleton skeletonModel = new ModelBossSkeleton(0.0F, false);
    private final ModelZombie zombieModel = new ModelZombie(0.0F, false);

    public RenderPlayerBoss(RenderManager manager) {
        super(manager, new ModelPlayer(0.0F, false), 0.5F);
        mainModel = classicModel;

        // Armor and head layers follow the model they are drawn on, like the vanilla renderers of these mobs
        LayerBipedArmor playerArmor = new LayerBipedArmor(this);
        PerModelLayer armor = new PerModelLayer();
        armor.layers.put(classicModel, playerArmor);
        armor.layers.put(slimModel, playerArmor);
        armor.layers.put(skeletonModel, new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                modelLeggings = new ModelBossSkeleton(0.5F, true);
                modelArmor = new ModelBossSkeleton(1.0F, true);
            }
        });
        armor.layers.put(zombieModel, new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                modelLeggings = new ModelZombie(0.5F, true);
                modelArmor = new ModelZombie(1.0F, true);
            }
        });
        PerModelLayer head = new PerModelLayer();
        for (ModelBiped model : new ModelBiped[]{classicModel, slimModel, skeletonModel, zombieModel}) {
            head.layers.put(model, new LayerCustomHead(model.bipedHead));
        }

        addLayer(armor);
        addLayer(new LayerHeldItem(this));
        addLayer(new LayerArrow(this));
        addLayer(head);
        addLayer(new LayerStrayClothing());
        if (ModLoadedUtil.xat.isLoaded()) addLayer(new LayerWings()); // LayerWings touches xat classes
    }

    private ModelBiped getModel(EntityPlayerBoss entity) {
        switch (entity.getBossModel()) {
            case SKELETON:
            case WITHER_SKELETON:
            case STRAY:
                return skeletonModel;
            case ZOMBIE:
            case HUSK:
                return zombieModel;
            default:
                return entity.getRenderData().getBoolean("Slim") ? slimModel : classicModel;
        }
    }

    @Override
    public void doRender(EntityPlayerBoss entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ModelBiped model = getModel(entity);
        mainModel = model;

        ModelBiped.ArmPose mainPose = entity.getHeldItemMainhand().isEmpty() ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;
        ModelBiped.ArmPose offPose = entity.getHeldItemOffhand().isEmpty() ? ModelBiped.ArmPose.EMPTY : ModelBiped.ArmPose.ITEM;
        ItemStack main = entity.getHeldItemMainhand();
        if (entity.isSwingingArms() && main.getItem() instanceof ItemBow) mainPose = ModelBiped.ArmPose.BOW_AND_ARROW;
        if (entity.getPrimaryHand() == EnumHandSide.RIGHT) {
            model.rightArmPose = mainPose;
            model.leftArmPose = offPose;
        } else {
            model.rightArmPose = offPose;
            model.leftArmPose = mainPose;
        }
        model.isSneak = entity.isSneaking();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /** Name tag with the subtitle as a second line under it, both centered like vanilla labels. */
    @Override
    protected void renderEntityName(EntityPlayerBoss entity, double x, double y, double z, String name, double distanceSq) {
        String subtitle = entity.getRenderData().getString("Subtitle");
        if (subtitle.isEmpty()) {
            super.renderEntityName(entity, x, y, z, name, distanceSq);
            return;
        }
        renderLivingLabel(entity, name, x, y + 0.27D, z, 64); // one label line (10px at 0.027 scale) up, so the subtitle sits where the name normally is
        renderLivingLabel(entity, subtitle, x, y, z, 64);
    }

    @Override
    protected void preRenderCallback(EntityPlayerBoss entity, float partialTicks) {
        float scale;
        switch (entity.getBossModel()) {
            case PLAYER: scale = 0.9375F; break; // players render at 0.9375
            case WITHER_SKELETON: scale = 1.2F; break;
            case HUSK: scale = 1.0625F; break;
            default: scale = 1.0F; break;
        }
        scale *= entity.getRenderScale();
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPlayerBoss entity) {
        String skin = entity.getRenderData().getString("Skin");
        if (skin.isEmpty()) {
            ResourceLocation mobTexture = MOB_TEXTURES.get(entity.getBossModel());
            return mobTexture != null ? mobTexture : DefaultPlayerSkin.getDefaultSkinLegacy();
        }
        return SKINS.computeIfAbsent(skin, s -> s.contains(":") ? new ResourceLocation(s) : new ResourceLocation("eaglemixins", "textures/entity/player_boss/" + s + ".png"));
    }

    /** Draws the layer that belongs to the model currently in use */
    private class PerModelLayer implements LayerRenderer<EntityPlayerBoss> {
        private final Map<ModelBiped, LayerRenderer<EntityLivingBase>> layers = new IdentityHashMap<>();

        @Override
        public void doRenderLayer(EntityPlayerBoss entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            LayerRenderer<EntityLivingBase> layer = layers.get(mainModel);
            if (layer != null) layer.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }

    /** Vanilla's LayerStrayClothing only takes EntityStray */
    private class LayerStrayClothing implements LayerRenderer<EntityPlayerBoss> {
        private final ModelBossSkeleton clothesModel = new ModelBossSkeleton(0.25F, true);

        @Override
        public void doRenderLayer(EntityPlayerBoss entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.getBossModel() != BossModel.STRAY) return;
            clothesModel.setModelAttributes(getMainModel());
            clothesModel.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(STRAY_OVERLAY);
            clothesModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

        @Override
        public boolean shouldCombineTextures() {
            return true;
        }
    }

    private class LayerWings implements LayerRenderer<EntityPlayerBoss> {
        @Override
        public void doRenderLayer(EntityPlayerBoss entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            if (entity.getBossModel() != BossModel.PLAYER) return; // xat's renderers are made for the player model
            NBTTagCompound render = entity.getRenderData();
            boolean slim = render.getBoolean("Slim");
            if (render.getBoolean("Wings")) {
                XatDragonRenderCompat.renderWings(entity, RenderPlayerBoss.this, slim, partialTicks, scale, render.getInteger("WingPrimary"), render.getInteger("WingSecondary"));
            }
            XatDragonRenderCompat.renderHorns(entity, RenderPlayerBoss.this, slim, partialTicks, scale, render.getString("Horns"), render.getInteger("HornColor"));
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
