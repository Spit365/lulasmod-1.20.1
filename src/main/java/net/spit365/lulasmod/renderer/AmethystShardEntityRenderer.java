package net.spit365.lulasmod.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ArrowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.entity.AmethystShardEntity;

@Environment(EnvType.CLIENT)
public class AmethystShardEntityRenderer extends EntityRenderer<AmethystShardEntity, ArrowRenderState> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "textures/entity/amethyst_shard.png");
    private final ArrowModel model;

    public AmethystShardEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ArrowModel(context.bakeLayer(ModelLayers.ARROW));
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    public void render(ArrowRenderState projectileEntityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        matrixStack.mulPose(Axis.YP.rotationDegrees(projectileEntityRenderState.yRot - 90.0F));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(projectileEntityRenderState.xRot));
        this.model.setupAnim(projectileEntityRenderState);
        this.model.renderToBuffer(matrixStack, vertexConsumerProvider.getBuffer(RenderType.entityCutout(TEXTURE)), i, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        super.render(projectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public void extractRenderState(AmethystShardEntity entity, ArrowRenderState entityRenderState, float f) {
        super.extractRenderState(entity, entityRenderState, f);
        entityRenderState.xRot = entity.getXRot(f);
        entityRenderState.yRot = -entity.getYRot(f);
    }
}