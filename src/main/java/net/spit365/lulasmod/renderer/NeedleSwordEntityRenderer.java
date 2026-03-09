package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModItems;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class NeedleSwordEntityRenderer extends EntityRenderer<NeedleSwordEntity> {

    private final ItemRenderer itemRenderer;

    public NeedleSwordEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public Identifier getTexture(NeedleSwordEntity entity) {
        return Identifier.ofVanilla("textures/entity/beacon_beam.png");
    }

    @Override
    public void render(NeedleSwordEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        float pitch = entity.getLerpTargetPitch();
        float entityYaw = entity.getLerpTargetYaw();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-pitch + 75));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entityYaw));

        itemRenderer.renderItem(
            entity.getSword().copyComponentsToNewStack(ModItems.NEEDLE_HEAD, 1),
            ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
            light,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumers,
            entity.getWorld(),
            0
        );
        matrices.pop();

        if (entity.getOwner() != null && entity.shouldReturn()) {
            Vec3d ownerPos = entity.getOwner().getLeashPos(tickDelta);
            Vec3d relativePos = ownerPos.subtract(entity.getLerpedPos(tickDelta));

            matrices.push();
            renderLeash(matrices, vertexConsumers, relativePos, light);
            matrices.pop();
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d relativePos, int light) {
        double x = relativePos.x;
        double z = relativePos.z;
        double normalizingFactorZPlane = org.joml.Math.invsqrt(x * x + z * z) * 0.025d;
        double XOffset = x * normalizingFactorZPlane;
        double ZOffset = z * normalizingFactorZPlane;

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(Identifier.ofVanilla("textures/entity/beacon_beam.png"), true));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        for (double l = 0; l <= 1; l += 1 / 24d) {
            renderLeashSegment(vertexConsumer, matrix4f, relativePos.multiply(l), XOffset, 0.05d, ZOffset, light);
            renderLeashSegment(vertexConsumer, matrix4f, relativePos.multiply(1 - l), XOffset, 0d, ZOffset, light);
        }
    }

    private static void renderLeashSegment(VertexConsumer vertexConsumer, Matrix4f matrix, Vec3d pos, double XOffset, double YOffset, double ZOffset, int light) {
        vertexConsumer
            .vertex(matrix, (float) (pos.x - ZOffset), (float) (pos.y + YOffset), (float) (pos.z + XOffset))
            .color(255, 255, 255, 255)
            .texture(4f, 0f)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(0, 1, 0);

        vertexConsumer
            .vertex(matrix, (float) (pos.x + ZOffset), (float) (pos.y + 0.05f - YOffset), (float) (pos.z - XOffset))
            .color(255, 255, 255, 255)
            .texture(0f, 0f)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(0, 1, 0);
    }
}