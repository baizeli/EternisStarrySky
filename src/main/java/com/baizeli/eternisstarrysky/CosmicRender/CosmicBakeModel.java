package com.baizeli.eternisstarrysky.CosmicRender;

import com.baizeli.eternisstarrysky.EternisStarrySky;
import com.baizeli.eternisstarrysky.Items.AvaritiaSword;
import com.baizeli.eternisstarrysky.Items.ModItems;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class CosmicBakeModel implements BakedModel {
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private final List<ResourceLocation> maskSprite;
    private final BakedModel wrapped;
    private final ItemOverrides overrideList;
    private ModelState parentState;
    private LivingEntity entity;
    private ClientLevel world;

    public CosmicBakeModel(final BakedModel wrapped, final List<ResourceLocation> maskSprite) {
        this.overrideList = new ItemOverrides() {
            @Override
            public BakedModel resolve(final @NotNull BakedModel originalModel, final @NotNull ItemStack stack, final ClientLevel world, final LivingEntity entity, final int seed) {
                CosmicBakeModel.this.entity = entity;
                CosmicBakeModel.this.world = ((world == null) ? ((entity == null) ? null : ((ClientLevel) entity.level())) : null);
                return CosmicBakeModel.this.wrapped.getOverrides().resolve(originalModel, stack, world, entity, seed);
            }
        };
        this.wrapped = wrapped;
        this.parentState = TransformUtils.stateFromItemTransforms(wrapped.getTransforms());
        this.maskSprite = maskSprite;
    }

    private static final Map<Item, EffectConfig> COSMIC_EFFECTS = new HashMap<>();

    public void renderItem(ItemStack stack, ItemDisplayContext transformType, PoseStack pStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        // 渲染基础模型
        BakedModel model = this.wrapped.getOverrides().resolve(this.wrapped, stack, this.world, this.entity, 0);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        assert model != null;

        for (BakedModel bakedModel : model.getRenderPasses(stack, true)) {
            for (RenderType rendertype : bakedModel.getRenderTypes(stack, true)) {
                itemRenderer.renderModelLists(bakedModel, stack, packedLight, packedOverlay, pStack, buffers.getBuffer(rendertype));
            }
        }

        // 检查并渲染特效
        EffectConfig config = COSMIC_EFFECTS.get(stack.getItem());
        if (config != null) {
           // int shadersType = stack.getItem() instanceof AvaritiaSword ? 1 : 2;
            renderCosmicEffect(stack, transformType, pStack, buffers, packedLight, packedOverlay, config.type, config.scale, config.v4f,2);
        }
    }

    private static class EffectConfig {
        final int type;
        final float scale;
        final Vector4f v4f;

        EffectConfig(int type, float scale, Vector4f v4f) {
            this.type = type;
            this.scale = scale;
            this.v4f = v4f;
        }
    }

    private void renderCosmicEffect(ItemStack stack, ItemDisplayContext transformType, PoseStack pStack, MultiBufferSource buffers, int packedLight, int packedOverlay, int useType, float starScale, Vector4f vec4,int shaderType)
    {
        if (buffers instanceof MultiBufferSource.BufferSource bs) {
            bs.endBatch();
        }

        Minecraft mc = Minecraft.getInstance();
        float yaw = 0.0F;
        float pitch = 0.0F;
        float scale = starScale;

        // 根据渲染环境调整参数
        if (AvaritiaShaders.inventoryRender || transformType == ItemDisplayContext.GUI)
        {
            scale = 100.0F;
        } else {
            assert mc.player != null;
            yaw = (float) (mc.player.getYRot() * 2.0F * Math.PI / 360.0);
            pitch = -(float) (mc.player.getXRot() * 2.0F * Math.PI / 360.0);
        }

        // 设置着色器参数
        AvaritiaShaders.cosmicTime.set((System.currentTimeMillis() - AvaritiaShaders.renderTime) / 2000.0F);
        AvaritiaShaders.cosmicYaw.set(yaw);
        AvaritiaShaders.cosmicPitch.set(pitch);
        AvaritiaShaders.cosmicExternalScale.set(scale);
        AvaritiaShaders.cosmicOpacity.set(1.0F);
        AvaritiaShaders.useType.set(useType);
        int ctime = 0;
        if (Minecraft.getInstance().level != null)
            ctime = Math.toIntExact(((Minecraft.getInstance().level.getDayTime() % 24000) + 6000) % 24000);
        AvaritiaShaders.currentTime.set(ctime);

        // 准备纹理UV
        for (int i = 0; i < 10; ++i)
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.fromNamespaceAndPath(EternisStarrySky.MOD_ID, "item/misc/cosmic_" + i));
            AvaritiaShaders.COSMIC_UVS[i * 4] = sprite.getU0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }
        AvaritiaShaders.cosmicUVs.set(AvaritiaShaders.COSMIC_UVS);

        // 创建顶点消费者
        VertexConsumer cons;
       // else {
            cons = buffers.getBuffer(AvaritiaShaders.COSMIC_RENDER_TYPE);
        //}
        List<TextureAtlasSprite> atlasSprite = new ArrayList<>();

        // 加载遮罩纹理
        for (ResourceLocation res : maskSprite)
        {
            atlasSprite.add(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(res));
        }

        // 生成渲染四边形
        LinkedList<BakedQuad> quads = new LinkedList<>();
        for (TextureAtlasSprite sprite : atlasSprite) {
            List<BlockElement> unbaked = ITEM_MODEL_GENERATOR.processFrames(
                    atlasSprite.indexOf(sprite),
                    "layer" + atlasSprite.indexOf(sprite),
                    sprite.contents()
            );

            for (BlockElement element : unbaked) {
                for (Map.Entry<Direction, BlockElementFace> entry : element.faces.entrySet()) {
                    quads.add(FACE_BAKERY.bakeQuad(
                            element.from,
                            element.to,
                            entry.getValue(),
                            sprite,
                            entry.getKey(),
                            new PerspectiveModelState(ImmutableMap.of()),
                            element.rotation,
                            element.shade,
                            ResourceLocation.fromNamespaceAndPath(EternisStarrySky.MOD_ID, "dynamic")
                    ));
                }
            }
        }

        // 渲染星空效果
        mc.getItemRenderer().renderQuadList(pStack, cons, quads, stack, packedLight, packedOverlay);
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public @NotNull BakedModel applyTransform(@NotNull ItemDisplayContext context, @NotNull PoseStack pStack, boolean leftFlip) {
        PerspectiveModelState modelState = (PerspectiveModelState) this.parentState;
        if (modelState != null) {
            Transformation transform = ((PerspectiveModelState) this.parentState).getTransform(context);
            Vector3f trans = transform.getTranslation();
            Vector3f scale = transform.getScale();
            pStack.translate(trans.x(), trans.y(), trans.z());
            pStack.mulPose(transform.getLeftRotation());
            pStack.scale(scale.x(), scale.y(), scale.z());
            pStack.mulPose(transform.getRightRotation());
            if (leftFlip) {
                pStack.mulPose(Axis.YN.rotationDegrees(180.0f));
            }
            return this;
        }
        return BakedModel.super.applyTransform(context, pStack, leftFlip);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(BlockState state, Direction side, @NotNull RandomSource rand) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.wrapped.getParticleIcon();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return this.wrapped.getParticleIcon(data);
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return this.overrideList;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.wrapped.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.wrapped.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.wrapped.usesBlockLight();
    }

    static
    {
            COSMIC_EFFECTS.put(ModItems.INFINITY_SWORD.get(), new EffectConfig(0, 0.6F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.INFINITY_SWORD_TRUE.get(), new EffectConfig(2, 0.6F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.INFINITY_ETERNAL_HELMET.get(), new EffectConfig(0, 0.5F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.INFINITY_ETERNAL_CHESTPLATE.get(), new EffectConfig(0, 0.5F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.INFINITY_ETERNAL_LEGGINGS.get(), new EffectConfig(0, 0.5F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.INFINITY_ETERNAL_BOOTS.get(), new EffectConfig(0, 0.5F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));

            COSMIC_EFFECTS.put(ModItems.PRIMOGEM.get(), new EffectConfig(2, 0.6F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.PURPLEITE_GALAXY_INGOT.get(), new EffectConfig(0, 0.6F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.ETERNIS_APPLE.get(), new EffectConfig(0, 0.5F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.PEACH.get(), new EffectConfig(1, 0.5F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
            COSMIC_EFFECTS.put(ModItems.AVARITIA_SWORD.get(), new EffectConfig(3, 0.6F, new Vector4f(0.0F, 0.02F, 0.03F, 1F)));
    }
}