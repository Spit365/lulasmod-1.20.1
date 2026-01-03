package net.spit365.lulasmod.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.entity.AmethystShardEntity;

public class AmethystShardEntityRenderer extends EntityRenderer<AmethystShardEntity, ProjectileEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.of(Lulasmod.MOD_ID, "textures/entity/amethyst_shard.png");
    private final ArrowEntityModel model;

    public AmethystShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW));
    }

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }

    public void render(ProjectileEntityRenderState projectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(projectileEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(projectileEntityRenderState.pitch));
        this.model.setAngles(projectileEntityRenderState);
        this.model.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(projectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public void updateRenderState(AmethystShardEntity persistentProjectileEntity, ProjectileEntityRenderState projectileEntityRenderState, float f) {
        super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
        projectileEntityRenderState.pitch = persistentProjectileEntity.getLerpedPitch(f);
        projectileEntityRenderState.yaw = -persistentProjectileEntity.getLerpedYaw(f);
    }
}