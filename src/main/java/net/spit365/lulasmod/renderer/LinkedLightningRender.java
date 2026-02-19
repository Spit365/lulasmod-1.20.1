package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.util.MultiVec3d;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class LinkedLightningRender {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
    public static Set<MultiVec3d> linkedLightnings = new HashSet<>();

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register(LinkedLightningRender::render);
    }

    private static void render(WorldRenderContext context) {
        if (linkedLightnings.isEmpty()) return;

        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        MatrixStack matrices = context.matrixStack();
        Matrix4f matrix;
        if (matrices != null) matrix = matrices.peek().getPositionMatrix();
        else matrix = null;

        Camera camera = context.gameRenderer().getCamera();
        Vec3d cameraPos = camera.getPos();

        VertexConsumer vc = immediate.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));
        for (MultiVec3d link : linkedLightnings)
            link.pairwiseSegments().forEach(twoVec3d -> {
                Vec3d start = twoVec3d.start().subtract(cameraPos);
                Vec3d end = twoVec3d.end().subtract(cameraPos);
                Vec3d dir = end.subtract(start);

                int segments = Math.max(1, (int) (dir.length() / 4));
                Vec3d step = dir.multiply(1.0 / segments);

                Vec3d dirNorm = dir.normalize();
                Vec3d up = new Vec3d(0, 1, 0);
                Vec3d offset = up.subtract(dirNorm.multiply(up.dotProduct(dirNorm)));
                if (offset.lengthSquared() < 1e-6) {
                    offset = new Vec3d(1, 0, 0);
                }
                offset = offset.normalize();

                for (int j = 0; j < segments; j++) {
                    Vec3d p0 = start.add(step.multiply(j));
                    Vec3d p1 = start.add(step.multiply(j + 1));

                    addBillboardQuad(vc, matrix, p0, p1, offset, j);
                }
            });
        immediate.draw();
    }

    private static void addBillboardQuad(VertexConsumer vc, Matrix4f matrix, Vec3d p0, Vec3d p1, Vec3d offset, int segmentIndex) {
        float u1 = segmentIndex + 1;
        float v0 = 0;
        float v1 = 1;

        Vec3d p0a = p0.add(offset);
        Vec3d p0b = p0.subtract(offset);
        Vec3d p1a = p1.add(offset);
        Vec3d p1b = p1.subtract(offset);

        vc.vertex(matrix, (float) p0a.x, (float) p0a.y, (float) p0a.z).color(255, 255, 255, 255).texture(segmentIndex, v0).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0, 0xF000F0).normal(0, 1, 0);
        vc.vertex(matrix, (float) p0b.x, (float) p0b.y, (float) p0b.z).color(255, 255, 255, 255).texture(segmentIndex, v1).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0, 0xF000F0).normal(0, 1, 0);
        vc.vertex(matrix, (float) p1b.x, (float) p1b.y, (float) p1b.z).color(255, 255, 255, 255).texture(u1, v1).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0, 0xF000F0).normal(0, 1, 0);
        vc.vertex(matrix, (float) p1a.x, (float) p1a.y, (float) p1a.z).color(255, 255, 255, 255).texture(u1, v0).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0, 0xF000F0).normal(0, 1, 0);
    }
}
