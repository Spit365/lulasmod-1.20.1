package net.spit365.lulasmod.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.ArrowModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.entity.AmethystShardEntity;

@Environment(EnvType.CLIENT)
public class AmethystShardEntityRenderer extends EntityRenderer<AmethystShardEntity, ArrowRenderState> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "textures/entity/amethyst_shard.png");
    private final ArrowModel model;

    public AmethystShardEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ArrowModel(context.bakeLayer(ModelLayers.ARROW));
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    @Override
    public void submit(ArrowRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        this.model.setupAnim(state);
        submitNodeCollector.submitModel(
            this.model,
            state,
            poseStack,
            TEXTURE,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0,
            null
        );
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public void extractRenderState(AmethystShardEntity entity, ArrowRenderState entityRenderState, float f) {
        super.extractRenderState(entity, entityRenderState, f);
        entityRenderState.xRot = entity.getXRot(f);
        entityRenderState.yRot = -entity.getYRot(f);
    }
}