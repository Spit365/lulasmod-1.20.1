package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModItems;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class NeedleSwordEntityRenderer extends EntityRenderer<NeedleSwordEntity, NeedleSwordEntityRenderer.NeedleSwordRenderState> {

    private final ItemRenderer itemRenderer;

    public NeedleSwordEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public NeedleSwordRenderState createRenderState() {
        return new NeedleSwordRenderState();
    }

    @Override
    public void render(NeedleSwordRenderState state, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.pushPose();
        matrices.mulPose(Axis.XP.rotationDegrees(-state.xRot + 75));
        matrices.mulPose(Axis.YP.rotationDegrees(-state.yRot));

        itemRenderer.renderStatic(
            state.needleSword,
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            light,
            OverlayTexture.NO_OVERLAY,
            matrices,
            vertexConsumers,
            state.world,
            0
        );
        matrices.popPose();
        matrices.pushPose();
        if (state.ownerPos.isPresent() && state.shouldDisplayString)
            renderLeash(matrices, vertexConsumers, state.ownerPos.get().subtract(new Vec3(state.x, state.y, state.z)), light);
        matrices.popPose();
    }


    private static void renderLeash(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 relativePos, int light) {
        double x = relativePos.x;
        double z = relativePos.z;
        double normalizingFactorZPlane = org.joml.Math.invsqrt(x * x + z * z) * 0.025d;
        double XOffset = x * normalizingFactorZPlane;
        double ZOffset = z * normalizingFactorZPlane;
        matrices.pushPose();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.beaconBeam(ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png"), true));
        Matrix4f matrix4f = matrices.last().pose();
        for (double l = 0; l <= 1; l += 1 / 24d) {
            renderLeashSegment(vertexConsumer, matrix4f, relativePos.scale(l), XOffset, 0.05d, ZOffset, light);
            renderLeashSegment(vertexConsumer, matrix4f, relativePos.scale(1 - l), XOffset, 0d, ZOffset, light);
        }
        matrices.popPose();
    }

    private static void renderLeashSegment(VertexConsumer vertexConsumer, Matrix4f matrix, Vec3 interpolatedRelativePos, double XOffset, double YOffset, double ZOffset, int light) {
        vertexConsumer
            .addVertex(matrix, (float) (interpolatedRelativePos.x - ZOffset), (float) (interpolatedRelativePos.y + YOffset), (float) (interpolatedRelativePos.z + XOffset))
            .setNormal(0, 0, 0)
            .setUv(4f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setColor(1, 1, 1, 1f)
            .setLight(light);
        vertexConsumer
            .addVertex(matrix, (float) (interpolatedRelativePos.x + ZOffset), (float) (interpolatedRelativePos.y + 0.05f - YOffset), (float) (interpolatedRelativePos.z - XOffset))
            .setNormal(0, 0, 0)
            .setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setColor(1, 1, 1, 1f)
            .setLight(light);
    }

    @Override
    public void extractRenderState(NeedleSwordEntity entity, NeedleSwordRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.needleSword = entity.getSword().transmuteCopy(ModItems.NEEDLE_HEAD, 1);
        state.ownerPos = Optional.ofNullable(entity.getOwner()).map(owner -> owner.getRopeHoldPosition(tickProgress));
        state.world = entity.level();
        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
        state.shouldDisplayString = entity.shouldReturn();
    }

    public static class NeedleSwordRenderState extends ArrowRenderState {
        public ItemStack needleSword;
        public Level world;
        public boolean shouldDisplayString;
        public Optional<Vec3> ownerPos;
    }
}
