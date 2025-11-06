package net.spit365.lulasmod.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModItems;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Optional;

public class NeedleSwordEntityRenderer extends EntityRenderer<NeedleSwordEntity, NeedleSwordEntityRenderer.NeedleSwordRenderState> {

    private static @NotNull Identifier getTexture() {
        return Identifier.ofVanilla("textures/block/redstone_dust_line" + Random.create().nextBetween(0, 1) + ".png");
    }

    private final ItemRenderer itemRenderer;

    public NeedleSwordEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public NeedleSwordRenderState createRenderState() {
        return new NeedleSwordRenderState();
    }

    @Override
    public void render(NeedleSwordRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-state.pitch + 75));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));

        itemRenderer.renderItem(
            state.needleSword,
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            light,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumers,
            state.world,
            0
        );
        matrices.pop();
        matrices.push();
        if (state.ownerPos.isPresent() && state.shouldDisplayString)
            renderLeash(matrices, vertexConsumers, state.ownerPos.get().subtract(new Vec3d(state.x, state.y, state.z)), light);
        matrices.pop();
    }


    private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d relativePos, int light) {
        double x = relativePos.x;
        double z = relativePos.z;
        double normalizingFactorZPlane = org.joml.Math.invsqrt(x * x + z * z) * 0.025d;
        double XOffset = x * normalizingFactorZPlane;
        double ZOffset = z * normalizingFactorZPlane;
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(Identifier.ofVanilla("textures/entity/beacon_beam.png"), true));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        for (double l = 0; l <= 1; l += 1 / 24d) {
            renderLeashSegment(vertexConsumer, matrix4f, relativePos.multiply(l), XOffset, 0.05d, ZOffset, light);
            renderLeashSegment(vertexConsumer, matrix4f, relativePos.multiply(1 - l), XOffset, 0d, ZOffset, light);
        }
        matrices.pop();
    }

    private static void renderLeashSegment(VertexConsumer vertexConsumer, Matrix4f matrix, Vec3d interpolatedRelativePos, double XOffset, double YOffset, double ZOffset, int light) {
        vertexConsumer
            .vertex(matrix, (float) (interpolatedRelativePos.x - ZOffset), (float) (interpolatedRelativePos.y + YOffset), (float) (interpolatedRelativePos.z + XOffset))
            .normal(0, 0,0)
            .texture(4f, 0f)
            .overlay(OverlayTexture.DEFAULT_UV)
            .color(1, 1, 1, 1f)
            .light(light);
        vertexConsumer
            .vertex(matrix, (float) (interpolatedRelativePos.x + ZOffset), (float) (interpolatedRelativePos.y + 0.05f - YOffset), (float) (interpolatedRelativePos.z - XOffset))
            .normal(0, 0,0)
            .texture(0f, 0f)
            .overlay(OverlayTexture.DEFAULT_UV)
            .color(1, 1, 1, 1f)
            .light(light);
    }

    @Override
    public void updateRenderState(NeedleSwordEntity entity, NeedleSwordRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.needleSword = entity.getSword().copyComponentsToNewStack(ModItems.NEEDLE_HEAD, 1);
        state.ownerPos = Optional.ofNullable(entity.getOwner()).map(owner -> owner.getLeashPos(tickProgress));
        state.world = entity.getWorld();
        state.yaw = entity.getYaw();
        state.pitch = entity.getPitch();
        state.shouldDisplayString = entity.shouldReturn();
    }

    public static class NeedleSwordRenderState extends ProjectileEntityRenderState {
        public ItemStack needleSword;
        public World world;
        public boolean shouldDisplayString;
        public Optional<Vec3d> ownerPos;
    }
}
